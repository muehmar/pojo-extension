package io.github.muehmar.pojoextension.generator.model;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_LIST;
import static io.github.muehmar.pojoextension.generator.model.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.model.OptionalFieldRelation.SAME_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.model.type.Types;
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
            .constructor(pojo.getConstructors().head())
            .fieldArguments(fieldArguments)
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
            new PojoField(Name.fromString("notAFieldInTheSamplePojo"), Types.string(), REQUIRED));

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
  void getNameWithTypeVariables_when_calledForNonGenericSample_then_onlyName() {
    assertEquals("Customer", Pojos.sample().getNameWithTypeVariables().asString());
  }

  @Test
  void getNameWithTypeVariables_when_calledForGenericSample_then_nameWithTypeVariables() {
    assertEquals("Customer<T, S>", Pojos.genericSample().getNameWithTypeVariables().asString());
  }

  @Test
  void getTypeVariablesWildcardSection_when_calledForNonGenericSample_then_empty() {
    assertEquals("", Pojos.sample().getTypeVariablesWildcardSection());
  }

  @Test
  void getTypeVariablesWildcardSection_when_calledForGenericSample_then_empty() {
    assertEquals("<?, ?>", Pojos.genericSample().getTypeVariablesWildcardSection());
  }

  @Test
  void getGenericTypeDeclarations_when_calledForNonGenericSample_then_empty() {
    assertEquals(PList.empty(), Pojos.sample().getGenericTypeDeclarations());
  }

  @Test
  void getGenericTypeDeclarations_when_calledForGenericSample_then_empty() {
    assertEquals(
        PList.of("T extends List<String>", "S"),
        Pojos.genericSample().getGenericTypeDeclarations());
  }

  @Test
  void getGenericTypeDeclarationSection_when_calledForNonGenericSample_then_empty() {
    assertEquals("", Pojos.sample().getGenericTypeDeclarationSection());
  }

  @Test
  void getGenericTypeDeclarationSection_when_calledForGenericSample_then_empty() {
    assertEquals(
        "<T extends List<String>, S>", Pojos.genericSample().getGenericTypeDeclarationSection());
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

  @Test
  void findUnusedTypeVariableName_when_nonGenericSample_then_firstUppercaseLetterReturned() {
    final Name unusedTypeVariableName = Pojos.sample().findUnusedTypeVariableName();
    assertEquals("A", unusedTypeVariableName.asString());
  }

  @Test
  void findUnusedTypeVariableName_when_genericSample_then_nextFreeUppercaseLetterReturned() {
    final Name unusedTypeVariableName =
        Pojos.sample()
            .withGenerics(PList.single(new Generic(Name.fromString("A"), PList.empty())))
            .findUnusedTypeVariableName();
    assertEquals("B", unusedTypeVariableName.asString());
  }

  @Test
  void
      findUnusedTypeVariableName_when_genericSampleWithPresentPreferredValue_then_returnNextFree() {
    final Name unusedTypeVariableName =
        Pojos.genericSample().findUnusedTypeVariableName(Name.fromString("T"));
    assertEquals("A", unusedTypeVariableName.asString());
  }

  @Test
  void
      findUnusedTypeVariableName_when_genericSampleWithAbsentPreferredValue_then_returnPreferred() {
    final Name unusedTypeVariableName =
        Pojos.sample()
            .withGenerics(PList.single(new Generic(Name.fromString("A"), PList.empty())))
            .findUnusedTypeVariableName(Name.fromString("T"));
    assertEquals("T", unusedTypeVariableName.asString());
  }
}
