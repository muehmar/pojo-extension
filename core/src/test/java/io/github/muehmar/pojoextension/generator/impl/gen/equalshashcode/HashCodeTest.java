package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_ARRAYS;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OBJECTS;
import static org.junit.jupiter.api.Assertions.*;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class HashCodeTest {

  @Test
  void staticHashCodeMethod_when_generatorUsedWithSamplePojoAndArrayField_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = HashCode.staticHashCodeMethod();

    final PList<PojoField> fields =
        Pojos.sample()
            .getFields()
            .cons(
                new PojoField(
                    Type.primitive("byte").withIsArray(true), Name.fromString("byteArray"), true));

    final Writer writer =
        generator.generate(
            Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter)),
            PojoSettings.defaultSettings(),
            Writer.createDefault());
    assertEquals(
        "public static int hashCode(Customer o) {\n"
            + "  int result = Objects.hash(o.getId(), o.getUsername(), o.getNickname());\n"
            + "  result = 31 * result + Arrays.hashCode(o.getByteArray());\n"
            + "  return result;\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OBJECTS::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void staticHashCodeMethod_when_generatorUsedWithBooleanField_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = HashCode.staticHashCodeMethod();

    final PList<PojoField> fields =
        PList.of(new PojoField(Type.primitiveBoolean(), Name.fromString("flag"), true));

    final Writer writer =
        generator.generate(
            Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter)),
            PojoSettings.defaultSettings(),
            Writer.createDefault());
    assertEquals(
        "public static int hashCode(Customer o) {\n"
            + "  int result = Objects.hash(o.isFlag());\n"
            + "  return result;\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OBJECTS::equals));
    assertFalse(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void staticHashCodeMethod_when_generatorUsedWithTwoByteArrays_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = HashCode.staticHashCodeMethod();

    final PList<PojoField> fields =
        PList.of(
            new PojoField(
                Type.primitive("byte").withIsArray(true), Name.fromString("byteArray"), true),
            new PojoField(
                Type.primitive("byte").withIsArray(true), Name.fromString("byteArray2"), true));

    final Writer writer =
        generator.generate(
            Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter)),
            PojoSettings.defaultSettings(),
            Writer.createDefault());
    assertEquals(
        "public static int hashCode(Customer o) {\n"
            + "  int result = Arrays.hashCode(o.getByteArray());\n"
            + "  result = 31 * result + Arrays.hashCode(o.getByteArray2());\n"
            + "  return result;\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void hashCodeMethod_when_generatorUsedWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = HashCode.hashCodeMethod();
    final Writer writer =
        generator.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "@Override\n" + "public int hashCode() {\n" + "  return hashCode(self());\n" + "}",
        writer.asString());
  }
}
