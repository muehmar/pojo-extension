package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import java.util.function.Predicate;

@PojoExtension
@SuppressWarnings("java:S2160")
public class FullBuilderField extends FullBuilderFieldBase {
  private final BuilderField builderField;
  private final PList<FieldBuilderMethod> fieldBuilderMethods;

  public FullBuilderField(
      BuilderField builderField, PList<FieldBuilderMethod> fieldBuilderMethods) {
    this.builderField = builderField;
    this.fieldBuilderMethods = fieldBuilderMethods;
  }

  public static PList<FullBuilderField> requiredFromPojo(Pojo pojo) {
    return fromPojo(pojo, PojoField::isRequired);
  }

  public static PList<FullBuilderField> optionalFromPojo(Pojo pojo) {
    return fromPojo(pojo, PojoField::isOptional);
  }

  private static PList<FullBuilderField> fromPojo(Pojo pojo, Predicate<PojoField> filter) {
    return pojo.getFields()
        .filter(filter)
        .zipWithIndex()
        .map(
            p -> {
              final PojoField field = p.first();
              final Integer index = p.second();
              final BuilderField builderField = new BuilderField(pojo, field, index);
              final PList<FieldBuilderMethod> fieldBuilderMethods =
                  pojo.getFieldBuilderMethods()
                      .filter(fbm -> fbm.getFieldName().equals(p.first().getName()));
              return new FullBuilderField(builderField, fieldBuilderMethods);
            });
  }

  public BuilderField getBuilderField() {
    return builderField;
  }

  public PList<FieldBuilderMethod> getFieldBuilderMethods() {
    return fieldBuilderMethods;
  }

  public Pojo getPojo() {
    return builderField.getPojo();
  }

  public PojoField getField() {
    return builderField.getField();
  }

  public int getIndex() {
    return builderField.getIndex();
  }

  public PList<FieldBuilderField> getFieldBuilderFields() {
    return fieldBuilderMethods.map(
        fieldBuilderMethod -> new FieldBuilderField(builderField, fieldBuilderMethod));
  }
}
