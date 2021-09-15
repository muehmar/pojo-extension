package io.github.muehmar.pojoextension.annotations.processor;

import ch.bluecare.commons.data.PList;
import com.google.auto.service.AutoService;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.data.Name;
import io.github.muehmar.pojoextension.data.PackageName;
import io.github.muehmar.pojoextension.data.Pojo;
import io.github.muehmar.pojoextension.data.PojoMember;
import io.github.muehmar.pojoextension.data.Type;
import io.github.muehmar.pojoextension.generator.JavaGenerator;
import io.github.muehmar.pojoextension.generator.JavaResolver;
import io.github.muehmar.pojoextension.generator.PojoSettings;
import io.github.muehmar.pojoextension.generator.WriterImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Set;
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
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class PojoExtensionProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      return processThrowing(annotations, roundEnv);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private boolean processThrowing(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
      throws IOException {
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
            .map(
                e -> {
                  final Name memberName = Name.fromString(e.getSimpleName().toString());
                  final Type memberType = mapTypeMirrorToType(e.asType());

                  return new PojoMember(memberType, memberName, true);
                });

    final Pojo pojoExtension = new Pojo(extensionClassName, className, classPackage, members);

    final JavaGenerator javaGenerator = new JavaGenerator(new JavaResolver());
    final WriterImpl writer = new WriterImpl();
    javaGenerator.generate(writer, pojoExtension, new PojoSettings(false));

    try {
      JavaFileObject builderFile =
          processingEnv.getFiler().createSourceFile(extensionClassName.asString());
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.println(writer.asString());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private Type mapTypeMirrorToType(TypeMirror typeMirror) {
    if (typeMirror instanceof DeclaredType) {
      DeclaredType declaredType = ((DeclaredType) typeMirror);
      final PList<Type> generics =
          PList.fromIter(declaredType.getTypeArguments()).map(this::mapTypeMirrorToType);

      return Type.fromQualifiedClassName(typeMirror.toString()).withGenerics(generics);
    }
    throw new IllegalArgumentException("TypeMirror is not a declared-type " + typeMirror.getKind());
  }
}
