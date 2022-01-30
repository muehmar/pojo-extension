package io.github.muehmar.pojoextension.generator.data;

import static io.github.muehmar.pojoextension.Booleans.not;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.exception.PojoExtensionException;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class PojoField extends PojoFieldBase {
  private final Name name;
  private final Type type;
  private final Necessity necessity;

  public PojoField(Name name, Type type, Necessity necessity) {
    this.name = name;
    this.type = type;
    this.necessity = necessity;
  }

  public Type getType() {
    return type;
  }

  public Name getName() {
    return name;
  }

  public Necessity getNecessity() {
    return necessity;
  }

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

    final boolean optionalTypeMatches =
        typeRelation
            .filter(
                r ->
                    r.equals(OptionalFieldRelation.SAME_TYPE)
                        || r.equals(OptionalFieldRelation.UNWRAP_OPTIONAL))
            .isPresent();

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
