package io.github.muehmar.pojoextension.generator.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.model.Name;
import org.junit.jupiter.api.Test;

class ClassnameTest {

  @Test
  void getOuterClassname_when_innerClass_then_outerClassnameReturned() {
    final Classname classname = Classname.fromFullClassName("Customer.Address.Street");

    assertEquals(Name.fromString("Customer"), classname.getOuterClassname());
    assertEquals("Customer.Address.Street", classname.asName().asString());
  }

  @Test
  void getOuterClassname_when_noInnerClass_then_classnameReturned() {
    final Classname classname = Classname.fromFullClassName("Customer");

    assertEquals(Name.fromString("Customer"), classname.getOuterClassname());
  }
}
