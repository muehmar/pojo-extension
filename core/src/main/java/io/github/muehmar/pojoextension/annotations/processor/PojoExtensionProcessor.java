package io.github.muehmar.pojoextension.annotations.processor;

import com.google.auto.service.AutoService;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
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
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class PojoExtensionProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final Set<? extends Element> elementsAnnotatedWith =
        roundEnv.getElementsAnnotatedWith(PojoExtension.class);

    elementsAnnotatedWith.forEach(
        element -> {
          final String className = element.toString();
          String packageName = null;
          int lastDot = className.lastIndexOf('.');
          if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
          }

          try {
            JavaFileObject builderFile =
                processingEnv.getFiler().createSourceFile(className + "Extension");
            try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
              out.println(
                  "package "
                      + packageName
                      + ";\n\npublic class "
                      + className.substring(lastDot + 1)
                      + "Extension"
                      + " {\n}");
            }
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        });
    return false;
  }
}
