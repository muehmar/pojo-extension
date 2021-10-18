package io.github.muehmar.pojoextension.generator;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Constructor;
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
            new PojoField(Type.integer(), Name.fromString("id"), true),
            new PojoField(Type.string(), Name.fromString("username"), true),
            new PojoField(Type.string(), Name.fromString("nickname"), false));
    final Name extensionName = Name.fromString("CustomerExtension");
    final Pojo pojo =
        Pojo.newBuilder()
            .setExtensionName(extensionName)
            .setPojoName(Name.fromString("Customer"))
            .setPkg(PACKAGE_NAME)
            .setFields(fields)
            .setConstructors(PList.empty())
            .andAllOptionals()
            .build();
    return pojo.withConstructors(PList.single(deviateStandardConstructor(pojo)));
  }

  public static Constructor deviateStandardConstructor(Pojo pojo) {
    return new Constructor(
        pojo.getPojoName(), pojo.getFields().map(f -> new Argument(f.getName(), f.getType())));
  }
}
