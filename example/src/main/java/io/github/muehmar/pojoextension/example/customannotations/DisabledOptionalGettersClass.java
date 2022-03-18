package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension(enableOptionalGetters = false)
public class DisabledOptionalGettersClass implements DisabledOptionalGettersClassExtension {
  Optional<String> prop;
}
