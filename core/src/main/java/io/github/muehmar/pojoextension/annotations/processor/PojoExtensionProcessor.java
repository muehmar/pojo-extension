package io.github.muehmar.pojoextension.annotations.processor;

import ch.bluecare.commons.data.PList;
import com.google.auto.service.AutoService;
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

    final PList<PojoMember> members =
        PList.fromIter(element.getEnclosedElements())
            .filter(e -> e.getKind().equals(ElementKind.FIELD))
            .map(this::convertToPojoMember);

    final Pojo pojoExtension = new Pojo(extensionClassName, className, classPackage, members);
    final PojoSettings settings = new PojoSettings(false);

    redirectPojo.ifPresent(output -> output.accept(pojoExtension, settings));
    if (!redirectPojo.isPresent()) {
      final String generatedPojoExtension = generator.generate(pojoExtension, settings);
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

  private PojoMember convertToPojoMember(Element e) {
    final Name memberName = Name.fromString(e.getSimpleName().toString());
    final Type memberType = mapTypeMirrorToType(e.asType());

    return convertToPojoMember(memberName, memberType);
  }

  private PojoMember convertToPojoMember(Name name, Type type) {
    return PojoMemberMapper.initial()
        .or(this::mapOptionalPojoMember)
        .mapWithDefault(name, type, () -> new PojoMember(type, name, true));
  }

  private Optional<PojoMember> mapOptionalPojoMember(Name name, Type type) {
    if (type.equalsIgnoreTypeParameters(Type.optional(Type.string()))) {
      final PList<Type> typeParameters = type.getTypeParameters();
      if (typeParameters.size() != 1) {
        throw new IllegalStateException(
            "Optional must have exactly one type parameter but current type has '"
                + typeParameters.map(Type::getClassName).mkString(",")
                + "'");
      }
      final Type typeParameter = typeParameters.apply(0);
      return Optional.of(new PojoMember(typeParameter, name, false));
    } else {
      return Optional.empty();
    }
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
    Optional<PojoMember> map(Name name, Type type);

    default PojoMemberMapper or(PojoMemberMapper next) {
      final PojoMemberMapper self = this;
      return (name, type) -> {
        final Optional<PojoMember> result = self.map(name, type);
        return result.isPresent() ? result : next.map(name, type);
      };
    }

    default PojoMember mapWithDefault(Name name, Type type, Supplier<PojoMember> s) {
      return this.map(name, type).orElseGet(s);
    }

    static PojoMemberMapper initial() {
      return ((name, type) -> Optional.empty());
    }
  }
}
