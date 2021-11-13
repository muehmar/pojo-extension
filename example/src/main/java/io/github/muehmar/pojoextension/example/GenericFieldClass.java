package io.github.muehmar.pojoextension.example;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Map;

@PojoExtension
@SuppressWarnings("java:S2160")
public class GenericFieldClass extends GenericFieldClassExtension {
  private final Map<String, PList<Integer>> listMap;

  public GenericFieldClass(Map<String, PList<Integer>> listMap) {
    this.listMap = listMap;
  }

  public Map<String, PList<Integer>> getListMap() {
    return listMap;
  }
}
