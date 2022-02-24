package io.github.muehmar.pojoextension.processor;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Generic;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.type.Type;
import javax.lang.model.element.TypeElement;

/** Processes type variables of classes, this means only upper bounds are considered. */
public class ClassTypeVariableProcessor {
  private ClassTypeVariableProcessor() {}

  static PList<Generic> processGenerics(TypeElement element) {
    return PList.fromIter(element.getTypeParameters())
        .map(
            typeParameterElement -> {
              final PList<Type> upperBounds =
                  PList.fromIter(typeParameterElement.getBounds()).map(TypeMirrorMapper::map);
              return new Generic(Name.fromString(typeParameterElement.toString()), upperBounds);
            });
  }
}
