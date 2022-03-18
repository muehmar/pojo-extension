package io.github.muehmar.pojoextension.generator.model;

import static io.github.muehmar.pojoextension.Booleans.not;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.exception.PojoExtensionException;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Value;

@Value
@PojoExtension
public class PojoField implements PojoFieldExtension {
  Name name;
  Type type;
  Necessity necessity;

  public boolean isRequired() {
    return necessity.isRequired();
  }

  public boolean isOptional() {
    return necessity.isOptional();
  }

  public Name builderSetMethodName(PojoSettings settings) {
    return settings
        .getBuilderSetMethodPrefix()
        .map(prefix -> prefix.append(name.toPascalCase()))
        .orElse(name);
  }

  public boolean isFieldBuilderMethod(FieldBuilderMethod method) {
    final boolean fieldNameMatches = method.getFieldName().equals(name);

    if (not(fieldNameMatches)) {
      return false;
    }

    return necessity
        .onRequired(() -> assertRequiredType(method))
        .onOptional(() -> assertOptionalType(method));
  }

  private boolean assertRequiredType(FieldBuilderMethod method) {
    final boolean sameType = method.getReturnType().equals(type);
    if (not(sameType)) {
      final String message = formatErrorMessage(method);
      throw new PojoExtensionException(message);
    }
    return true;
  }

  private boolean assertOptionalType(FieldBuilderMethod method) {
    final Optional<OptionalFieldRelation> typeRelation = method.getReturnType().getRelation(type);

    final Predicate<OptionalFieldRelation> sameTypeOrUnwrapOptional =
        r ->
            r.equals(OptionalFieldRelation.SAME_TYPE)
                || r.equals(OptionalFieldRelation.UNWRAP_OPTIONAL);

    final boolean optionalTypeMatches = typeRelation.filter(sameTypeOrUnwrapOptional).isPresent();

    if (not(optionalTypeMatches)) {
      final String message =
          formatErrorMessage(method)
              + " As this field is optional, the actual type could also be wrapped into a java.util.Optional.";
      throw new PojoExtensionException(message);
    }
    return true;
  }

  private String formatErrorMessage(FieldBuilderMethod method) {
    return String.format(
        "The return type '%s' of the method '%s' annotated with @FieldBuilder for field '%s' does not match the type '%s' of the field '%s'.",
        method.getReturnType().getName(), method.getMethodName(), name, type.getName(), name);
  }
}
