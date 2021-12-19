package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Pojos;
import java.util.Optional;
import java.util.Set;
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
        MatchingConstructorBuilder.create()
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
            new PojoField(Name.fromString("notAFieldInTheSamplePojo"), Type.string(), REQUIRED));

    assertEquals(Optional.empty(), fieldGetter);
  }

  @Test
  void getDiamond_when_calledForNonGenericSample_then_empty() {
    assertEquals("", Pojos.sample().getDiamond());
  }

  @Test
  void getDiamond_when_calledForGenericSample_then_empty() {
    assertEquals("<>", Pojos.genericSample().getDiamond());
  }

  @Test
  void getTypeVariablesSection_when_calledForNonGenericSample_then_empty() {
    assertEquals("", Pojos.sample().getTypeVariablesSection());
  }

  @Test
  void getTypeVariablesSection_when_calledForGenericSample_then_empty() {
    assertEquals("<T, S>", Pojos.genericSample().getTypeVariablesSection());
  }

  @Test
  void getGenericDefinitions_when_calledForNonGenericSample_then_empty() {
    assertEquals(PList.empty(), Pojos.sample().getGenericTypeDeclaration());
  }

  @Test
  void getGenericDefinitions_when_calledForGenericSample_then_empty() {
    assertEquals(
        PList.of("T extends List<String>", "S"), Pojos.genericSample().getGenericTypeDeclaration());
  }

  @Test
  void getGenericImports_when_calledForNonGenericSample_then_empty() {
    assertEquals(PList.empty(), Pojos.sample().getGenericImports());
  }

  @Test
  void getGenericImports_when_calledForGenericSample_then_empty() {
    final Set<Name> actual = Pojos.genericSample().getGenericImports().toHashSet();
    final Set<Name> expected =
        PList.of(Name.fromString(JAVA_UTIL_LIST), Name.fromString(JAVA_LANG_STRING)).toHashSet();
    assertEquals(expected, actual);
  }
}
