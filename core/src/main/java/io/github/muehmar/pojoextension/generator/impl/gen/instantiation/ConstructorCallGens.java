package io.github.muehmar.pojoextension.generator.impl.gen.instantiation;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.WRAP_INTO_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Mapper;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.MatchingConstructor;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import java.util.Objects;
import java.util.Optional;

public class ConstructorCallGens {
  private ConstructorCallGens() {}

  /**
   * This generator creates a call to the constructor (return the created instance) of the pojo
   * while wrapping optional fields into an {@link Optional} depending on the argument of the
   * constructor.
   */
  public static Generator<Pojo, PojoSettings> callWithAllLocalVariables() {
    return Generator.<Pojo, PojoSettings>emptyGen()
        .append(
            constructorCallForFields(),
            pojo -> {
              final MatchingConstructor matchingConstructor = pojo.getMatchingConstructorOrThrow();

              final PList<FinalConstructorArgument> fields =
                  matchingConstructor
                      .getFieldArguments()
                      .map(
                          fa ->
                              FinalConstructorArgument.ofFieldVariable(
                                  new FieldVariable(pojo, fa.getField(), SAME_TYPE), fa));

              return new ConstructorCall(pojo, fields, matchingConstructor);
            });
  }

  public static Generator<FieldVariable, PojoSettings> callWithSingleFieldVariable() {
    return Generator.<FieldVariable, PojoSettings>emptyGen()
        .append(
            constructorCallForFields(),
            fieldVariable -> {
              final Pojo pojo = fieldVariable.getPojo();
              final MatchingConstructor matchingConstructor = pojo.getMatchingConstructorOrThrow();

              final PList<FinalConstructorArgument> fields =
                  matchingConstructor
                      .getFieldArguments()
                      .map(
                          fa -> {
                            final PojoField pojoField = fa.getField();
                            if (pojoField.equals(fieldVariable.getField())) {
                              return FinalConstructorArgument.ofFieldVariable(fieldVariable, fa);
                            } else {
                              final FieldGetter fieldGetter =
                                  pojo.getMatchingGetterOrThrow(pojoField);
                              return FinalConstructorArgument.ofGetter(fieldGetter, fa);
                            }
                          });

              return new ConstructorCall(pojo, fields, matchingConstructor);
            });
  }

  private static Generator<ConstructorCall, PojoSettings> constructorCallForFields() {
    return (constructorCall, settings, writer) -> {
      final PList<String> constructorParameters =
          constructorCall
              .getFields()
              .map(
                  finalConstructorArgument ->
                      finalConstructorArgument
                          .getRelation()
                          .apply(
                              finalConstructorArgument,
                              onUnwrapOptional(),
                              onSameType(),
                              onWrapOptional()));

      final boolean hasWrapIntoOptional =
          constructorCall
              .getFields()
              .map(FinalConstructorArgument::getRelation)
              .exists(relation -> relation.equals(WRAP_INTO_OPTIONAL));

      return Mapper.initial(writer)
          .mapConditionally(hasWrapIntoOptional, w -> w.ref(JAVA_UTIL_OPTIONAL))
          .apply()
          .println(
              "return new %s(%s);",
              constructorCall.getPojo().getName(), constructorParameters.mkString(", "));
    };
  }

  private static OptionalFieldRelation.OnUnwrapOptional<FinalConstructorArgument, String>
      onUnwrapOptional() {
    return finalConstructorArgument ->
        String.format("%s.orElse(null)", finalConstructorArgument.getFieldString());
  }

  private static OptionalFieldRelation.OnSameType<FinalConstructorArgument, String> onSameType() {
    return FinalConstructorArgument::getFieldString;
  }

  private static OptionalFieldRelation.OnWrapOptional<FinalConstructorArgument, String>
      onWrapOptional() {
    return finalConstructorArgument ->
        String.format("Optional.ofNullable(%s)", finalConstructorArgument.getFieldString());
  }

  private static class ConstructorCall {
    private final Pojo pojo;
    private final PList<FinalConstructorArgument> fields;
    private final MatchingConstructor constructor;

    public ConstructorCall(
        Pojo pojo, PList<FinalConstructorArgument> fields, MatchingConstructor constructor) {
      this.pojo = pojo;
      this.fields = fields;
      this.constructor = constructor;
    }

    public Pojo getPojo() {
      return pojo;
    }

    public PList<FinalConstructorArgument> getFields() {
      return fields;
    }

    public MatchingConstructor getMatchingConstructor() {
      return constructor;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ConstructorCall that = (ConstructorCall) o;
      return Objects.equals(pojo, that.pojo)
          && Objects.equals(fields, that.fields)
          && Objects.equals(constructor, that.constructor);
    }

    @Override
    public int hashCode() {
      return Objects.hash(pojo, fields, constructor);
    }

    @Override
    public String toString() {
      return "ConstructorCall{"
          + "pojo="
          + pojo
          + ", fields="
          + fields
          + ", constructor="
          + constructor
          + '}';
    }
  }
}
