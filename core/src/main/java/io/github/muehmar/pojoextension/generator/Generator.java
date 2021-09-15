package io.github.muehmar.pojoextension.generator;

import io.github.muehmar.pojoextension.data.Pojo;

public interface Generator {
  String generate(Pojo pojo, PojoSettings settings);
}
