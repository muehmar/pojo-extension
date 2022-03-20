package io.github.muehmar.pojoextension.generator;

import static java.util.Optional.empty;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.BuilderField;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.FullBuilderField;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.FullBuilderFieldBuilder;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;

public class FullBuilderFields {
  private FullBuilderFields() {}

  public static FullBuilderField of(Pojo pojo, PojoField field, int index) {
    if (pojo.getFields().size() < index) {
      throw new IllegalArgumentException(
          "Pojo has not enough fields " + pojo.getFields().size() + " < " + index);
    }
    final PList<PojoField> fields = pojo.getFields().take(index).add(field);
    final BuilderField builderField = new BuilderField(pojo.withFields(fields), field, index);
    return FullBuilderFieldBuilder.create()
        .builderField(builderField)
        .andAllOptionals()
        .fieldBuilder(empty())
        .build();
  }
}
