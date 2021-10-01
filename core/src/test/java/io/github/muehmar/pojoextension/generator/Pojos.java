package io.github.muehmar.pojoextension.generator;

import ch.bluecare.commons.data.PList;
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
    return new Pojo(
        Name.fromString("CustomerExtension"), Name.fromString("Customer"), PACKAGE_NAME, fields);
  }
}
