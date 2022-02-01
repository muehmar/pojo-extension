package io.github.muehmar.pojoextension.generator.impl.gen.getter;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.DEFAULT;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.OnSameType;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.OnUnwrapOptional;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.OnWrapOptional;
import io.github.muehmar.pojoextension.generator.data.PojoAndField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;
import io.github.muehmar.pojoextension.generator.impl.gen.Refs;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class GetterGens {

  private GetterGens() {}

  public static Generator<PojoAndField, PojoSettings> optionalGetterMethod() {
    return MethodGenBuilder.<PojoAndField, PojoSettings>create()
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
