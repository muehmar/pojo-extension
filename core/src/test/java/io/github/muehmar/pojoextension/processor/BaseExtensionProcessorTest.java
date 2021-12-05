package io.github.muehmar.pojoextension.processor;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PackageName;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.joor.CompileOptions;
import org.joor.Reflect;
import org.junit.jupiter.api.Assertions;

public abstract class BaseExtensionProcessorTest {
  protected static final PackageName PACKAGE = PackageName.fromString("io.github.muehmar");

  protected static Name randomClassName() {
    return Name.fromString("Customer")
        .append(UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10));
  }

  protected static Name qualifiedClassName(Name className) {
    return className.prefix(".").prefix(PACKAGE.asString());
  }

  protected static PojoAndSettings runAnnotationProcessor(Name name, String content) {
    final AtomicReference<PojoAndSettings> ref = new AtomicReference<>();
    final PojoExtensionProcessor pojoExtensionProcessor =
        new PojoExtensionProcessor(
            ((pojo, settings) -> ref.set(new PojoAndSettings(pojo, settings))));

    try {
      Reflect.compile(
          name.asString(), content, new CompileOptions().processors(pojoExtensionProcessor));
    } catch (Exception e) {
      Assertions.fail("Compilation failed: " + e.getMessage());
    }
    final PojoAndSettings pojoAndSettings = ref.get();
    assertNotNull(pojoAndSettings, "Output not redirected");

    return pojoAndSettings;
  }

  protected static class PojoAndSettings {
    private final Pojo pojo;
    private final PojoSettings settings;

    public PojoAndSettings(Pojo pojo, PojoSettings settings) {
      this.pojo = pojo;
      this.settings = settings;
    }

    public Pojo getPojo() {
      return pojo;
    }

    public PojoSettings getSettings() {
      return settings;
    }
  }
}
