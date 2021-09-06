package io.github.muehmar.pojoextension.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Class description. Prefixed with 'Dsc' to avoid confusion with java.lang.Class. */
public class ClassDsc {
  private final PackageName pkg;
  private final Name name;

  private static final Pattern classPattern = Pattern.compile("(.*)\\.([A-Za-z_$0-9]*)$");

  public ClassDsc(PackageName pkg, Name className) {
    this.pkg = pkg;
    this.name = className;
  }

  public static ClassDsc fromFullClassName(String className) {
    final Matcher matcher = classPattern.matcher(className);
    if (matcher.find()) {
      return new ClassDsc(PackageName.fromString(matcher.group(1)), Name.of(matcher.group(2)));
    }

    throw new IllegalArgumentException("Not a valid full classname: " + className);
  }

  public PackageName getPkg() {
    return pkg;
  }

  public Name getName() {
    return name;
  }
}
