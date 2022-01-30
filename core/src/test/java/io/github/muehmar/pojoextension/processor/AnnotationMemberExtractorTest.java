package io.github.muehmar.pojoextension.processor;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.annotations.RecordExtension;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class AnnotationMemberExtractorTest {
  @Test
  void PojoExtensionContainsAllMethods() {
    final Class<?> clazz = PojoExtension.class;

    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.OPTIONAL_DETECTION));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.EXTENSION_NAME));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_SAFE_BUILDER));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.BUILDER_NAME));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.BUILDER_SET_METHOD_PREFIX));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.BASE_CLASS_NAME));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_BASE_CLASS));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_EQUALS_AND_HASH_CODE));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_TO_STRING));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_WITHERS));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_OPTIONAL_GETTERS));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_MAPPERS));
  }

  @Test
  void RecordExtensionContainsAllMethods() {
    final Class<?> clazz = RecordExtension.class;

    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.OPTIONAL_DETECTION));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.EXTENSION_NAME));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_SAFE_BUILDER));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.BUILDER_NAME));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.BUILDER_SET_METHOD_PREFIX));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_OPTIONAL_GETTERS));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.ENABLE_MAPPERS));
  }

  @Test
  void SafeBuilderContainsAllMethods() {
    final Class<?> clazz = SafeBuilder.class;

    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.OPTIONAL_DETECTION));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.BUILDER_NAME));
    assertTrue(hasMethod(clazz, AnnotationMemberExtractor.BUILDER_SET_METHOD_PREFIX));
  }

  private static boolean hasMethod(Class<?> clazz, String methodName) {
    final Method[] declaredMethods = clazz.getDeclaredMethods();
    return Stream.of(declaredMethods).anyMatch(m -> m.getName().equals(methodName));
  }
}
