package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;

public class SetMethodGen extends MethodGen<PojoField, PojoSettings> {
  public SetMethodGen() {
    super(
        (field, settings) ->
            JavaModifiers.of(field.isRequired() ? JavaModifier.PRIVATE : JavaModifier.PUBLIC),
        (field, settings) -> "Builder",
        (field, settings) -> String.format("set%s", field.getName().toPascalCase().asString()),
        (field, settings) ->
            PList.single(
                String.format(
                    "%s %s",
                    field.getType().getClassName().asString(), field.getName().asString())),
        (field, settings, writer) ->
            writer
                .println("this.%s = %s;", field.getName().asString(), field.getName().asString())
                .println("return this;"));
  }
}