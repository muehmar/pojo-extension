package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.List;
import java.util.Map;

@PojoExtension
@SuppressWarnings("java:S2160")
public class GenericFieldClass extends GenericFieldClassBase {
  private final Map<String, List<Integer>> listMap;

  public GenericFieldClass(Map<String, List<Integer>> listMap) {
    this.listMap = listMap;
  }

  public Map<String, List<Integer>> getListMap() {
    return listMap;
  }
}
