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
        GenericClassBuilder.<Data, String>create().setProp1("prop1").setData(data).build();

    assertEquals("Hello World!", genericClass.getData().get(0));
    assertEquals("prop1", genericClass.getProp1());
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

  @Test
  void equals_when_calledWithTwoSampleInstances_then_equals() {
    assertEquals(sampleInstance(), sampleInstance());
  }

  @Test
  void toString_when_sampleInstance_then_correctOutput() {
    assertEquals(
        "GenericClass{prop1='prop1', data=Data{}, additionalData=Optional[additionalData]}",
        sampleInstance().toString());
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
        .setProp1("prop1")
        .setData(data)
        .andAllOptionals()
        .setAdditionalData("additionalData")
        .build();
  }
}
