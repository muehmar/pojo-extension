package io.github.muehmar.pojoextension.generator;

import static io.github.muehmar.pojoextension.generator.model.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.model.Necessity.REQUIRED;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.Constructor;
import io.github.muehmar.pojoextension.generator.model.Generic;
import io.github.muehmar.pojoextension.generator.model.Getter;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.PackageName;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoBuilder;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import java.util.Optional;

public class Pojos {
  public static final PackageName PACKAGE_NAME = PackageName.fromString("io.github.muehmar");

  private Pojos() {}

  public static Pojo sample() {
    final PList<PojoField> fields =
        PList.of(
            new PojoField(Names.id(), Types.integer(), REQUIRED),
            new PojoField(Name.fromString("username"), Types.string(), REQUIRED),
            new PojoField(Name.fromString("nickname"), Types.string(), OPTIONAL));

    final PList<Getter> getters = fields.map(PojoFields::toGetter);

    final Pojo pojo =
        PojoBuilder.create()
            .name(Name.fromString("Customer"))
            .pkg(PACKAGE_NAME)
            .fields(fields)
            .constructors(PList.empty())
            .getters(getters)
            .generics(PList.empty())
            .fieldBuilders(PList.empty())
            .andAllOptionals()
            .buildMethod(Optional.empty())
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
                        ? new Argument(f.getName(), Types.optional(f.getType()))
                        : PojoFields.toArgument(f));

    return pojo.withConstructors(
        PList.single(new Constructor(Name.fromString("Customer"), arguments)));
  }

  public static Pojo genericSample() {
    final PList<PojoField> fields =
        PList.of(
            new PojoField(Names.id(), Types.string(), REQUIRED),
            new PojoField(
                Name.fromString("data"), Types.typeVariable(Name.fromString("T")), REQUIRED),
            new PojoField(
                Name.fromString("additionalData"),
                Types.typeVariable(Name.fromString("S")),
                OPTIONAL));

    final PList<Getter> getters = fields.map(PojoFields::toGetter);

    final PList<Generic> generics =
        PList.of(
            new Generic(Name.fromString("T"), PList.single(Types.list(Types.string()))),
            new Generic(Name.fromString("S"), PList.empty()));

    final Pojo pojo =
        PojoBuilder.create()
            .name(Name.fromString("Customer"))
            .pkg(PACKAGE_NAME)
            .fields(fields)
            .constructors(PList.empty())
            .getters(getters)
            .generics(generics)
            .fieldBuilders(PList.empty())
            .andAllOptionals()
            .buildMethod(Optional.empty())
            .build();
    return pojo.withConstructors(PList.single(deviateStandardConstructor(pojo)));
  }

  public static Constructor deviateStandardConstructor(Pojo pojo) {
    return new Constructor(
        pojo.getName(), pojo.getFields().map(f -> new Argument(f.getName(), f.getType())));
  }
}
