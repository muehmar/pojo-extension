package io.github.muehmar.pojoextension.generator.impl.gen.map;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_BIFUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_FUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_UNARYOPERATOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class MapGensTest {

  @Test
  void mapMethod_when_called_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapMethod();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public <T> T map(Function<Customer, T> f) {\n" + "  return f.apply(self());\n" + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_FUNCTION::equals));
  }

  @Test
  void mapIfMethod_when_called_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfMethod();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public Customer mapIf(boolean shouldMap, UnaryOperator<Customer> f) {\n"
            + "  return shouldMap ? f.apply(self()) : self();\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_UNARYOPERATOR::equals));
  }

  @Test
  void mapIfPresentMethod_when_called_then_correctOutput() {
    final Generator<Pojo, PojoSettings> gen = MapGens.mapIfPresent();
    final Writer writer =
        gen.generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public <T> Customer mapIfPresent(Optional<T> value, BiFunction<Customer, T, Customer> f) {\n"
            + "  return value.map(v -> f.apply(self(), v)).orElseGet(this::self);\n"
            + "}",
        writer.asString());
    assertTrue(writer.getRefs().exists(JAVA_UTIL_BIFUNCTION::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_OPTIONAL::equals));
  }
}
