package io.github.muehmar.pojoextension.generator;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Constructor;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;

public class Pojos {
  public static final PackageName PACKAGE_NAME = PackageName.fromString("io.github.muehmar");

  private Pojos() {}

  public static Pojo sample() {
    final PList<PojoField> fields =
        PList.of(
            new PojoField(Name.fromString("id"), Type.integer(), true),
            new PojoField(Name.fromString("username"), Type.string(), true),
            new PojoField(Name.fromString("nickname"), Type.string(), false));

    final PList<Getter> getters =
        fields.map(
            f -> {
              final Type returnType =
                  f.isOptional() && !f.getType().isOptional()
                      ? Type.optional(f.getType())
                      : f.getType();
              return Getter.newBuilder()
                  .setName(Getter.getterName(f))
                  .setReturnType(returnType)
                  .build();
            });

    final Pojo pojo =
        Pojo.newBuilder()
            .setName(Name.fromString("Customer"))
            .setPkg(PACKAGE_NAME)
            .setFields(fields)
            .setConstructors(PList.empty())
            .setGetters(getters)
            .andAllOptionals()
            .build();
    return pojo.withConstructors(PList.single(deviateStandardConstructor(pojo)));
  }

  public static Pojo sampleWithConstructorWithOptionalArgument() {
    final Pojo pojo = sample();
    final PList<Argument> arguments =
        pojo.getFields()
            .map(
                f ->
                    f.isOptional()
                        ? new Argument(f.getName(), Type.optional(f.getType()))
                        : PojoFields.toArgument(f));

    return pojo.withConstructors(
        PList.single(new Constructor(Name.fromString("Customer"), arguments)));
  }

  public static Constructor deviateStandardConstructor(Pojo pojo) {
    return new Constructor(
        pojo.getName(), pojo.getFields().map(f -> new Argument(f.getName(), f.getType())));
  }
}
