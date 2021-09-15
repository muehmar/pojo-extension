package io.github.muehmar.pojoextension.data;

import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PojoMember that = (PojoMember) o;
    return required == that.required
        && Objects.equals(type, that.type)
        && Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, name, required);
  }

  @Override
  public String toString() {
    return "PojoMember{" + "type=" + type + ", name=" + name + ", required=" + required + '}';
  }
}
