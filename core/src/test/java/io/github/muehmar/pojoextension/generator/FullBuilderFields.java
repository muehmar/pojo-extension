package io.github.muehmar.pojoextension.generator;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.BuilderField;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.FullBuilderField;

public class FullBuilderFields {
  private FullBuilderFields() {}

  public static FullBuilderField of(Pojo pojo, PojoField field, int index) {
    if (pojo.getFields().size() < index) {
      throw new IllegalArgumentException(
          "Pojo has not enough fields " + pojo.getFields().size() + " < " + index);
    }
    final PList<PojoField> fields = pojo.getFields().take(index).add(field);
    final BuilderField builderField = new BuilderField(pojo.withFields(fields), field, index);
    return new FullBuilderField(builderField, PList.empty());
  }
}
