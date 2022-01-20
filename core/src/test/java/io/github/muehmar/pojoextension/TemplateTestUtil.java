package io.github.muehmar.pojoextension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.muehmar.pojoextension.generator.Resources;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;

public class TemplateTestUtil {
  private static final String EXTENSION_TEST_UPDATE_TEMPLATES = "extension.test.updateTemplates";

  private TemplateTestUtil() {}

  public static void assertTemplateEqualsOrUpdate(String template, String output) {
    final boolean updateTemplate =
        Optional.ofNullable(System.getProperty(EXTENSION_TEST_UPDATE_TEMPLATES))
            .map(String::trim)
            .filter(Boolean.TRUE.toString()::equalsIgnoreCase)
            .isPresent();
    if (updateTemplate) {
      writeResourcePojoTemplate(template, output);
    }
    assertEquals(readResourcePojoTemplate(template), output);
  }

  private static String readResourcePojoTemplate(String template) {
    return Resources.readString("/java/pojos/" + template + ".java.template");
  }

  private static void writeResourcePojoTemplate(String template, String output) {
    final String resourcePath = TemplateTestUtil.class.getResource("/").getPath();
    final int buildStart = resourcePath.lastIndexOf("build");
    final String templatePath =
        String.format(
            "%ssrc/test/resources/java/pojos/%s.java.template",
            resourcePath.substring(0, buildStart), template);
    try {
      final BufferedWriter writer = new BufferedWriter(new FileWriter(templatePath));
      writer.write(output);
      writer.close();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
