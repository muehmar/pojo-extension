package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Pojos;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PojoTest {

  @Test
  void findMatchingConstructor_when_samplePojo_then_matchForSingleConstructorAndFields() {
    final Pojo pojo = Pojos.sample();

    final PList<FieldArgument> fieldArguments =
        pojo.getFields()
            .zip(pojo.getConstructors().head().getArguments())
            .map(p -> new FieldArgument(p.first(), p.second(), SAME_TYPE));
    final MatchingConstructor expected =
        MatchingConstructor.newBuilder()
            .setConstructor(pojo.getConstructors().head())
            .setFieldArguments(fieldArguments)
            .build();

    final Optional<MatchingConstructor> matchingConstructor = pojo.findMatchingConstructor();

    assertTrue(matchingConstructor.isPresent());
    assertEquals(expected, matchingConstructor.get());
  }

  @Test
  void findMatchingConstructor_when_samplePojoWithoutConstructor_then_noMatchingConstructorFound() {
    final Pojo pojo = Pojos.sample().withConstructors(PList.empty());
    final Optional<MatchingConstructor> matchingConstructor = pojo.findMatchingConstructor();

    assertFalse(matchingConstructor.isPresent());
  }

  @Test
  void findMatchingConstructor_when_samplePojoAndOneRemovedField_then_noMatchingConstructorFound() {
    final Pojo p = Pojos.sample();
    final Pojo pojo = p.withFields(p.getFields().drop(1));
    final Optional<MatchingConstructor> matchingConstructor = pojo.findMatchingConstructor();

    assertFalse(matchingConstructor.isPresent());
  }

  @Test
  void
      findMatchingConstructor_when_samplePojoAndReversedFieldOrder_then_noMatchingConstructorFound() {
    final Pojo p = Pojos.sample();
    final Pojo pojo = p.withFields(p.getFields().reverse());
    final Optional<MatchingConstructor> matchingConstructor = pojo.findMatchingConstructor();

    assertFalse(matchingConstructor.isPresent());
  }

  @Test
  void findMatchingGetter_when_calledForFieldInPojo_then_returnsFieldGetter() {
    final Pojo pojo = Pojos.sample();
    final Optional<FieldGetter> fieldGetter = pojo.findMatchingGetter(pojo.getFields().head());

    final FieldGetter expected =
        new FieldGetter(pojo.getGetters().head(), pojo.getFields().head(), SAME_TYPE);
    assertEquals(Optional.of(expected), fieldGetter);
  }

  @Test
  void findMatchingGetter_when_calledForOtherField_then_returnsEmpty() {
    final Pojo pojo = Pojos.sample();
    final Optional<FieldGetter> fieldGetter =
        pojo.findMatchingGetter(
            new PojoField(Type.string(), Name.fromString("notAFieldInTheSamplePojo"), true));

    assertEquals(Optional.empty(), fieldGetter);
  }
}
