package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.FieldBuilder;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Value;

@Value
@PojoExtension
public class BuilderField implements BuilderFieldExtension {
  IndexedField indexedField;
  Optional<FieldBuilder> fieldBuilder;

  public Pojo getPojo() {
    return indexedField.getPojo();
  }

  public PojoField getField() {
    return indexedField.getField();
  }

  public int getIndex() {
    return indexedField.getIndex();
  }

  public boolean hasFieldBuilder() {
    return fieldBuilder.isPresent();
  }

  public boolean isDisableDefaultMethods() {
    return fieldBuilder.map(FieldBuilder::isDisableDefaultMethods).orElse(false);
  }

  public boolean isEnableDefaultMethods() {
    return !isDisableDefaultMethods();
  }

  public boolean isFieldOptional() {
    return indexedField.getField().isOptional();
  }

  public static PList<BuilderField> requiredFromPojo(Pojo pojo) {
    return fromPojo(pojo, PojoField::isRequired);
  }

  public static PList<BuilderField> optionalFromPojo(Pojo pojo) {
    return fromPojo(pojo, PojoField::isOptional);
  }

  private static PList<BuilderField> fromPojo(Pojo pojo, Predicate<PojoField> filter) {
    return pojo.getFields()
        .filter(filter)
        .zipWithIndex()
        .map(
            p -> {
              final PojoField field = p.first();
              final Integer index = p.second();
              final IndexedField indexedField = new IndexedField(pojo, field, index);
              final Optional<FieldBuilder> fieldBuilder =
                  pojo.getFieldBuilders().find(field::isFieldBuilder);
              return new BuilderField(indexedField, fieldBuilder);
            });
  }

  public PList<BuilderFieldWithMethod> getBuilderFieldsWithMethod() {
    return fieldBuilder
        .map(FieldBuilder::getMethods)
        .map(NonEmptyList::toPList)
        .orElse(PList.empty())
        .map(fieldBuilderMethod -> new BuilderFieldWithMethod(this, fieldBuilderMethod));
  }
}
