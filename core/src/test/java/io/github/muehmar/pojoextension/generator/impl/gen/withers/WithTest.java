package io.github.muehmar.pojoextension.generator.impl.gen.withers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.data.WithField;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class WithTest {

  @Test
  void withMethod() {
    final Generator<WithField, PojoSettings> generator = With.withMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public Customer withId(Integer id) {\n" + "  return with(self(), id);\n" + "}",
        writer.asString());
  }

  @Test
  void staticWithMethod() {
    final Generator<WithField, PojoSettings> generator = With.staticWithMethod();

    final Pojo pojo = Pojos.sample();
    final WithField withField = WithField.of(pojo, pojo.getFields().head());

    final Writer writer =
        generator.generate(withField, PojoSettings.defaultSettings(), Writer.createDefault());
    assertEquals(
        "public static Customer withId(Customer self, Integer id) {\n" + "  return self;\n" + "}",
        writer.asString());
  }
}
