package io.github.muehmar.pojoextension.generator.impl.gen;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class RefsGen {
  private RefsGen() {}

  public static Generator<PojoField, PojoSettings> fieldRefs() {
    return (f, s, w) -> addRefs(w, f.getType().getImports());
  }

  public static Generator<Pojo, PojoSettings> genericRefs() {
    return (pojo, s, w) -> addRefs(w, pojo.getGenericImports());
  }

  public static Generator<FieldBuilderMethod, PojoSettings> fieldBuilderMethodRefs() {
    return (field, s, w) -> {
      final PList<Name> argumentImports =
          field.getArguments().map(Argument::getType).flatMap(Type::getImports);
      return addRefs(w, argumentImports);
    };
  }

  private static Writer addRefs(Writer writer, PList<Name> imports) {
    return imports.map(Name::asString).foldLeft(writer, Writer::ref);
  }
}
