package io.github.muehmar.pojoextension.generator.model;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension
public class Constructor implements ConstructorExtension {
  Name name;
  PList<Argument> arguments;

  /**
   * Return the list of fields in the order of the match of the arguments for this constructor or
   * empty if they do not match.
   */
  public Optional<PList<FieldArgument>> matchFields(PList<PojoField> fields) {
    if (fields.size() != arguments.size()) {
      return Optional.empty();
    }

    final PList<FieldArgument> fieldArguments =
        fields
            .zip(arguments)
            .flatMapOptional(
                p -> {
                  final Argument argument = p.second();
                  final PojoField field = p.first();
                  final Optional<OptionalFieldRelation> optionalFieldRelation =
                      argument.getRelationFromField(field);
                  return optionalFieldRelation.map(
                      relation -> new FieldArgument(field, argument, relation));
                });

    return Optional.of(fieldArguments).filter(f -> f.size() == arguments.size());
  }
}
