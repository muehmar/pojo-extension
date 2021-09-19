package io.github.muehmar.pojoextension.annotations.processor;

import ch.bluecare.commons.data.PList;
import com.google.auto.service.AutoService;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.data.Name;
import io.github.muehmar.pojoextension.data.PackageName;
import io.github.muehmar.pojoextension.data.Pojo;
import io.github.muehmar.pojoextension.data.PojoMember;
import io.github.muehmar.pojoextension.data.Type;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.GeneratorFactory;
import io.github.muehmar.pojoextension.generator.PojoSettings;
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
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class PojoExtensionProcessor extends AbstractProcessor {

  private final Optional<BiConsumer<Pojo, PojoSettings>> redirectPojo;
  private final Generator generator = GeneratorFactory.create();

  public PojoExtensionProcessor() {
    redirectPojo = Optional.empty();
  }

  /**
   * Creates an annotation processor which does not actually produce output but redirects the
   * created {@link Pojo} and {@link PojoSettings} instances to the given consumer.
   */
  public PojoExtensionProcessor(BiConsumer<Pojo, PojoSettings> redirectPojo) {
    this.redirectPojo = Optional.of(redirectPojo);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final Set<? extends Element> elementsAnnotatedWith =
        roundEnv.getElementsAnnotatedWith(PojoExtension.class);

    elementsAnnotatedWith.forEach(this::processElement);

    return false;
  }

  private void processElement(Element element) {
    final Type pojoType = Type.fromQualifiedClassName(element.toString());
    final Name className = pojoType.getName();
    final Name extensionClassName = className.append("Extension");
    final PackageName classPackage = pojoType.getPackage();

    final PojoExtension annotation = element.getAnnotation(PojoExtension.class);
    final DetectionSettings detectionSettings =
        new DetectionSettings(PList.fromArray(annotation.optionalDetection()));

    final PList<PojoMember> members =
        PList.fromIter(element.getEnclosedElements())
            .filter(e -> e.getKind().equals(ElementKind.FIELD))
            .map(e -> convertToPojoMember(e, detectionSettings));

    final Pojo pojoExtension = new Pojo(extensionClassName, className, classPackage, members);
    final PojoSettings pojoSettings = new PojoSettings(false);

    redirectPojo.ifPresent(output -> output.accept(pojoExtension, pojoSettings));
    if (!redirectPojo.isPresent()) {
      final String generatedPojoExtension = generator.generate(pojoExtension, pojoSettings);
      try {
        JavaFileObject builderFile =
            processingEnv.getFiler().createSourceFile(pojoExtension.getName().asString());
        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
          out.println(generatedPojoExtension);
        }
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  private PojoMember convertToPojoMember(Element element, DetectionSettings settings) {
    final Name memberName = Name.fromString(element.getSimpleName().toString());
    final Type memberType = mapTypeMirrorToType(element.asType());

    return convertToPojoMember(element, memberName, memberType, settings);
  }

  private PojoMember convertToPojoMember(
      Element element, Name name, Type type, DetectionSettings settings) {
    return PojoMemberMapper.initial()
        .or(this::mapOptionalPojoMember)
        .or(this::mapNullablePojoMember)
        .mapWithDefault(element, name, type, settings, () -> new PojoMember(type, name, true));
  }

  private Optional<PojoMember> mapOptionalPojoMember(
      Element element, Name name, Type type, DetectionSettings settings) {
    return Optional.of(type)
        .filter(
            ignore ->
                settings.getOptionalDetections().exists(OptionalDetection.OPTIONAL_CLASS::equals))
        .filter(t -> t.equalsIgnoreTypeParameters(Type.optional(Type.string())))
        .map(Type::getTypeParameters)
        .filter(p -> p.size() == 1)
        .map(p -> p.apply(0))
        .map(typeParameter -> new PojoMember(typeParameter, name, false));
  }

  private Optional<PojoMember> mapNullablePojoMember(
      Element element, Name name, Type type, DetectionSettings settings) {
    return Optional.ofNullable(element.getAnnotation(Nullable.class))
        .filter(
            ignore ->
                settings
                    .getOptionalDetections()
                    .exists(OptionalDetection.NULLABLE_ANNOTATION::equals))
        .map(ignore -> new PojoMember(type, name, false));
  }

  private Type mapTypeMirrorToType(TypeMirror typeMirror) {
    if (typeMirror instanceof DeclaredType) {
      DeclaredType declaredType = ((DeclaredType) typeMirror);
      final PList<Type> typeParameters =
          PList.fromIter(declaredType.getTypeArguments()).map(this::mapTypeMirrorToType);

      return Type.fromQualifiedClassName(declaredType.toString())
          .withTypeParameters(typeParameters);
    }
    throw new IllegalArgumentException("TypeMirror is not a declared-type " + typeMirror.getKind());
  }

  @FunctionalInterface
  private interface PojoMemberMapper {
    Optional<PojoMember> map(
        Element element, Name name, Type type, DetectionSettings detectionSettings);

    default PojoMemberMapper or(PojoMemberMapper next) {
      final PojoMemberMapper self = this;
      return (element, name, type, settings) -> {
        final Optional<PojoMember> result = self.map(element, name, type, settings);
        return result.isPresent() ? result : next.map(element, name, type, settings);
      };
    }

    default PojoMember mapWithDefault(
        Element element, Name name, Type type, DetectionSettings settings, Supplier<PojoMember> s) {
      return this.map(element, name, type, settings).orElseGet(s);
    }

    static PojoMemberMapper initial() {
      return ((element, name, type, settings) -> Optional.empty());
    }
  }
}
