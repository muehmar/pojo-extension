package io.github.muehmar.pojoextension.data;

public class PojoMember {
  private final Type type;
  private final Name name;
  private final boolean required;

  public PojoMember(Type type, Name name, boolean required) {
    this.type = type;
    this.name = name;
    this.required = required;
  }

  public Type getType() {
    return type;
  }

  public Name getName() {
    return name;
  }

  public boolean isRequired() {
    return required;
  }

  public boolean isOptional() {
    return !isRequired();
  }
}
