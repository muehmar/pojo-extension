package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class RefsGen {
  private RefsGen() {}

  public static Generator<PojoField, PojoSettings> fieldRefs() {
    return (f, s, w) -> f.getType().getImports().map(Name::asString).foldLeft(w, Writer::ref);
  }

  public static Generator<Pojo, PojoSettings> genericRefs() {
    return (pojo, s, w) -> pojo.getGenericImports().map(Name::asString).foldLeft(w, Writer::ref);
  }
}
