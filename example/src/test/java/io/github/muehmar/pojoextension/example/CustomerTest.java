package io.github.muehmar.pojoextension.example;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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

  @Test
  void equals_when_eitherSameInstanceOrWithSameFields_then_equals() {
    final Customer customer1 = sampleCustomer();
    final Customer customer2 = sampleCustomer();

    assertEquals(customer1, customer1);
    assertEquals(customer1, customer2);
    assertEquals(
        customer1,
        new Customer(
            customer1.getId(),
            customer1.getName(),
            customer1.getNickname().orElse(null),
            customer1.getAge().orElse(null),
            customer1.getRandom(),
            customer1.getKey()));
  }

  @Test
  void equals_when_oneFieldChanged_then_notEquals() {
    final Customer customer1 = sampleCustomer();
    assertNotEquals(
        customer1,
        new Customer(
            "id",
            customer1.getName(),
            customer1.getNickname().orElse(null),
            customer1.getAge().orElse(null),
            customer1.getRandom(),
            customer1.getKey()));
    assertNotEquals(
        customer1,
        new Customer(
            customer1.getId(),
            "name",
            customer1.getNickname().orElse(null),
            customer1.getAge().orElse(null),
            customer1.getRandom(),
            customer1.getKey()));
    assertNotEquals(
        customer1,
        new Customer(
            customer1.getId(),
            customer1.getName(),
            "nickname",
            customer1.getAge().orElse(null),
            customer1.getRandom(),
            customer1.getKey()));
    assertNotEquals(
        customer1,
        new Customer(
            customer1.getId(),
            customer1.getName(),
            customer1.getNickname().orElse(null),
            15,
            customer1.getRandom(),
            customer1.getKey()));
    assertNotEquals(
        customer1,
        new Customer(
            customer1.getId(),
            customer1.getName(),
            customer1.getNickname().orElse(null),
            customer1.getAge().orElse(null),
            987L,
            customer1.getKey()));
    assertNotEquals(
        customer1,
        new Customer(
            customer1.getId(),
            customer1.getName(),
            customer1.getNickname().orElse(null),
            customer1.getAge().orElse(null),
            customer1.getRandom(),
            new byte[] {0x7E}));
  }

  private static Customer sampleCustomer() {
    return Customer.newBuilder()
        .setId("123456")
        .setName("Dexter")
        .setRandom(12.5d)
        .setKey(new byte[] {0x15})
        .andAllOptionals()
        .setNickname("Dex")
        .setAge(Optional.empty())
        .build();
  }
}
