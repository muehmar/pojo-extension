package io.github.muehmar.pojoextension.generator.data;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import org.junit.jupiter.api.Test;

class PojoFieldTest {

  @Test
  void builderSetMethodName_when_noPrefix_then_isFieldName() {
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(empty());
    final Name builderSetMethodName = PojoFields.requiredId().builderSetMethodName(settings);

    assertEquals("id", builderSetMethodName.asString());
  }

  @Test
  void builderSetMethodName_when_with_then_correctPrefixed() {
    final PojoSettings settings =
        PojoSettings.defaultSettings().withBuilderSetMethodPrefix(Name.fromString("set"));
    final Name builderSetMethodName = PojoFields.requiredId().builderSetMethodName(settings);

    assertEquals("setId", builderSetMethodName.asString());
  }
}
