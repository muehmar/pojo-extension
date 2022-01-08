package io.github.muehmar.pojoextension.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserTest {
  @Test
  void newBuilder_when_usedToCreateInstanceEmptyOptional_then_allAttributesSetAccordingly() {
    final User user =
        UserBuilder.create().name("Joe").andAllOptionals().age(Optional.empty()).build();

    assertEquals("Joe", user.getName());
    assertEquals(Optional.empty(), user.getAge());
  }

  @Test
  void newBuilder_when_usedToCreateInstanceSetOptional_then_allAttributesSetAccordingly() {
    final User user = UserBuilder.create().name("Joe").andAllOptionals().age(50).build();

    assertEquals("Joe", user.getName());
    assertEquals(Optional.of(50), user.getAge());
  }
}
