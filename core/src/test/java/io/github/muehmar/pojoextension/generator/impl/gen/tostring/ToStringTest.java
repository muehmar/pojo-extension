package io.github.muehmar.pojoextension.generator.impl.gen.tostring;

import static io.github.muehmar.pojoextension.generator.Settings.noSettings;
import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ToStringTest {
  @Test
  void toStringMethod_when_called_then_correctDelegatedCall() {
    final Generator<Pojo, Void> gen = ToString.toStringMethod();

    final Writer writer = gen.generate(Pojos.sample(), noSettings(), Writer.createDefault());

    assertEquals(
        "@Override\n" + "public String toString() {\n" + "  return toString(self());\n" + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithRequiredStringField_then_correctOutput() {
    final Generator<Pojo, Void> gen = ToString.staticToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("username"), Type.string(), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer = gen.generate(pojo, noSettings(), Writer.createDefault());

    assertEquals(
        "public static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"username='\" + self.getUsername() + '\\''\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithOptionalStringField_then_correctOutput() {
    final Generator<Pojo, Void> gen = ToString.staticToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("username"), Type.string(), OPTIONAL));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer = gen.generate(pojo, noSettings(), Writer.createDefault());

    assertEquals(
        "public static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"username=\" + self.getUsername()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithInteger_then_correctOutput() {
    final Generator<Pojo, Void> gen = ToString.staticToStringMethod();
    final PList<PojoField> fields =
        PList.single(new PojoField(Name.fromString("age"), Type.integer(), REQUIRED));
    final Pojo pojo =
        Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter));

    final Writer writer = gen.generate(pojo, noSettings(), Writer.createDefault());

    assertEquals(
        "public static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"age=\" + self.getAge()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }

  @Test
  void staticToStringMethod_when_calledWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, Void> gen = ToString.staticToStringMethod();

    final Writer writer = gen.generate(Pojos.sample(), noSettings(), Writer.createDefault());

    assertEquals(
        "public static String toString(Customer self) {\n"
            + "  return \"Customer{\"\n"
            + "      + \"id=\" + self.getId()\n"
            + "      + \"username='\" + self.getUsername() + '\\''\n"
            + "      + \"nickname=\" + self.getNickname()\n"
            + "      + '}';\n"
            + "}",
        writer.asString());
  }
}
