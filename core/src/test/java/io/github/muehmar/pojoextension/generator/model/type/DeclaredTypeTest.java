package io.github.muehmar.pojoextension.generator.model.type;

import static org.junit.jupiter.api.Assertions.*;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.PackageName;
import org.junit.jupiter.api.Test;

class DeclaredTypeTest {

  @Test
  void getTypeDeclaration_when_javaMap_then_correctNameReturned() {
    final DeclaredType declaredType =
        DeclaredType.of(
            Name.fromString("Map"),
            PackageName.javaUtil(),
            PList.of(Types.string(), Types.integer()));
    assertEquals("Map<String,Integer>", declaredType.getTypeDeclaration().asString());
  }
}
