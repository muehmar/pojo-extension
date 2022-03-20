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
public class FullBuilderField implements FullBuilderFieldExtension {
  BuilderField builderField;
  Optional<FieldBuilder> fieldBuilder;

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
              final Optional<FieldBuilder> fieldBuilder =
                  pojo.getFieldBuilders().find(field::isFieldBuilder);
              return new FullBuilderField(builderField, fieldBuilder);
            });
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
    return fieldBuilder
        .map(FieldBuilder::getMethods)
        .map(NonEmptyList::toPList)
        .orElse(PList.empty())
        .map(fieldBuilderMethod -> new FieldBuilderField(builderField, fieldBuilderMethod));
  }
}
