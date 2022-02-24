package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_INTEGER;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.FieldBuilderMethods;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.type.Types;
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

  @Test
  void fieldBuilderMethodRefs_when_called_then_refsForArgumentsAddButNotForFieldReturnType() {
    final Generator<FieldBuilderMethod, PojoSettings> gen = RefsGen.fieldBuilderMethodRefs();
    final FieldBuilderMethod fieldBuilderMethod =
        FieldBuilderMethods.forField(
            PojoFields.optionalName(),
            Name.fromString("customMethod"),
            new Argument(Name.fromString("val"), Types.map(Types.string(), Types.integer())));
    final Writer writer =
        gen.generate(fieldBuilderMethod, PojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }
}
