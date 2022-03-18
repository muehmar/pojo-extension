package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.List;
import java.util.Map;
import lombok.Value;

@Value
@PojoExtension
public class GenericFieldClass implements GenericFieldClassExtension {
  Map<String, List<Integer>> listMap;
}
