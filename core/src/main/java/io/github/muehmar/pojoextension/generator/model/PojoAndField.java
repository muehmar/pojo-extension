package io.github.muehmar.pojoextension.generator.model;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import lombok.Value;

@Value
@PojoExtension
public class PojoAndField implements PojoAndFieldExtension {
  Pojo pojo;
  PojoField field;

  public static PojoAndField of(Pojo pojo, PojoField field) {
    return new PojoAndField(pojo, field);
  }

  public FieldGetter getMatchingGetterOrThrow() {
    return pojo.getMatchingGetterOrThrow(field);
  }
}
