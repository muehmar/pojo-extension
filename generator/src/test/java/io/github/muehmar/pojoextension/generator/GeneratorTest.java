package io.github.muehmar.pojoextension.generator;

import static io.github.muehmar.pojoextension.generator.Generator.ofWriterFunction;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.impl.WriterFactory;
import org.junit.jupiter.api.Test;

class GeneratorTest {

  @Test
  void appendGenerator_when_called_then_generatedCorrectAppended() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<Void, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<Void, Void> gen = genA.append(genB);
    final Writer writer = gen.generate(null, null, WriterFactory.createDefault());
    assertEquals("genA\ngenB\n", writer.asString());
  }

  @Test
  void appendGeneratorWithTabs_when_called_then_generatedCorrectAppendedWithIndention() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<Void, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<Void, Void> gen = genA.append(2, genB);
    final Writer writer = gen.generate(null, null, WriterFactory.createDefault());
    assertEquals("genA\n    genB\n", writer.asString());
  }

  @Test
  void appendUnaryOperator_when_called_then_generatedCorrectAppend() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));

    final Generator<Void, Void> gen = genA.append(w -> w.println("appended"));
    final Writer writer = gen.generate(null, null, WriterFactory.createDefault());
    assertEquals("genA\nappended\n", writer.asString());
  }

  @Test
  void appendList_when_called_then_contentCreatedForEveryElementInTheList() {
    final Generator<Pojo, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> fieldGen =
        (field, settings, writer) -> writer.println("%s", field.getName().asString());

    final Pojo pojo = Pojos.sample();

    final Generator<Pojo, Void> generator = genA.appendList(fieldGen, Pojo::getFields);
    final Writer writer = generator.generate(pojo, null, WriterFactory.createDefault());

    assertEquals("genA\nid\nusername\n", writer.asString());
  }
}
