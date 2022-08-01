package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;

public class PackageGen implements Generator<Pojo, PojoSettings> {
  @Override
  public Writer generate(Pojo pojo, PojoSettings settings, Writer writer) {
    return writer.println("package %s;", pojo.getPackage().asString());
  }
}
