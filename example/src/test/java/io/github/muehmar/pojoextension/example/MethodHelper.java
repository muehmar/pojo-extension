package io.github.muehmar.pojoextension.example;

import java.lang.reflect.Method;
import java.util.stream.Stream;

public class MethodHelper {
  public static final String EQUALS = "equals";
  public static final String GEN_EQUALS = "genEquals";
  public static final String HASH_CODE = "hashCode";
  public static final String GEN_HASH_CODE = "genHashCode";
  public static final String TO_STRING = "toString";
  public static final String GEN_TO_STRING = "genToString";

  public static final String MAP = "map";
  public static final String MAP_IF = "map";
  public static final String MAP_IF_PRESENT = "map";

  private MethodHelper() {}

  public static boolean hasMethod(Class<?> clazz, String methodName) {
    final Method[] declaredMethods = clazz.getDeclaredMethods();
    return Stream.of(declaredMethods).anyMatch(m -> m.getName().equals(methodName));
  }
}
