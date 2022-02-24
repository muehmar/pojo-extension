package io.github.muehmar.pojoextension.generator.data.type;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.UNWRAP_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.WRAP_INTO_OPTIONAL;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation;
import java.util.Optional;
import java.util.function.Function;

@PojoExtension
@SuppressWarnings("java:S2160")
public class Type extends TypeBase {
  private final SpecificType specificType;

  Type(SpecificType specificType) {
    this.specificType = specificType;
  }

  public SpecificType getSpecificType() {
    return specificType;
  }

  public static Type fromSpecificType(SpecificType specificType) {
    return new Type(specificType);
  }

  /** Returns the type declaration of this type, i.e. including type parameters. */
  public Name getTypeDeclaration() {
    return specificType.getTypeDeclaration();
  }

  /** Returns all qualified types used for imports. */
  public PList<Name> getImports() {
    return specificType.getImports();
  }

  public Name getName() {
    return specificType.getName();
  }

  public boolean isArray() {
    return specificType.getKind().equals(TypeKind.ARRAY);
  }

  public boolean isNotArray() {
    return !isArray();
  }

  public boolean isPrimitive() {
    return specificType.getKind().equals(TypeKind.PRIMITIVE);
  }

  public boolean isVoid() {
    return Types.voidType().equals(this);
  }

  public boolean isOptional() {
    return fold(DeclaredType::isOptional, ignore -> false, ignore -> false, ignore -> false);
  }

  public <T> T fold(
      Function<DeclaredType, T> onDeclaredType,
      Function<ArrayType, T> onArray,
      Function<PrimitiveType, T> onPrimitive,
      Function<TypeVariableType, T> onTypeVariable) {
    if (specificType instanceof DeclaredType) {
      return onDeclaredType.apply((DeclaredType) specificType);
    } else if (specificType instanceof ArrayType) {
      return onArray.apply((ArrayType) specificType);
    } else if (specificType instanceof PrimitiveType) {
      return onPrimitive.apply((PrimitiveType) specificType);
    } else if (specificType instanceof TypeVariableType) {
      return onTypeVariable.apply((TypeVariableType) specificType);
    } else {
      throw new IllegalStateException(
          "Specific type " + specificType.getClass() + " not handled when folding.");
    }
  }

  public <T> Optional<T> onDeclaredType(Function<DeclaredType, T> onDeclaredType) {
    return fold(
        decType -> Optional.of(onDeclaredType.apply(decType)),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  public Optional<OptionalFieldRelation> getRelation(Type other) {
    if (this.equals(other)) {
      return Optional.of(SAME_TYPE);
    } else if (isOptional() && this.equals(Types.optional(other))) {
      return Optional.of(UNWRAP_OPTIONAL);
    } else if (other.isOptional() && other.equals(Types.optional(this))) {
      return Optional.of(WRAP_INTO_OPTIONAL);
    } else {
      return Optional.empty();
    }
  }
}
