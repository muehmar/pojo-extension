package io.github.muehmar.pojoextension.annotations.processor;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Constructor;
import io.github.muehmar.pojoextension.generator.data.Name;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

public class ConstructorProcessor {
  private ConstructorProcessor() {}

  public static PList<Constructor> process(Element pojo) {

    return PList.fromIter(pojo.getEnclosedElements())
        .filter(e -> e.getKind().equals(ElementKind.CONSTRUCTOR) && e instanceof ExecutableElement)
        .map(ExecutableElement.class::cast)
        .map(
            e -> {
              final PList<Argument> arguments =
                  PList.fromIter(e.getParameters()).map(ConstructorProcessor::toArgument);
              return new Constructor(Name.fromString(pojo.getSimpleName().toString()), arguments);
            });
  }

  private static Argument toArgument(VariableElement el) {
    return new Argument(
        Name.fromString(el.getSimpleName().toString()), TypeMirrorMapper.map(el.asType()));
  }
}
