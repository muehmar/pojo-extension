package io.github.muehmar.pojoextension.example.customannotations;

public final class DisabledToStringClassFieldBuilder {

  private DisabledToStringClassFieldBuilder() {}

  public static Builder0 create() {
    return new Builder0(new Builder());
  }

  public static final class Builder {
    private Builder() {}

    private String prop1;

    private Builder prop1(String prop1) {
      this.prop1 = prop1;
      return this;
    }

    public DisabledToStringClass build() {
      return new DisabledToStringClass(prop1);
    }
  }

  public static final class Builder0 {
    private final Builder builder;

    private Builder0(Builder builder) {
      this.builder = builder;
    }

    public Builder1 prop1(String prop1) {
      return new Builder1(builder.prop1(prop1));
    }

    public Builder1 noProp() {
      return new Builder1(builder.prop1(DisabledToStringClass.emptyProp()));
    }
  }

  public static final class Builder1 {
    private final Builder builder;

    private Builder1(Builder builder) {
      this.builder = builder;
    }

    public OptBuilder0 andAllOptionals() {
      return new OptBuilder0(builder);
    }

    public Builder andOptionals() {
      return builder;
    }

    public DisabledToStringClass build() {
      return builder.build();
    }
  }

  public static final class OptBuilder0 {
    private final Builder builder;

    private OptBuilder0(Builder builder) {
      this.builder = builder;
    }

    public DisabledToStringClass build() {
      return builder.build();
    }
  }
}
