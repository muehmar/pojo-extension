package io.github.muehmar.pojoextension.generator;

import static io.github.muehmar.pojoextension.generator.Generator.ofWriterFunction;
import static io.github.muehmar.pojoextension.generator.data.Necessity.OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class GeneratorTest {

  @Test
  void appendGenerator_when_called_then_generatedCorrectAppended() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<Void, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<Void, Void> gen = genA.append(genB);
    final Writer writer = gen.generate(null, null, Writer.createDefault());
    assertEquals("genA\ngenB", writer.asString());
  }

  @Test
  void appendGeneratorWithTabs_when_called_then_generatedCorrectAppendedWithIndention() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<Void, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<Void, Void> gen = genA.append(genB, 2);
    final Writer writer = gen.generate(null, null, Writer.createDefault());
    assertEquals("genA\n    genB", writer.asString());
  }

  @Test
  void appendUnaryOperator_when_called_then_generatedCorrectAppend() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));

    final Generator<Void, Void> gen = genA.append(w -> w.println("appended"));
    final Writer writer = gen.generate(null, null, Writer.createDefault());
    assertEquals("genA\nappended", writer.asString());
  }

  @Test
  void appendList_when_called_then_contentCreatedForEveryElementInTheList() {
    final Generator<Pojo, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> fieldGen =
        (field, settings, writer) -> writer.println("%s", field.getName());

    final Pojo pojo = Pojos.sample();

    final Generator<Pojo, Void> generator = genA.appendList(fieldGen, Pojo::getFields);
    final Writer writer = generator.generate(pojo, null, Writer.createDefault());

    assertEquals("genA\nid\nusername\nnickname", writer.asString());
  }

  @Test
  void appendConditionally_when_conditionEvaluatedToTrue_then_generatorAppended() {
    final Generator<PojoField, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<PojoField, Void> generator =
        genA.appendConditionally(PojoField::isRequired, genB);
    final Writer writer = generator.generate(PojoFields.requiredId(), null, Writer.createDefault());

    assertEquals("genA\ngenB", writer.asString());
  }

  @Test
  void appendConditionally_when_conditionEvaluatedToFalse_then_generatorNotAppended() {
    final Generator<PojoField, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<PojoField, Void> generator =
        genA.appendConditionally(PojoField::isRequired, genB);
    final Writer writer =
        generator.generate(
            PojoFields.requiredId().withNecessity(OPTIONAL), null, Writer.createDefault());

    assertEquals("genA", writer.asString());
  }
}
