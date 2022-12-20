package io.github.muehmar.pojoextension.generator.model.type;

import io.github.muehmar.pojoextension.generator.model.PackageName;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Value;

public class ClassnameParser {
  private static final String PACKAGE_NAME_PATTERN = "[a-z][A-Za-z0-9_$]*";
  private static final String IDENTIFIER_PATTERN = "[A-Z][A-Za-z0-9_$]*";

  private static final Pattern QUALIFIED_CLASS_NAME_PATTERN =
      Pattern.compile(
          String.format(
              "^(?:(%s(?:\\.%s)*)\\.)?(%s(?:\\.%s)*)",
              PACKAGE_NAME_PATTERN, PACKAGE_NAME_PATTERN, IDENTIFIER_PATTERN, IDENTIFIER_PATTERN));

  private ClassnameParser() {}

  public static NameAndPackage parseThrowing(String classname) {
    return parse(classname)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Class "
                        + classname
                        + " cannot be parsed. It must match "
                        + QUALIFIED_CLASS_NAME_PATTERN.pattern()));
  }

  public static Optional<NameAndPackage> parse(String classname) {
    final Matcher matcher = QUALIFIED_CLASS_NAME_PATTERN.matcher(classname);
    if (matcher.find()) {
      final Classname name = Classname.fromFullClassName(matcher.group(2));
      final Optional<PackageName> packageName =
          Optional.ofNullable(matcher.group(1)).map(PackageName::fromString);
      return Optional.of(new NameAndPackage(name, packageName));
    }

    return Optional.empty();
  }

  @Value
  public static class NameAndPackage {
    Classname classname;
    Optional<PackageName> pkg;

    public NameAndPackage(Classname classname, Optional<PackageName> pkg) {
      this.classname = classname;
      this.pkg = pkg;
    }
  }
}
