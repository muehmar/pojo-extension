package io.github.muehmar.pojoextension.generator.data;

import java.util.EnumMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An optional field may be nullable or wrapped into an optional. This enum describes the relation
 * of the type of the field and the return type of the corresponding getter or the argument in the
 * constructor.
 */
public enum OptionalFieldRelation {
  /** The origin is wrapped into an {@link Optional} and the target is nullable. */
  UNWRAP_OPTIONAL {
    @Override
    protected EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> getTransitionMap() {
      final EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> map =
          new EnumMap<>(OptionalFieldRelation.class);
      map.put(SAME_TYPE, () -> UNWRAP_OPTIONAL);
      map.put(WRAP_INTO_OPTIONAL, () -> SAME_TYPE);
      map.put(
          UNWRAP_OPTIONAL,
          () -> {
            throw new IllegalStateException("Cannot unwrap optional two times!");
          });
      return map;
    }

    @Override
    public <A, B> B apply(
        A in,
        OnUnwrapOptional<A, B> onUnwrapOptional,
        OnSameType<A, B> onSameType,
        OnWrapOptional<A, B> onWrapOptional) {
      return onUnwrapOptional.apply(in);
    }
  },
  /**
   * The origin and target have the same type, either both nullable or wrapped into an {@link
   * Optional}.
   */
  SAME_TYPE {
    @Override
    protected EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> getTransitionMap() {
      final EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> map =
          new EnumMap<>(OptionalFieldRelation.class);
      map.put(SAME_TYPE, () -> SAME_TYPE);
      map.put(WRAP_INTO_OPTIONAL, () -> WRAP_INTO_OPTIONAL);
      map.put(UNWRAP_OPTIONAL, () -> UNWRAP_OPTIONAL);
      return map;
    }

    @Override
    public <A, B> B apply(
        A in,
        OnUnwrapOptional<A, B> onUnwrapOptional,
        OnSameType<A, B> onSameType,
        OnWrapOptional<A, B> onWrapOptional) {
      return onSameType.apply(in);
    }
  },
  /** The origin is nullable and the target wrapped into an {@link Optional}. */
  WRAP_INTO_OPTIONAL {
    @Override
    protected EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> getTransitionMap() {
      final EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> map =
          new EnumMap<>(OptionalFieldRelation.class);
      map.put(SAME_TYPE, () -> WRAP_INTO_OPTIONAL);
      map.put(UNWRAP_OPTIONAL, () -> SAME_TYPE);
      map.put(
          WRAP_INTO_OPTIONAL,
          () -> {
            throw new IllegalStateException("Cannot wrap into optional two times!");
          });
      return map;
    }

    @Override
    public <A, B> B apply(
        A in,
        OnUnwrapOptional<A, B> onUnwrapOptional,
        OnSameType<A, B> onSameType,
        OnWrapOptional<A, B> onWrapOptional) {
      return onWrapOptional.apply(in);
    }
  };

  /**
   * Returns the relation from A to C when {@code this} describes the relation from A to B and
   * {@code next} the relation from B to C.
   */
  public OptionalFieldRelation andThen(OptionalFieldRelation next) {
    final EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>> transitionMap =
        getTransitionMap();
    return Optional.ofNullable(transitionMap.get(next))
        .map(Supplier::get)
        .orElseThrow(
            () -> new IllegalStateException("No transition defined from" + this + " to " + next));
  }

  protected abstract EnumMap<OptionalFieldRelation, Supplier<OptionalFieldRelation>>
      getTransitionMap();

  /** Executes the corresponding function with the given input depending on the current relation. */
  public abstract <A, B> B apply(
      A in,
      OnUnwrapOptional<A, B> onUnwrapOptional,
      OnSameType<A, B> onSameType,
      OnWrapOptional<A, B> onWrapOptional);

  public interface OnUnwrapOptional<A, B> extends Function<A, B> {}

  public interface OnSameType<A, B> extends Function<A, B> {}

  public interface OnWrapOptional<A, B> extends Function<A, B> {}
}
