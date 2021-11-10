package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import io.github.muehmar.pojoextension.annotations.PojoExtension;

@PojoExtension(optionalDetection = OptionalDetection.NONE)
public @interface AllRequiredExtension {}
