package io.github.muehmar.pojoextension.generator;

import io.github.muehmar.pojoextension.data.Name;

public interface Resolver {
  Name getterName(Name name);

  Name setterName(Name name);

  Name witherName(Name name);

  Name memberName(Name name);

  Name className(Name name);

  Name enumName(Name name);

  Name enumMemberName(Name name);
}
