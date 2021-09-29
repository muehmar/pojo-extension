package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenerator;

public class SetMethodGenerator extends MethodGenerator<PojoField, PojoSettings> {
  public SetMethodGenerator(Generator<PojoField, PojoSettings> contentGenerator) {
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
        contentGenerator);
  }
}
