package io.github.muehmar.pojoextension.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class CustomerTest {
  @Test
  void newBuilder_when_usedToCreateInstance_then_allAttributesSetAccordingly() {
    final Customer customer =
        Customer.newBuilder()
            .setId("123456")
            .setName("Dexter")
            .setRandom(12.5d)
            .setKey(new byte[] {0x15})
            .andAllOptionals()
            .setNickname("Dex")
            .setAge(Optional.empty())
            .build();

    assertEquals("123456", customer.getId());
    assertEquals("Dexter", customer.getName());
    assertEquals(12.5d, customer.getRandom());
    assertArrayEquals(new byte[] {0x15}, customer.getKey());
    assertEquals(Optional.of("Dex"), customer.getNickname());
    assertEquals(Optional.empty(), customer.getAge());
  }
}
