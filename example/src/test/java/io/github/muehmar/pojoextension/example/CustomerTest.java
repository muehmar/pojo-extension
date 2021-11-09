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
            .setFlag(true)
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
            customer1.getKey(),
            customer1.isFlag()));
  }

  @Test
  void equals_when_oneFieldChanged_then_notEquals() {
    final Customer customer1 = sampleCustomer();
    assertNotEquals(customer1, customer1.withId("id"));
    assertNotEquals(customer1, customer1.withName("name"));
    assertNotEquals(customer1, customer1.withNickname("nickname"));
    assertNotEquals(customer1, customer1.withAge(15));
    assertNotEquals(customer1, customer1.withRandom(987L));
    assertNotEquals(customer1, customer1.withKey(new byte[] {0x7E}));
  }

  @Test
  void with_when_calledWithTheSameValue_then_stillEquals() {
    final Customer c = sampleCustomer();
    assertEquals(c, c.withId(c.getId()));
    assertEquals(c, c.withName(c.getName()));
    assertEquals(c, c.withNickname(c.getNickname().orElseThrow(IllegalStateException::new)));
    assertEquals(c, c.withRandom(c.getRandom()));
    assertEquals(c, c.withKey(c.getKey()));
  }

  @Test
  void with_when_calledWithOverloadedOptional_then_stillEquals() {
    final Customer c = sampleCustomer();
    assertEquals(c, c.withNickname(c.getNickname()));
    assertEquals(c, c.withAge(c.getAge()));
    assertEquals(c.withAge(15), c.withAge(Optional.of(15)));
    assertEquals(c.withNickname("nick"), c.withNickname(Optional.of("nick")));
  }

  private static Customer sampleCustomer() {
    return Customer.newBuilder()
        .setId("123456")
        .setName("Dexter")
        .setRandom(12.5d)
        .setKey(new byte[] {0x15})
        .setFlag(true)
        .andAllOptionals()
        .setNickname("Dex")
        .setAge(Optional.empty())
        .build();
  }
}
