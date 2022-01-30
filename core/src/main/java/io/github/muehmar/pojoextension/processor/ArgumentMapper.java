package io.github.muehmar.pojoextension.processor;

import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Name;
import javax.lang.model.element.VariableElement;

public class ArgumentMapper {
  private ArgumentMapper() {}

  public static Argument toArgument(VariableElement element) {
    return new Argument(
        Name.fromString(element.getSimpleName().toString()),
        TypeMirrorMapper.map(element.asType()));
  }
}
