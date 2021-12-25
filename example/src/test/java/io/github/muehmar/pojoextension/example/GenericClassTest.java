package io.github.muehmar.pojoextension.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class GenericClassTest {
  @Test
  void createInGenericClassBuilder_when_calledAndBuilderUsed_then_correctInstanceCreated() {
    final Data data = new Data();
    data.add("Hello World!");

    final GenericClass<Data> genericClass =
        GenericClassBuilder.<Data>create().setProp1("prop1").setData(data).build();

    assertEquals("Hello World!", genericClass.getData().get(0));
    assertEquals("prop1", genericClass.getProp1());
  }

  public static class Data extends ArrayList<String> implements Comparable<Data> {
    @Override
    public int compareTo(Data o) {
      return 0;
    }
  }
}
