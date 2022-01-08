package io.github.muehmar.pojoextension.processor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.annotations.RecordExtension;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AnnotationValueExtractorTest {
  @Test
  void PojoExtensionContainsAllMethods() {
    final Class<?> clazz = PojoExtension.class;

    assertTrue(hasMethod(clazz, AnnotationValueExtractor.OPTIONAL_DETECTION));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.EXTENSION_NAME));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_SAFE_BUILDER));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.BUILDER_NAME));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.BUILDER_SET_METHOD_PREFIX));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.BASE_CLASS_NAME));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_BASE_CLASS));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_EQUALS_AND_HASH_CODE));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_TO_STRING));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_WITHERS));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_OPTIONAL_GETTERS));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_MAPPERS));
  }

  @Test
  void RecordExtensionContainsAllMethods() {
    final Class<?> clazz = RecordExtension.class;

    assertTrue(hasMethod(clazz, AnnotationValueExtractor.OPTIONAL_DETECTION));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.EXTENSION_NAME));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_SAFE_BUILDER));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.BUILDER_NAME));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.BUILDER_SET_METHOD_PREFIX));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_OPTIONAL_GETTERS));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.ENABLE_MAPPERS));
  }

  @Test
  void SafeBuilderContainsAllMethods() {
    final Class<?> clazz = SafeBuilder.class;

    assertTrue(hasMethod(clazz, AnnotationValueExtractor.OPTIONAL_DETECTION));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.BUILDER_NAME));
    assertTrue(hasMethod(clazz, AnnotationValueExtractor.BUILDER_SET_METHOD_PREFIX));
  }

  private static boolean hasMethod(Class<?> clazz, String methodName) {
    final Method[] declaredMethods = clazz.getDeclaredMethods();
    return Stream.of(declaredMethods).anyMatch(m -> m.getName().equals(methodName));
  }
}
