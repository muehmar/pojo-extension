package io.github.muehmar.pojoextension.annotations.processor;

import static io.github.muehmar.pojoextension.Names.extensionSuffix;

import ch.bluecare.commons.data.PList;
import com.google.auto.service.AutoService;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Constructor;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.impl.gen.extension.ExtensionFactory;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class PojoExtensionProcessor extends AbstractProcessor {

  private final Optional<BiConsumer<Pojo, PojoSettings>> redirectPojo;
  private final Generator<Pojo, PojoSettings> generator = ExtensionFactory.extensionClass();

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
    final Set<? extends Element> elementsAnnotatedWith =
        roundEnv.getElementsAnnotatedWith(PojoExtension.class);

    elementsAnnotatedWith.forEach(this::processElement);

    return false;
  }

  private void processElement(Element element) {
    final Type pojoType = Type.fromClassName(element.toString());
    final Name className = pojoType.getName();
    final PackageName classPackage = pojoType.getPackage();

    final PojoExtension annotation = element.getAnnotation(PojoExtension.class);
    final DetectionSettings detectionSettings =
        new DetectionSettings(PList.fromArray(annotation.optionalDetection()));

    final PList<Constructor> constructors = ConstructorProcessor.process(element);

    final PList<PojoField> fields =
        PList.fromIter(element.getEnclosedElements())
            .filter(e -> e.getKind().equals(ElementKind.FIELD))
            .map(e -> convertToPojoField(e, detectionSettings));

    final Pojo pojoExtension =
        Pojo.newBuilder()
            .setName(className)
            .setPkg(classPackage)
            .setFields(fields)
            .setConstructors(constructors)
            .andAllOptionals()
            .build();
    final PojoSettings pojoSettings = new PojoSettings(false);

    redirectPojo.ifPresent(output -> output.accept(pojoExtension, pojoSettings));
    if (!redirectPojo.isPresent()) {
      final String generatedPojoExtension =
          generator.generate(pojoExtension, pojoSettings, Writer.createDefault()).asString();
      try {
        final Name qualifiedExtensionName = pojoExtension.qualifiedName().append(extensionSuffix());
        JavaFileObject builderFile =
            processingEnv.getFiler().createSourceFile(qualifiedExtensionName.asString());
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
          out.println(generatedPojoExtension);
        }
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
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
        .mapWithDefault(element, name, type, settings, () -> new PojoField(type, name, true));
  }

  private Optional<PojoField> mapOptionalPojoField(
      Element element, Name name, Type type, DetectionSettings settings) {
    return Optional.of(type)
        .filter(
            ignore ->
                settings.getOptionalDetections().exists(OptionalDetection.OPTIONAL_CLASS::equals))
        .filter(t -> t.equalsIgnoreTypeParameters(Type.optional(Type.string())))
        .map(Type::getTypeParameters)
        .filter(p -> p.size() == 1)
        .map(p -> p.apply(0))
        .map(typeParameter -> new PojoField(typeParameter, name, false));
  }

  private Optional<PojoField> mapNullablePojoField(
      Element element, Name name, Type type, DetectionSettings settings) {
    return Optional.ofNullable(element.getAnnotation(Nullable.class))
        .filter(
            ignore ->
                settings
                    .getOptionalDetections()
                    .exists(OptionalDetection.NULLABLE_ANNOTATION::equals))
        .map(ignore -> new PojoField(type, name, false));
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
}
