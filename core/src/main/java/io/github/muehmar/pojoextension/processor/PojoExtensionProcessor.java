package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.Booleans.not;
import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.settings.ExtensionUsage.INHERITED;
import static io.github.muehmar.pojoextension.generator.data.settings.ExtensionUsage.STATIC;

import ch.bluecare.commons.data.PList;
import com.google.auto.service.AutoService;
import io.github.muehmar.pojoextension.Optionals;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Constructor;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.data.settings.Ability;
import io.github.muehmar.pojoextension.generator.data.settings.DiscreteBuilder;
import io.github.muehmar.pojoextension.generator.data.settings.ExtensionUsage;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.extension.ExtensionGens;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.SafeBuilderClassGens;
import io.github.muehmar.pojoextension.generator.writer.Writer;
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
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class PojoExtensionProcessor extends AbstractProcessor {

  private final Optional<BiConsumer<Pojo, PojoSettings>> redirectPojo;
  private final Generator<Pojo, PojoSettings> extensionGenerator = ExtensionGens.extensionClass();
  private final Generator<Pojo, PojoSettings> builderGenerator =
      SafeBuilderClassGens.safeBuilderClass();

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
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final Set<? extends Element> annotatedElements =
        roundEnv.getElementsAnnotatedWith(PojoExtension.class);

    PList.fromIter(annotatedElements)
        .flatMap(
            annotatedElement ->
                findAnnotatedClasses(
                    roundEnv,
                    annotatedElement,
                    annotatedElement.getAnnotation(PojoExtension.class)))
        .forEach(this::processElementAndAnnotation);

    return false;
  }

  private PList<ElementAndAnnotation> findAnnotatedClasses(
      RoundEnvironment roundEnv, Element annotatedElement, PojoExtension extension) {
    if (not(annotatedElement instanceof TypeElement)) {
      return PList.empty();
    }

    final TypeElement typeElement = (TypeElement) annotatedElement;

    if (annotatedElement.getKind().equals(ElementKind.CLASS)) {
      return PList.single(ElementAndAnnotation.of(typeElement, extension));
    } else if (annotatedElement.getKind().equals(ElementKind.ANNOTATION_TYPE)) {
      return PList.fromIter(roundEnv.getElementsAnnotatedWith(typeElement))
          .flatMap(e -> findAnnotatedClasses(roundEnv, e, extension));
    } else {
      return PList.empty();
    }
  }

  private void processElementAndAnnotation(ElementAndAnnotation elementAndAnnotation) {
    final TypeElement element = elementAndAnnotation.getElement();
    final PojoExtension annotation = elementAndAnnotation.getPojoExtension();
    final Type pojoType = Type.fromClassName(element.toString());
    final Name className = pojoType.getName();
    final PackageName classPackage = pojoType.getPackage();

    final Pojo pojo = extractPojo(element, annotation, className, classPackage);
    final PojoSettings pojoSettings = extractSettings(element, annotation, pojo);

    outputPojo(pojo, pojoSettings);
  }

  private Pojo extractPojo(
      TypeElement element, PojoExtension annotation, Name className, PackageName classPackage) {
    final DetectionSettings detectionSettings =
        new DetectionSettings(PList.fromArray(annotation.optionalDetection()));

    final PList<Constructor> constructors = ConstructorProcessor.process(element);
    final PList<Getter> getters = GetterProcessor.process(element);

    final PList<PojoField> fields =
        PList.fromIter(element.getEnclosedElements())
            .filter(e -> e.getKind().equals(ElementKind.FIELD))
            .filter(this::isNonConstantField)
            .map(e -> convertToPojoField(e, detectionSettings));

    return Pojo.newBuilder()
        .setName(className)
        .setPkg(classPackage)
        .setFields(fields)
        .setConstructors(constructors)
        .setGetters(getters)
        .andAllOptionals()
        .build();
  }

  private PojoSettings extractSettings(TypeElement element, PojoExtension annotation, Pojo pojo) {
    final Optional<Name> extensionName =
        Optional.ofNullable(annotation.extensionName())
            .filter(n -> n.trim().length() > 0)
            .map(Name::fromString);
    final Optional<Name> builderName =
        Optional.ofNullable(annotation.builderName())
            .filter(n -> n.trim().length() > 0)
            .map(Name::fromString);

    final PojoSettings settings =
        PojoSettings.newBuilder()
            .setExtensionUsage(INHERITED)
            .setSafeBuilderAbility(Ability.fromBoolean(annotation.enableSafeBuilder()))
            .setDiscreteBuilder(DiscreteBuilder.fromBoolean(annotation.discreteBuilder()))
            .setEqualsHashCodeAbility(Ability.fromBoolean(annotation.enableEqualsAndHashCode()))
            .setToStringAbility(Ability.fromBoolean(annotation.enableToString()))
            .setWithersAbility(Ability.fromBoolean(annotation.enableWithers()))
            .setMappersAbility(Ability.fromBoolean(annotation.enableMappers()))
            .andAllOptionals()
            .setExtensionName(extensionName)
            .setBuilderName(builderName)
            .build();

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

  private void outputPojo(Pojo pojo, PojoSettings pojoSettings) {
    Optionals.ifPresentOrElse(
        redirectPojo,
        output -> output.accept(pojo, pojoSettings),
        () -> writeExtensionClass(pojo, pojoSettings));
  }

  private void writeExtensionClass(Pojo pojo, PojoSettings settings) {
    writeJavaFile(
        settings.qualifiedExtensionName(pojo),
        extensionGenerator,
        pojo,
        settings,
        settings.createExtension());
    writeJavaFile(
        settings.qualifiedBuilderName(pojo),
        builderGenerator,
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
        .flatMap(t -> t.onOptional(Function.identity()))
        .map(typeParameter -> new PojoField(name, typeParameter, OPTIONAL));
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

  private static class ElementAndAnnotation {
    private final TypeElement element;
    private final PojoExtension pojoExtension;

    public ElementAndAnnotation(TypeElement element, PojoExtension pojoExtension) {
      this.element = element;
      this.pojoExtension = pojoExtension;
    }

    public static ElementAndAnnotation of(TypeElement element, PojoExtension extension) {
      return new ElementAndAnnotation(element, extension);
    }

    public TypeElement getElement() {
      return element;
    }

    public PojoExtension getPojoExtension() {
      return pojoExtension;
    }
  }
}
