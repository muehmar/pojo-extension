package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.SafeBuilderPojoField;

public class CompleteSafeBuilderFactory {
  private CompleteSafeBuilderFactory() {}

  public static Generator<Pojo, PojoSettings> completeSafeBuilder() {
    return NormalBuilderFactory.builderClass()
        .append(newLine())
        .append(newBuilderMethod())
        .append(newLine())
        .appendList(
            SafeBuilderFactory.fieldBuilderClass().append(newLine()),
            CompleteSafeBuilderFactory::requiredPojoFields)
        .append(SafeBuilderFactory.finalRequiredBuilder())
        .append(newLine())
        .appendList(
            SafeBuilderFactory.fieldBuilderClass().append(newLine()),
            CompleteSafeBuilderFactory::optionalPojoFields)
        .append(SafeBuilderFactory.finalOptionalBuilder());
  }

  private static PList<SafeBuilderPojoField> requiredPojoFields(Pojo pojo) {
    return pojo.getFields()
        .filter(PojoField::isRequired)
        .zipWithIndex()
        .map(p -> new SafeBuilderPojoField(p.first(), p.second()));
  }

  private static PList<SafeBuilderPojoField> optionalPojoFields(Pojo pojo) {
    return pojo.getFields()
        .filter(PojoField::isOptional)
        .zipWithIndex()
        .map(p -> new SafeBuilderPojoField(p.first(), p.second()));
  }

  private static Generator<Pojo, PojoSettings> newBuilderMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .returnType("Builder0")
        .methodName("newBuilder")
        .noArguments()
        .content("return new Builder0(new Builder());");
  }
}
