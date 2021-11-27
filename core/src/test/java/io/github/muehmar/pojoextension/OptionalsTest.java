package io.github.muehmar.pojoextension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class OptionalsTest {
  @Test
  void ifPresentOrElse_when_empty_then_orElseRunnableCalled() {
    final AtomicBoolean runnableCalled = new AtomicBoolean(false);
    final AtomicBoolean consumerCalled = new AtomicBoolean(false);
    final Optional<Integer> value = Optional.empty();
    Optionals.ifPresentOrElse(
        value, ignore -> consumerCalled.set(true), () -> runnableCalled.set(true));

    assertTrue(runnableCalled.get());
    assertFalse(consumerCalled.get());
  }

  @Test
  void ifPresentOrElse_when_valuePresent_then_consumerWithValueCalled() {
    final AtomicBoolean runnableCalled = new AtomicBoolean(false);
    final AtomicInteger consumerValue = new AtomicInteger(0);
    final Optional<Integer> value = Optional.of(10);
    Optionals.ifPresentOrElse(value, consumerValue::set, () -> runnableCalled.set(true));

    assertFalse(runnableCalled.get());
    assertEquals(10, consumerValue.get());
  }
}
