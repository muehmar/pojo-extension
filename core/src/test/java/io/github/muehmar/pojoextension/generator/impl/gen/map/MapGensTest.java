package io.github.muehmar.pojoextension.generator.impl.gen.map;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_BIFUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_FUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_UNARYOPERATOR;
import static io.github.muehmar.pojoextension.generator.model.settings.Ability.DISABLED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class MapGensTest {

  @Test
  void mapMethod_when_called_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapMethod();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default <T> T map(Function<Customer, T> f) {\n"
            + "  final Customer self =\n"
            + "    new Customer(getId(), getUsername(), getNickname().orElse(null));\n"
            + "  return f.apply(self);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_FUNCTION::equals));
  }

  @Test
  void mapMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapMethod();
    final Writer writer =
        gen.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withMappersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void mapMethod_when_genericSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapMethod();
    final Writer writer =
        gen.generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default <A> A map(Function<Customer<T, S>, A> f) {\n"
            + "  final Customer<T, S> self =\n"
            + "    new Customer<>(getId(), getData(), getAdditionalData().orElse(null));\n"
            + "  return f.apply(self);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_FUNCTION::equals));
  }

  @Test
  void mapIfMethod_when_called_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfMethod();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default Customer mapIf(boolean shouldMap, UnaryOperator<Customer> f) {\n"
            + "  final Customer self =\n"
            + "    new Customer(getId(), getUsername(), getNickname().orElse(null));\n"
            + "  return shouldMap ? f.apply(self) : self;\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_UNARYOPERATOR::equals));
  }

  @Test
  void mapIfMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfMethod();
    final Writer writer =
        gen.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withMappersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void mapIfMethod_when_genericSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfMethod();
    final Writer writer =
        gen.generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default Customer<T, S> mapIf(boolean shouldMap, UnaryOperator<Customer<T, S>> f) {\n"
            + "  final Customer<T, S> self =\n"
            + "    new Customer<>(getId(), getData(), getAdditionalData().orElse(null));\n"
            + "  return shouldMap ? f.apply(self) : self;\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_UNARYOPERATOR::equals));
  }

  @Test
  void mapIfPresentMethod_when_called_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfPresentMethod();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default <T> Customer mapIfPresent(Optional<T> value, BiFunction<Customer, T, Customer> f) {\n"
            + "  final Customer self =\n"
            + "    new Customer(getId(), getUsername(), getNickname().orElse(null));\n"
            + "  return value.map(v -> f.apply(self, v)).orElse(self);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_BIFUNCTION::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }

  @Test
  void mapIfPresentMethod_when_disabled_then_noOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfPresentMethod();
    final Writer writer =
        gen.generate(
            Pojos.sample(),
            PojoSettings.defaultSettings().withMappersAbility(DISABLED),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void mapIfPresentMethod_when_genericSample_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfPresentMethod();
    final Writer writer =
        gen.generate(Pojos.genericSample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default <A> Customer<T, S> mapIfPresent(Optional<A> value, BiFunction<Customer<T, S>, A, Customer<T, S>> f) {\n"
            + "  final Customer<T, S> self =\n"
            + "    new Customer<>(getId(), getData(), getAdditionalData().orElse(null));\n"
            + "  return value.map(v -> f.apply(self, v)).orElse(self);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_BIFUNCTION::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }
}
