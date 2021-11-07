package io.github.muehmar.pojoextension.generator.impl.gen.instantiation;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.WRAP_INTO_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Updater;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.MatchingConstructor;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import java.util.Optional;

public class ConstructorCallGen {
  private ConstructorCallGen() {}

  /**
   * This generator creates a call to the constructor (return the created instance) of the pojo
   * while wrapping optional fields into an {@link Optional} depending on the argument of the
   * constructor.
   */
  public static Generator<Pojo, PojoSettings> constructorCall() {
    return (pojo, settings, writer) -> {
      final MatchingConstructor matchingConstructor =
          pojo.findMatchingConstructor()
              .orElseThrow(
                  () ->
                      new IllegalStateException(
                          "No matching constructor found for pojo " + pojo.getName()));

      final PList<String> constructorParameters =
          matchingConstructor
              .getFieldArguments()
              .map(
                  fieldArgument ->
                      fieldArgument
                          .getRelation()
                          .apply(
                              fieldArgument.getField(),
                              onUnwrapOptional(),
                              onSameType(),
                              onWrapOptional()));

      final boolean hasWrapIntoOptional =
          matchingConstructor
              .getFieldArguments()
              .exists(fa -> fa.getRelation().equals(WRAP_INTO_OPTIONAL));

      return Updater.initial(writer)
          .updateConditionally(hasWrapIntoOptional, w -> w.ref(JAVA_UTIL_OPTIONAL))
          .get()
          .println(
              "return new %s(%s);",
              matchingConstructor.getConstructor().getName(), constructorParameters.mkString(", "));
    };
  }

  private static OptionalFieldRelation.OnUnwrapOptional<PojoField, String> onUnwrapOptional() {
    return field -> String.format("%s.orElse(null)", field.getName());
  }

  private static OptionalFieldRelation.OnSameType<PojoField, String> onSameType() {
    return field -> field.getName().asString();
  }

  private static OptionalFieldRelation.OnWrapOptional<PojoField, String> onWrapOptional() {
    return field -> String.format("Optional.ofNullable(%s)", field.getName());
  }
}
