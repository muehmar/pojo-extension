package io.github.muehmar.pojoextension.example;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class CustomerTest {

  public static final String SAMPLE_ID = "123456";

  @Test
  void newBuilder_when_usedToCreateInstance_then_allAttributesSetAccordingly() {
    final Customer customer =
        CustomerBuilder.create()
            .setId(SAMPLE_ID)
            .setName("Dexter")
            .setRandom(12.5d)
            .setKey(new byte[] {0x15})
            .setFlag(true)
            .andAllOptionals()
            .setNickname("Dex")
            .setAge(empty())
            .build();

    assertEquals(SAMPLE_ID, customer.getIdentification());
    assertEquals("Dexter", customer.getName());
    assertEquals(12.5d, customer.getRandom());
    assertArrayEquals(new byte[] {0x15}, customer.getKey());
    assertEquals(Optional.of("Dex"), customer.getNick());
    assertEquals(empty(), customer.getAge());
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
            customer1.getIdentification(),
            customer1.getName(),
            customer1.getNick().orElse(null),
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
    assertEquals(c, c.withId(c.getIdentification()));
    assertEquals(c, c.withName(c.getName()));
    assertEquals(c, c.withNickname(c.getNick().orElseThrow(IllegalStateException::new)));
    assertEquals(c, c.withRandom(c.getRandom()));
    assertEquals(c, c.withKey(c.getKey()));
  }

  @Test
  void with_when_calledWithOverloadedOptional_then_stillEquals() {
    final Customer c = sampleCustomer();
    assertEquals(c, c.withNickname(c.getNick()));
    assertEquals(c, c.withAge(c.getAge()));
    assertEquals(c.withAge(15), c.withAge(Optional.of(15)));
    assertEquals(c.withNickname("nick"), c.withNickname(Optional.of("nick")));
  }

  @Test
  void newBuilder_when_calledForAddress_then_correctInstanceCreated() {
    final Customer.Address address =
        CustomerAddressBuilder.create()
            .street("Waldweg 10")
            .city("Winterthur")
            .andAllOptionals()
            .build();

    assertEquals("Waldweg 10", address.getStreet());
    assertEquals("Winterthur", address.getCity());
  }

  @Test
  void getAgeOr_when_noAgePresent_then_defaultValueReturned() {
    final Customer customer = sampleCustomer().withAge(empty());
    assertEquals(55, customer.getAgeOr(55));
  }

  @Test
  void getAgeOr_when_agePresent_then_actualAgeReturned() {
    final Customer customer = sampleCustomer().withAge(28);
    assertEquals(28, customer.getAgeOr(55));
  }

  @Test
  void map_when_calledForCustomer_then_mapFunctionApplied() {
    final Customer customer = sampleCustomer().map(c -> c.withId(SAMPLE_ID + "99"));
    assertEquals(SAMPLE_ID + "99", customer.getIdentification());
  }

  @Test
  void mapIf_when_shouldNotMap_then_mapFunctionNotApplied() {
    final Customer customer = sampleCustomer().mapIf(false, c -> c.withId(SAMPLE_ID + "99"));
    assertEquals(sampleCustomer(), customer);
  }

  @Test
  void mapIf_when_shouldMap_then_mapFunctionApplied() {
    final Customer customer = sampleCustomer().mapIf(true, c -> c.withId(SAMPLE_ID + "99"));
    assertEquals(SAMPLE_ID + "99", customer.getIdentification());
  }

  @Test
  void mapIfPresent_when_valuePresent_then_mapFunctionApplied() {
    final Customer customer =
        sampleCustomer().mapIfPresent(Optional.of(SAMPLE_ID + "99"), CustomerExtension::withId);
    assertEquals(SAMPLE_ID + "99", customer.getIdentification());
  }

  @Test
  void mapIfPresent_when_valueNotPresent_then_mapFunctionNotApplied() {
    final Customer customer =
        sampleCustomer().mapIfPresent(Optional.<String>empty(), CustomerExtension::withId);
    assertEquals(sampleCustomer(), customer);
  }

  @Test
  void toString_when_calledForPopulatedCustomer_then_correctString() {
    final Customer customer = sampleCustomer();
    assertEquals(
        "Customer{id='123456', name='Dexter', nickname=Optional[Dex], age=Optional.empty, random=12.5, key=[21], flag=true}",
        customer.toString());
  }

  private static Customer sampleCustomer() {
    return CustomerBuilder.create()
        .setId(SAMPLE_ID)
        .setName("Dexter")
        .setRandom(12.5d)
        .setKey(new byte[] {0x15})
        .setFlag(true)
        .andAllOptionals()
        .setNickname("Dex")
        .setAge(empty())
        .build();
  }
}
