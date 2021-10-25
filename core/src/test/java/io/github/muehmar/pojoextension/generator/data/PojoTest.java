package io.github.muehmar.pojoextension.generator.data;

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
    final Optional<MatchingConstructor> matchingConstructor = pojo.findMatchingConstructor();

    assertTrue(matchingConstructor.isPresent());
    assertEquals(
        MatchingConstructor.newBuilder()
            .setConstructor(pojo.getConstructors().head())
            .setFields(pojo.getFields())
            .build(),
        matchingConstructor.get());
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
}