package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class PackageGen implements Generator<Pojo, PojoSettings> {
  @Override
  public Writer generate(Pojo pojo, PojoSettings settings, Writer writer) {
    return writer.println("package %s;", pojo.getPackage().asString());
  }
}
