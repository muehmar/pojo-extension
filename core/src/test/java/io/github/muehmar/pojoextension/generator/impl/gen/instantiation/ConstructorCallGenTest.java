package io.github.muehmar.pojoextension.generator.impl.gen.instantiation;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ConstructorCallGenTest {

  @Test
  void constructorCall_when_samplePojo_then_simpleConstructorCall() {
    final Writer writer =
        ConstructorCallGen.constructorCall()
            .generate(Pojos.sample(), PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("return new Customer(id, username, nickname);", writer.asString());
  }

  @Test
  void
      constructorCall_when_samplePojoAndOptionalArgumentWrappedInOptional_then_wrapNullableFieldInOptional() {
    final Pojo sample = Pojos.sampleWithConstructorWithOptionalArgument();

    final Writer pojo =
        ConstructorCallGen.constructorCall()
            .generate(sample, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JAVA_UTIL_OPTIONAL), pojo.getRefs());
    assertEquals(
        "return new Customer(id, username, Optional.ofNullable(nickname));", pojo.asString());
  }
}
