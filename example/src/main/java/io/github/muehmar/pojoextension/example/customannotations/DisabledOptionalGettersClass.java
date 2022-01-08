package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;

@PojoExtension(enableOptionalGetters = false)
@SuppressWarnings("java:S2160")
public class DisabledOptionalGettersClass extends DisabledOptionalGettersClassBase {
  private final Optional<String> prop;

  public DisabledOptionalGettersClass(Optional<String> prop) {
    this.prop = prop;
  }

  public Optional<String> getProp() {
    return prop;
  }
}
