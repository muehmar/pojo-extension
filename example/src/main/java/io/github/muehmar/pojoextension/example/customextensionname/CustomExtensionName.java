package io.github.muehmar.pojoextension.example.customextensionname;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import lombok.Value;

@Value
@PojoExtension(extensionName = "{CLASSNAME}Class")
public class CustomExtensionName implements CustomExtensionNameClass {
  String name;
}
