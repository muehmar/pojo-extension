package io.github.muehmar.pojoextension.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class GenericClassTest {
  @Test
  void createInGenericClassBuilder_when_calledAndBuilderUsed_then_correctInstanceCreated() {
    final Data data = new Data();
    data.add("Hello World!");

    final GenericClass<Data, String> genericClass =
        GenericClassBuilder.<Data, String>create().prop1("prop1").data(data).code(Code.A1).build();

    assertEquals("Hello World!", genericClass.getData().get(0));
    assertEquals("prop1", genericClass.getProp1());
    assertEquals(Code.A1, genericClass.getCode());
  }

  @Test
  void withProp1_when_called_then_prop1Changed() {
    assertEquals("prop1Changed", sampleInstance().withProp1("prop1Changed").getProp1());
  }

  @Test
  void map_when_called_then_mapFunctionApplied() {
    final GenericClass<Data, String> genericClass = sampleInstance();
    final Data data = genericClass.map(GenericClass::getData);
    assertEquals("Hello World!", data.get(0));
  }

  public static class Data extends ArrayList<String> implements Comparable<Data> {
    @Override
    public int compareTo(Data o) {
      return 0;
    }

    @Override
    public String toString() {
      return "Data{}";
    }
  }

  private static GenericClass<Data, String> sampleInstance() {
    final Data data = new Data();
    data.add("Hello World!");
    return GenericClassBuilder.<Data, String>create()
        .prop1("prop1")
        .data(data)
        .code(Code.A2)
        .andAllOptionals()
        .additionalData("additionalData")
        .build();
  }

  private enum Code {
    A1,
    A2
  }
}
