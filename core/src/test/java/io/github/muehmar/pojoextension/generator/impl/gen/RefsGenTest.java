package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class RefsGenTest {

  @Test
  void fieldRefs_when_genericFieldTypeAsInput_then_writerContainsAllRefs() {
    final Generator<PojoField, PojoSettings> gen = RefsGen.fieldRefs();
    final Writer writer =
        gen.generate(
            PojoFields.requiredMap(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
  }

  @Test
  void genericRefs_when_genericPojo_then_writerContainsAllRefs() {
    final Generator<Pojo, PojoSettings> gen = RefsGen.genericRefs();
    final Writer writer =
        gen.generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_LIST::equals));
  }

  @Test
  void genericRefs_when_nonGenericPojo_then_noRefs() {
    final Generator<Pojo, PojoSettings> gen = RefsGen.genericRefs();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());
  }
}
