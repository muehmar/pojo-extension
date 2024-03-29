package io.github.muehmar.pojoextension.generator.impl.gen.getter;

import static io.github.muehmar.codegenerator.java.JavaModifier.DEFAULT;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.impl.gen.Refs;
import io.github.muehmar.pojoextension.generator.model.FieldGetter;
import io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation;
import io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.OnSameType;
import io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.OnUnwrapOptional;
import io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.OnWrapOptional;
import io.github.muehmar.pojoextension.generator.model.PojoAndField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;

public class GetterGens {

  private GetterGens() {}

  public static Generator<PojoAndField, PojoSettings> optionalGetterMethod() {
    return JavaGenerators.<PojoAndField, PojoSettings>methodGen()
        .modifiers(DEFAULT)
        .noGenericTypes()
        .returnTypeName(paf -> paf.getField().getType().getTypeDeclaration())
        .methodName(GetterGens::optionalGetterMethodName)
        .singleArgument(
            paf ->
                String.format(
                    "%s %s",
                    paf.getField().getType().getTypeDeclaration(), paf.getField().getName()))
        .content(optionalGetterMethodContent())
        .build()
        .filter((paf, s) -> paf.getField().isOptional())
        .filter((paf, s) -> s.getOptionalGettersAbility().isEnabled());
  }

  private static String optionalGetterMethodName(PojoAndField pojoAndField) {
    final FieldGetter fieldGetter = pojoAndField.getMatchingGetterOrThrow();
    return fieldGetter.getGetter().getName().append("Or").asString();
  }

  private static Generator<PojoAndField, PojoSettings> optionalGetterMethodContent() {
    return (paf, s, w) -> {
      final OnUnwrapOptional<FieldGetter, Writer> onUnwrapOptional =
          fg -> {
            throw new IllegalArgumentException("Should not occur when wrapping into optional;");
          };
      final OnSameType<FieldGetter, Writer> onSameType =
          fg ->
              w.println(
                  "return %s().orElse(%s);", fg.getGetter().getName(), fg.getField().getName());
      final OnWrapOptional<FieldGetter, Writer> onWrapOptional =
          fg ->
              w.println(
                      "return Optional.ofNullable(%s()).orElse(%s);",
                      fg.getGetter().getName(), fg.getField().getName())
                  .ref(Refs.JAVA_UTIL_OPTIONAL);

      final FieldGetter fieldGetter = paf.getMatchingGetterOrThrow();
      return fieldGetter
          .getRelation()
          .andThen(OptionalFieldRelation.WRAP_INTO_OPTIONAL)
          .apply(fieldGetter, onUnwrapOptional, onSameType, onWrapOptional);
    };
  }
}
