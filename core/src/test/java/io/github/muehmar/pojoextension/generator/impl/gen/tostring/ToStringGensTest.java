package io.github.muehmar.pojoextension.generator.impl.gen.tostring;

import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.settings.Ability.DISABLED;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_ARRAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ToStringGensTest {
  @Test
  void toStringMethod_when_called_then_correctDelegatedCall() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.toStringMethod();

    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "@Override\n" + "public String toString() {\n" + "  return toString(self());\n" + "}",
        writer.asString());
  }

  @Test
  void toStringMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.toStringMethod();
    final Writer writer =
        gen.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withToStringAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void staticToStringMethod_when_calledWithRequiredStringField_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.staticToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("username"), Type.string(), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "private static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"username='\" + self.getUsername() + '\\''\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithOptionalStringField_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.staticToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("username"), Type.string(), OPTIONAL));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "private static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"username=\" + self.getUsername()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithInteger_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.staticToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("age"), Type.integer(), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "private static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"age=\" + self.getAge()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithArray_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.staticToStringMethod();
    final PList<PojoField> fields =
        PList.single(
            new PojoField(Name.fromString("data"), Type.integer().withIsArray(true), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "private static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"data=\" + Arrays.toString(self.getData())\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void staticToStringMethod_when_calledWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.staticToStringMethod();

    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "private static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"id=\" + self.getId()\n"
            + "      + \", username='\" + self.getUsername() + '\\''\n"
            + "      + \", nickname=\" + self.getNickname()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.staticToStringMethod();
    final Writer writer =
        gen.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withToStringAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }
}
