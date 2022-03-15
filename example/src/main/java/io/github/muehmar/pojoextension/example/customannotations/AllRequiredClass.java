package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.Nullable;
import java.util.Optional;
import lombok.Value;

@Value
@AllRequiredExtension(extensionName = "CustomExtension")
public class AllRequiredClass implements CustomExtension {
  String id;
  Optional<Boolean> flag;
  @Nullable Integer age;
}
