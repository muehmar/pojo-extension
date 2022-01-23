package io.github.muehmar.pojoextension;

public class Strings {
  private Strings() {}

  public static String surroundIfNotEmpty(String prefix, String content, String suffix) {
    if (content.isEmpty()) {
      return content;
    } else {
      return String.format("%s%s%s", prefix, content, suffix);
    }
  }

  public static boolean nonEmpty(String str) {
    return str.length() > 0;
  }
}
