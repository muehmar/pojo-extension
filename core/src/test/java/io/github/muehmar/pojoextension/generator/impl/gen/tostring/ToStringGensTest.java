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
  void genToStringMethod_when_calledWithRequiredStringField_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.genToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("username"), Type.string(), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default String genToString() {\n"
            + "  return \"Customer{\"\n"
            + "      + \"username='\" + getUsername() + '\\''\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void genToStringMethod_when_calledWithOptionalStringField_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.genToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("username"), Type.string(), OPTIONAL));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default String genToString() {\n"
            + "  return \"Customer{\"\n"
            + "      + \"username=\" + getUsername()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void genToStringMethod_when_calledWithInteger_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.genToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("age"), Type.integer(), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default String genToString() {\n"
            + "  return \"Customer{\"\n"
            + "      + \"age=\" + getAge()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void genToStringMethod_when_calledWithArray_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.genToStringMethod();
    final PList<PojoField> fields =
        PList.single(
            new PojoField(Name.fromString("data"), Type.integer().withIsArray(true), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer =
        gen.generate(pojo, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default String genToString() {\n"
            + "  return \"Customer{\"\n"
            + "      + \"data=\" + Arrays.toString(getData())\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void genToStringMethod_when_calledWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.genToStringMethod();

    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default String genToString() {\n"
            + "  return \"Customer{\"\n"
            + "      + \"id=\" + getId()\n"
            + "      + \", username='\" + getUsername() + '\\''\n"
            + "      + \", nickname=\" + getNickname()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void genToStringMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.genToStringMethod();
    final Writer writer =
        gen.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withToStringAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void genToStringMethod_when_genericSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = ToStringGens.genToStringMethod();

    final Writer writer =
        gen.generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default String genToString() {\n"
            + "  return \"Customer{\"\n"
            + "      + \"id='\" + getId() + '\\''\n"
            + "      + \", data=\" + getData()\n"
            + "      + \", additionalData=\" + getAdditionalData()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }
}
