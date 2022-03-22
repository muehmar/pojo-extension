package io.github.muehmar.pojoextension.generator;

import static io.github.muehmar.pojoextension.generator.Generator.ofWriterFunction;
import static io.github.muehmar.pojoextension.generator.Settings.noSettings;
import static io.github.muehmar.pojoextension.generator.model.Necessity.OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class GeneratorTest {

  @Test
  void appendGenerator_when_called_then_generatedCorrectAppended() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<Void, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<Void, Void> gen = genA.append(genB);
    final Writer writer = gen.generate(null, noSettings(), Writer.createDefault());
    assertEquals("genA\ngenB", writer.asString());
  }

  @Test
  void appendGeneratorWithTabs_when_called_then_generatedCorrectAppendedWithIndention() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<Void, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<Void, Void> gen = genA.append(genB, 2);
    final Writer writer = gen.generate(null, noSettings(), Writer.createDefault());
    assertEquals("genA\n    genB", writer.asString());
  }

  @Test
  void appendUnaryOperator_when_called_then_generatedCorrectAppend() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));

    final Generator<Void, Void> gen = genA.append(w -> w.println("appended"));
    final Writer writer = gen.generate(null, noSettings(), Writer.createDefault());
    assertEquals("genA\nappended", writer.asString());
  }

  @Test
  void appendList_when_called_then_contentCreatedForEveryElementInTheList() {
    final Generator<Pojo, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> fieldGen =
        (field, settings, writer) -> writer.println("%s", field.getName());

    final Pojo pojo = Pojos.sample();

    final Generator<Pojo, Void> generator = genA.appendList(fieldGen, Pojo::getFields);
    final Writer writer = generator.generate(pojo, noSettings(), Writer.createDefault());

    assertEquals("genA\nid\nusername\nnickname", writer.asString());
  }

  @Test
  void appendList_when_calledWithSeparator_then_separatorAddedBetween() {
    final Generator<Pojo, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> fieldGen =
        (field, settings, writer) -> writer.println("%s", field.getName());

    final Pojo pojo = Pojos.sample();

    final Generator<Pojo, Void> generator =
        genA.appendList(fieldGen, Pojo::getFields, Generator.ofWriterFunction(Writer::println));
    final Writer writer = generator.generate(pojo, noSettings(), Writer.createDefault());

    assertEquals("genA\nid\n\nusername\n\nnickname", writer.asString());
  }

  @Test
  void appendList_when_emptyList_then_initialGenExecuted() {
    final Generator<Pojo, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> fieldGen =
        (field, settings, writer) -> writer.println("%s", field.getName());

    final Pojo pojo = Pojos.sample();

    final Generator<Pojo, Void> generator =
        genA.appendList(
            fieldGen, ignore -> PList.empty(), Generator.ofWriterFunction(Writer::println));
    final Writer writer = generator.generate(pojo, noSettings(), Writer.createDefault());

    assertEquals("genA", writer.asString());
  }

  @Test
  void appendConditionally_when_conditionEvaluatedToTrue_then_generatorAppended() {
    final Generator<PojoField, Void> genA = ofWriterFunction(w -> w.println("genA"));
    final Generator<PojoField, Void> genB = ofWriterFunction(w -> w.println("genB"));

    final Generator<PojoField, Void> generator =
        genA.appendConditionally(PojoField::isRequired, genB);
    final Writer writer =
        generator.generate(PojoFields.requiredId(), noSettings(), Writer.createDefault());

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
            PojoFields.requiredId().withNecessity(OPTIONAL), noSettings(), Writer.createDefault());

    assertEquals("genA", writer.asString());
  }

  @Test
  void filter_when_conditionIsTrue_then_generatorUnchanged() {
    final Generator<Integer, Integer> genA = ofWriterFunction(w -> w.println("genA"));

    final Generator<Integer, Integer> generator = genA.filter((i1, i2) -> i1 + i2 == 3);

    final Writer writer = generator.generate(1, 2, Writer.createDefault());

    assertEquals("genA", writer.asString());
  }

  @Test
  void filter_when_conditionIsFalse_then_emptyGeneratorReturned() {
    final Generator<Integer, Integer> genA = ofWriterFunction(w -> w.println("genA"));

    final Generator<Integer, Integer> generator = genA.filter((i1, i2) -> i1 + i2 == 3);

    final Writer writer = generator.generate(2, 2, Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void appendNewLine_when_called_then_outputHasNewLineAppended() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));

    final Writer writer =
        genA.appendNewLine().generate((Void) null, noSettings(), Writer.createDefault());

    assertEquals("genA\n", writer.asString());
  }

  @Test
  void prependNewLine_when_called_then_outputHasNewLinePrepended() {
    final Generator<Void, Void> genA = ofWriterFunction(w -> w.println("genA"));

    final Writer writer =
        genA.prependNewLine().generate((Void) null, noSettings(), Writer.createDefault());

    assertEquals("\ngenA", writer.asString());
  }

  @Test
  void contraMap_when_newGenCalled_then_inputTransformedAccordingly() {
    final Generator<String, Void> genA = (s, ignore, w) -> w.println(s);

    final Generator<Integer, Void> generator = genA.contraMap(Integer::toHexString);

    final Writer writer = generator.generate(255, noSettings(), Writer.createDefault());

    assertEquals("ff", writer.asString());
  }
}
