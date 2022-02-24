package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.Booleans.not;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.GetterBuilder;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.type.Type;
import io.github.muehmar.pojoextension.generator.data.type.Types;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

public class GetterProcessor {
  private GetterProcessor() {}

  public static PList<Getter> process(Element pojo) {
    return PList.fromIter(pojo.getEnclosedElements())
        .filter(e -> e.getKind().equals(ElementKind.METHOD))
        .filter(ExecutableElement.class::isInstance)
        .map(ExecutableElement.class::cast)
        .filter(e -> e.getParameters().isEmpty())
        .map(GetterProcessor::mapToGetter)
        .filter(g -> not(g.getReturnType().equals(Types.voidType())));
  }

  private static Getter mapToGetter(ExecutableElement e) {
    final Optional<Name> fieldName = extractFieldName(e);
    final Name methodName = Name.fromString(e.getSimpleName().toString());
    final Type returnType = TypeMirrorMapper.map(e.getReturnType());
    return GetterBuilder.create()
        .name(methodName)
        .returnType(returnType)
        .andAllOptionals()
        .fieldName(fieldName)
        .build();
  }

  private static Optional<Name> extractFieldName(ExecutableElement e) {
    return Optional.ofNullable(
            e.getAnnotation(io.github.muehmar.pojoextension.annotations.Getter.class))
        .map(io.github.muehmar.pojoextension.annotations.Getter::value)
        .filter(name -> name.trim().length() > 0)
        .map(Name::fromString);
  }
}
