package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;

@PojoExtension
@SuppressWarnings("java:S2160")
public class SafeBuilderPojoField extends SafeBuilderPojoFieldBase {
  private final Pojo pojo;
  private final PojoField field;
  private final PList<FieldBuilderMethod> fieldBuilderMethods;
  private final int index;

  public SafeBuilderPojoField(
      Pojo pojo, PojoField field, PList<FieldBuilderMethod> fieldBuilderMethods, int index) {
    this.pojo = pojo;
    this.field = field;
    this.fieldBuilderMethods = fieldBuilderMethods;
    this.index = index;
  }

  public Pojo getPojo() {
    return pojo;
  }

  public PojoField getField() {
    return field;
  }

  public PList<FieldBuilderMethod> getFieldBuilderMethods() {
    return fieldBuilderMethods;
  }

  public int getIndex() {
    return index;
  }
}
