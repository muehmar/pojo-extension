package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.Booleans.not;
import static io.github.muehmar.pojoextension.generator.model.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.model.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.model.settings.ExtensionUsage.INHERITED;
import static io.github.muehmar.pojoextension.generator.model.settings.ExtensionUsage.STATIC;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getBuilderName;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getBuilderSetMethodPrefix;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getEnableMappers;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getEnableOptionalGetters;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getEnableSafeBuilder;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getEnableWithers;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getExtensionName;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getOptionalDetection;
import static io.github.muehmar.pojoextension.processor.AnnotationMemberExtractor.getPackagePrivateBuilder;

import ch.bluecare.commons.data.PList;
import com.google.auto.service.AutoService;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.Optionals;
import io.github.muehmar.pojoextension.Strings;
import io.github.muehmar.pojoextension.annotations.Ignore;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.impl.gen.extension.ExtensionGens;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.SafeBuilderClassGens;
import io.github.muehmar.pojoextension.generator.model.BuildMethod;
import io.github.muehmar.pojoextension.generator.model.ClassAccessLevelModifier;
import io.github.muehmar.pojoextension.generator.model.Constructor;
import io.github.muehmar.pojoextension.generator.model.FieldBuilder;
import io.github.muehmar.pojoextension.generator.model.Generic;
import io.github.muehmar.pojoextension.generator.model.Getter;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.PackageName;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoBuilder;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.model.settings.Ability;
import io.github.muehmar.pojoextension.generator.model.settings.ExtensionUsage;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.model.type.ClassnameParser;
import io.github.muehmar.pojoextension.generator.model.type.DeclaredType;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("*")
@AutoService(Processor.class)
public class PojoExtensionProcessor extends AbstractProcessor {
  private static final int MAX_ANNOTATION_PATH_DEPTH = 50;

  private final Optional<BiConsumer<Pojo, PojoSettings>> redirectPojo;

  private PojoExtensionProcessor(Optional<BiConsumer<Pojo, PojoSettings>> redirectPojo) {
    this.redirectPojo = redirectPojo;
  }

  public PojoExtensionProcessor() {
    this(Optional.empty());
  }

  /**
   * Creates an annotation processor which does not actually produce output but redirects the
   * created {@link Pojo} and {@link PojoSettings} instances to the given consumer.
   */
  public PojoExtensionProcessor(BiConsumer<Pojo, PojoSettings> redirectPojo) {
    this(Optional.of(redirectPojo));
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    PList.fromIter(annotations)
        .flatMap(roundEnv::getElementsAnnotatedWith)
        .filter(this::isClassOrRecord)
        .filter(TypeElement.class::isInstance)
        .map(TypeElement.class::cast)
        .distinct(Object::toString)
        .flatMapOptional(this::findAnnotationPath)
        .forEach(this::processElementAndPath);

    return false;
  }

  private boolean isClassOrRecord(Element e) {
    return e.getKind().equals(ElementKind.CLASS) || e.getKind().name().equalsIgnoreCase("Record");
  }

  private void processElementAndPath(ElementAndAnnotationPath elementAndPath) {
    final PojoSettings pojoSettings = extractSettingsFromAnnotationPath(elementAndPath.getPath());
    final TypeElement classElement = elementAndPath.getClassElement();
    final String fullClassName = classElement.toString();

    final ClassnameParser.NameAndPackage nameAndPackage =
        ClassnameParser.parseThrowing(fullClassName);

    final PackageName classPackage =
        nameAndPackage
            .getPkg()
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Class " + fullClassName + " does not have a package."));

    final Pojo pojo =
        extractPojo(
            classElement, pojoSettings, nameAndPackage.getClassname().asName(), classPackage);

    outputPojo(pojo, deviateExtensionUsage(classElement, pojoSettings, pojo));
  }

  private Optional<ElementAndAnnotationPath> findAnnotationPath(TypeElement element) {
    return Optional.of(findAnnotationPath(element, PList.empty()))
        .filter(PList::nonEmpty)
        .map(path -> new ElementAndAnnotationPath(element, path));
  }

  private PList<AnnotationMirror> findAnnotationPath(
      Element currentElement, PList<AnnotationMirror> currentPath) {
    if (currentPath.size() >= MAX_ANNOTATION_PATH_DEPTH) {
      return PList.empty();
    }

    final PList<AnnotationMirror> annotationMirrors =
        PList.fromIter(currentElement.getAnnotationMirrors()).map(a -> a);
    final Optional<AnnotationMirror> pojoExtension =
        annotationMirrors.find(
            a ->
                a.getAnnotationType()
                    .asElement()
                    .asType()
                    .toString()
                    .equals(PojoExtension.class.getName()));

    return pojoExtension
        .map(currentPath::cons)
        .orElseGet(
            () ->
                annotationMirrors
                    .filter(a -> not(currentPath.exists(a::equals)))
                    .map(
                        a ->
                            findAnnotationPath(
                                a.getAnnotationType().asElement(), currentPath.cons(a)))
                    .find(PList::nonEmpty)
                    .orElse(PList.empty()));
  }

  private Pojo extractPojo(
      TypeElement element, PojoSettings settings, Name className, PackageName classPackage) {
    final DetectionSettings detectionSettings =
        new DetectionSettings(settings.getOptionalDetections());

    final PList<Constructor> constructors = ConstructorProcessor.process(element);
    final PList<Getter> getters = GetterProcessor.process(element);
    final PList<Generic> generics = ClassTypeVariableProcessor.processGenerics(element);
    final PList<FieldBuilder> fieldBuilders = FieldBuilderProcessor.process(element);
    final Optional<BuildMethod> buildMethod = BuildMethodProcessor.process(element);

    final PList<PojoField> fields =
        PList.fromIter(element.getEnclosedElements())
            .filter(e -> e.getKind().equals(ElementKind.FIELD))
            .filter(this::isNonConstantField)
            .filter(this::isNotIgnoredField)
            .map(e -> convertToPojoField(e, detectionSettings));

    return PojoBuilder.create()
        .name(className)
        .pkg(classPackage)
        .fields(fields)
        .constructors(constructors)
        .getters(getters)
        .generics(generics)
        .fieldBuilders(fieldBuilders)
        .andAllOptionals()
        .buildMethod(buildMethod)
        .build();
  }

  private PojoSettings deviateExtensionUsage(
      TypeElement element, PojoSettings settings, Pojo pojo) {

    final String superclassString = element.getSuperclass().toString();
    final String unqualifiedExtensionName = settings.extensionName(pojo).asString();
    final String qualifiedExtensionName = settings.qualifiedExtensionName(pojo).asString();
    final ExtensionUsage extensionUsage =
        superclassString.equals(unqualifiedExtensionName)
                || superclassString.equals(qualifiedExtensionName)
            ? INHERITED
            : STATIC;

    return settings.withExtensionUsage(extensionUsage);
  }

  private PojoSettings extractSettingsFromAnnotationPath(PList<AnnotationMirror> annotations) {
    return extractSettingsFromAnnotationPath(annotations, PojoSettings.defaultSettings());
  }

  private PojoSettings extractSettingsFromAnnotationPath(
      PList<AnnotationMirror> annotations, PojoSettings currentSettings) {
    return annotations
        .headOption()
        .map(
            a ->
                extractSettingsFromAnnotationPath(
                    annotations.tail(), overrideWithAnnotationValues(a, currentSettings)))
        .orElse(currentSettings);
  }

  private PojoSettings overrideWithAnnotationValues(
      AnnotationMirror annotation, PojoSettings currentSettings) {
    return currentSettings
        .mapIfPresent(getOptionalDetection(annotation), PojoSettings::withOptionalDetections)
        .mapIfPresent(
            getExtensionName(annotation)
                .map(String::trim)
                .filter(Strings::nonEmpty)
                .map(Name::fromString),
            PojoSettings::withExtensionName)
        .mapIfPresent(
            getBuilderName(annotation)
                .map(String::trim)
                .filter(Strings::nonEmpty)
                .map(Name::fromString),
            PojoSettings::withBuilderName)
        .mapIfPresent(
            getBuilderSetMethodPrefix(annotation)
                .map(String::trim)
                .filter(Strings::nonEmpty)
                .map(Name::fromString),
            PojoSettings::withBuilderSetMethodPrefix)
        .mapIfPresent(
            getEnableSafeBuilder(annotation).map(Ability::fromBoolean),
            PojoSettings::withSafeBuilderAbility)
        .mapIfPresent(
            getPackagePrivateBuilder(annotation)
                .map(this::classAccessLevelModifierFromIsPackagePrivateFlag),
            PojoSettings::withBuilderAccessLevel)
        .mapIfPresent(
            getEnableWithers(annotation).map(Ability::fromBoolean),
            PojoSettings::withWithersAbility)
        .mapIfPresent(
            getEnableOptionalGetters(annotation).map(Ability::fromBoolean),
            PojoSettings::withOptionalGettersAbility)
        .mapIfPresent(
            getEnableMappers(annotation).map(Ability::fromBoolean),
            PojoSettings::withMappersAbility);
  }

  private ClassAccessLevelModifier classAccessLevelModifierFromIsPackagePrivateFlag(
      boolean isPackagePrivate) {
    return isPackagePrivate
        ? ClassAccessLevelModifier.PACKAGE_PRIVATE
        : ClassAccessLevelModifier.PUBLIC;
  }

  private void outputPojo(Pojo pojo, PojoSettings pojoSettings) {
    Optionals.ifPresentOrElse(
        redirectPojo,
        output -> output.accept(pojo, pojoSettings),
        () -> writeExtensionClass(pojo, pojoSettings));
  }

  private void writeExtensionClass(Pojo pojo, PojoSettings settings) {
    writeJavaFile(
        settings.qualifiedExtensionName(pojo),
        ExtensionGens.extensionInterface(),
        pojo,
        settings,
        settings.createExtension());
    writeJavaFile(
        settings.qualifiedBuilderName(pojo),
        SafeBuilderClassGens.safeBuilderClass(),
        pojo,
        settings,
        settings.createDiscreteBuilder());
  }

  private void writeJavaFile(
      Name qualifiedClassName,
      Generator<Pojo, PojoSettings> gen,
      Pojo pojo,
      PojoSettings pojoSettings,
      boolean createFile) {
    if (not(createFile)) {
      return;
    }
    final String javaContent = gen.generate(pojo, pojoSettings, Writer.createDefault()).asString();
    try {
      final JavaFileObject builderFile =
          processingEnv.getFiler().createSourceFile(qualifiedClassName.asString());
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.println(javaContent);
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private boolean isNonConstantField(Element element) {
    final Set<Modifier> modifiers = element.getModifiers();
    return !modifiers.contains(Modifier.STATIC);
  }

  private boolean isNotIgnoredField(Element element) {
    final Optional<Ignore> annotation = Optional.ofNullable(element.getAnnotation(Ignore.class));
    return not(annotation.isPresent());
  }

  private PojoField convertToPojoField(Element element, DetectionSettings settings) {
    final Name fieldName = Name.fromString(element.getSimpleName().toString());
    final Type fieldType = TypeMirrorMapper.map(element.asType());

    return convertToPojoField(element, fieldName, fieldType, settings);
  }

  private PojoField convertToPojoField(
      Element element, Name name, Type type, DetectionSettings settings) {
    return PojoFieldMapper.initial()
        .or(this::mapOptionalPojoField)
        .or(this::mapNullablePojoField)
        .mapWithDefault(element, name, type, settings, () -> new PojoField(name, type, REQUIRED));
  }

  private Optional<PojoField> mapOptionalPojoField(
      Element element, Name name, Type type, DetectionSettings settings) {
    return Optional.of(type)
        .filter(
            ignore ->
                settings.getOptionalDetections().exists(OptionalDetection.OPTIONAL_CLASS::equals))
        .flatMap(this::getOptionalValueType)
        .map(typeParameter -> new PojoField(name, typeParameter, OPTIONAL));
  }

  private Optional<Type> getOptionalValueType(Type type) {
    final Function<DeclaredType, Optional<Type>> getOptionalType =
        classType ->
            Optional.of(classType)
                .filter(DeclaredType::isOptional)
                .flatMap(t -> t.getTypeParameters().headOption());
    return type.onDeclaredType(getOptionalType).flatMap(Function.identity());
  }

  private Optional<PojoField> mapNullablePojoField(
      Element element, Name name, Type type, DetectionSettings settings) {
    return Optional.ofNullable(element.getAnnotation(Nullable.class))
        .filter(
            ignore ->
                settings
                    .getOptionalDetections()
                    .exists(OptionalDetection.NULLABLE_ANNOTATION::equals))
        .map(ignore -> new PojoField(name, type, OPTIONAL));
  }

  @FunctionalInterface
  private interface PojoFieldMapper {
    Optional<PojoField> map(
        Element element, Name name, Type type, DetectionSettings detectionSettings);

    default PojoFieldMapper or(PojoFieldMapper next) {
      final PojoFieldMapper self = this;
      return (element, name, type, settings) -> {
        final Optional<PojoField> result = self.map(element, name, type, settings);
        return result.isPresent() ? result : next.map(element, name, type, settings);
      };
    }

    default PojoField mapWithDefault(
        Element element, Name name, Type type, DetectionSettings settings, Supplier<PojoField> s) {
      return this.map(element, name, type, settings).orElseGet(s);
    }

    static PojoFieldMapper initial() {
      return ((element, name, type, settings) -> Optional.empty());
    }
  }

  private static class ElementAndAnnotationPath {
    private final TypeElement classElement;
    private final PList<AnnotationMirror> path;

    public ElementAndAnnotationPath(TypeElement classElement, PList<AnnotationMirror> path) {
      this.classElement = classElement;
      this.path = path;
    }

    public static ElementAndAnnotationPath of(TypeElement element, PList<AnnotationMirror> path) {
      return new ElementAndAnnotationPath(element, path);
    }

    public TypeElement getClassElement() {
      return classElement;
    }

    public PList<AnnotationMirror> getPath() {
      return path;
    }
  }
}
