package io.github.muehmar.pojoextension.annotations.processor;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Type;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

public class GetterProcessor {
  private GetterProcessor() {}

  public static PList<Getter> process(Element pojo) {
    return PList.fromIter(pojo.getEnclosedElements())
        .filter(e -> e.getKind().equals(ElementKind.METHOD) && e instanceof ExecutableElement)
        .map(ExecutableElement.class::cast)
        .filter(e -> e.getParameters().isEmpty())
        .map(
            e -> {
              final Optional<Name> fieldName = extractFieldName(e);
              final Name methodName = Name.fromString(e.getSimpleName().toString());
              final Type returnType = TypeMirrorMapper.map(e.getReturnType());
              return Getter.newBuilder()
                  .setName(methodName)
                  .setReturnType(returnType)
                  .andAllOptionals()
                  .setFieldName(fieldName)
                  .build();
            })
        .filter(
            g ->
                g.getName().asString().startsWith("get")
                    || g.getName().asString().startsWith("is"));
  }

  private static Optional<Name> extractFieldName(ExecutableElement e) {
    return Optional.ofNullable(
            e.getAnnotation(io.github.muehmar.pojoextension.annotations.Getter.class))
        .map(io.github.muehmar.pojoextension.annotations.Getter::value)
        .filter(name -> name.trim().length() > 0)
        .map(Name::fromString);
  }
}
