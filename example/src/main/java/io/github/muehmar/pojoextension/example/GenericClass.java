package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.List;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension
public class GenericClass<T extends List<String> & Comparable<T>, S>
    implements GenericClassExtension<T, S> {
  String prop1;
  T data;
  Optional<S> additionalData;
  Enum<?> code;
}
