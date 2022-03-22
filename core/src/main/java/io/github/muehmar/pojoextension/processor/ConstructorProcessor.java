package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.Booleans.not;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Constructor;
import io.github.muehmar.pojoextension.generator.model.Name;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

public class ConstructorProcessor {
  private ConstructorProcessor() {}

  public static PList<Constructor> process(Element pojo) {

    return PList.fromIter(pojo.getEnclosedElements())
        .filter(e -> e.getKind().equals(ElementKind.CONSTRUCTOR) && e instanceof ExecutableElement)
        .map(ExecutableElement.class::cast)
        .filter(e -> not(e.getModifiers().contains(Modifier.PRIVATE)))
        .map(ExecutableElement::getParameters)
        .map(PList::fromIter)
        .map(params -> params.map(ArgumentMapper::toArgument))
        .map(
            arguments ->
                new Constructor(Name.fromString(pojo.getSimpleName().toString()), arguments));
  }
}
