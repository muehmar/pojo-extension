package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.settings.Ability.DISABLED;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_ARRAYS;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OBJECTS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.type.PrimitiveType;
import io.github.muehmar.pojoextension.generator.data.type.Type;
import io.github.muehmar.pojoextension.generator.data.type.Types;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class EqualsGensTest {

  @Test
  void genEqualsMethod_when_generatorUsedWithSamplePojoWithoutFields_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsGens.genEqualsMethod();

    final Writer writer =
        generator.generate(
            Pojos.sample().withFields(PList.empty()),
            PojoSettings.defaultSettings(),
            Writer.createDefault());
    assertEquals(
        "default boolean genEquals(Object obj) {\n"
            + "  if (this == obj) return true;\n"
            + "  if (obj == null || this.getClass() != obj.getClass()) return false;\n"
            + "  final Customer other = (Customer) obj;\n"
            + "  return true;\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OBJECTS::equals));
    assertFalse(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void genEqualsMethod_when_generatorUsedWithSamplePojo_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsGens.genEqualsMethod();

    final Writer writer =
        generator.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default boolean genEquals(Object obj) {\n"
            + "  if (this == obj) return true;\n"
            + "  if (obj == null || this.getClass() != obj.getClass()) return false;\n"
            + "  final Customer other = (Customer) obj;\n"
            + "  return Objects.equals(getId(), other.getId())\n"
            + "      && Objects.equals(getUsername(), other.getUsername())\n"
            + "      && Objects.equals(getNickname(), other.getNickname());\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OBJECTS::equals));
    assertFalse(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void genEqualsMethod_when_generatorUsedWithPrimitiveTypes_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsGens.genEqualsMethod();

    final PList<PojoField> primitiveFields =
        PList.of(PrimitiveType.values())
            .map(t -> new PojoField(t.getName().prefix("p"), Type.fromSpecificType(t), REQUIRED));

    final Writer writer =
        generator.generate(
            Pojos.sample()
                .withFields(primitiveFields)
                .withGetters(primitiveFields.map(PojoFields::toGetter)),
            PojoSettings.defaultSettings(),
            Writer.createDefault());
    assertEquals(
        "default boolean genEquals(Object obj) {\n"
            + "  if (this == obj) return true;\n"
            + "  if (obj == null || this.getClass() != obj.getClass()) return false;\n"
            + "  final Customer other = (Customer) obj;\n"
            + "  return getPint() == other.getPint()\n"
            + "      && getPbyte() == other.getPbyte()\n"
            + "      && getPshort() == other.getPshort()\n"
            + "      && getPlong() == other.getPlong()\n"
            + "      && getPfloat() == other.getPfloat()\n"
            + "      && Double.compare(getPdouble(), other.getPdouble()) == 0\n"
            + "      && isPboolean() == other.isPboolean()\n"
            + "      && getPchar() == other.getPchar();\n"
            + "}",
        writer.asString());
    assertFalse(writer.getRefs().exists(JAVA_UTIL_OBJECTS::equals));
    assertFalse(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void genEqualsMethod_when_generatorUsedWithSamplePojoAndArrayField_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsGens.genEqualsMethod();

    final PList<PojoField> fields =
        Pojos.sample()
            .getFields()
            .cons(
                new PojoField(
                    Name.fromString("byteArray"), Types.array(Types.primitiveByte()), REQUIRED));

    final Writer writer =
        generator.generate(
            Pojos.sample().withFields(fields).withGetters(fields.map(PojoFields::toGetter)),
            PojoSettings.defaultSettings(),
            Writer.createDefault());
    assertEquals(
        "default boolean genEquals(Object obj) {\n"
            + "  if (this == obj) return true;\n"
            + "  if (obj == null || this.getClass() != obj.getClass()) return false;\n"
            + "  final Customer other = (Customer) obj;\n"
            + "  return Arrays.equals(getByteArray(), other.getByteArray())\n"
            + "      && Objects.equals(getId(), other.getId())\n"
            + "      && Objects.equals(getUsername(), other.getUsername())\n"
            + "      && Objects.equals(getNickname(), other.getNickname());\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OBJECTS::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_ARRAYS::equals));
  }

  @Test
  void genEqualsMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsGens.genEqualsMethod();

    final Writer writer =
        generator.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withEqualsHashCodeAbility(DISABLED),
            Writer.createDefault());
    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void genEqualsMethod_when_genericSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsGens.genEqualsMethod();

    final Writer writer =
        generator.generate(
            Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "default boolean genEquals(Object obj) {\n"
            + "  if (this == obj) return true;\n"
            + "  if (obj == null || this.getClass() != obj.getClass()) return false;\n"
            + "  final Customer<?, ?> other = (Customer<?, ?>) obj;\n"
            + "  return Objects.equals(getId(), other.getId())\n"
            + "      && Objects.equals(getData(), other.getData())\n"
            + "      && Objects.equals(getAdditionalData(), other.getAdditionalData());\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OBJECTS::equals));
  }

  @Test
  void equalsMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> generator = EqualsGens.genEqualsMethod();

    final Writer writer =
        generator.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withEqualsHashCodeAbility(DISABLED),
            Writer.createDefault());
    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }
}
