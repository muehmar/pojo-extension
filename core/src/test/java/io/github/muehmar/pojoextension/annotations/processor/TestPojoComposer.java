package io.github.muehmar.pojoextension.annotations.processor;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.data.Name;
import io.github.muehmar.pojoextension.data.PackageName;

public class TestPojoComposer {
  private TestPojoComposer() {}

  public static Imports ofPackage(String pkg) {
    final StringBuilder builder = new StringBuilder();
    builder.append(String.format("package %s;\n", pkg));
    return new Imports(builder);
  }

  public static Imports ofPackage(PackageName pkg) {
    return ofPackage(pkg.asString());
  }

  public static class Imports {
    private final StringBuilder builder;

    private Imports(StringBuilder builder) {
      this.builder = builder;
    }

    public Imports withImport(Class<?> clazz) {
      return withImport(clazz.getName());
    }

    public Imports withImport(String clazz) {
      builder.append(String.format("import %s;\n", clazz));
      return this;
    }

    public <T extends Enum<T>> ClassAnnotations annotation(
        Class<?> annotation, String metaName, Class<T> metaType, T metaValue) {
      return new ClassAnnotations(builder).annotation(annotation, metaName, metaType, metaValue);
    }

    public ClassAnnotations annotation(Class<?> annotation) {
      return new ClassAnnotations(builder).annotation(annotation);
    }

    public PojoFields className(String name) {
      return new ClassName(builder).className(name);
    }

    public PojoFields className(Name name) {
      return new ClassName(builder).className(name);
    }
  }

  public static class ClassAnnotations {
    private final StringBuilder builder;

    public ClassAnnotations(StringBuilder builder) {
      this.builder = builder;
    }

    public ClassAnnotations annotation(Class<?> annotation) {
      builder.append(String.format("@%s\n", annotation.getSimpleName()));
      return this;
    }

    public <T extends Enum<T>> ClassAnnotations annotation(
        Class<?> annotation, String metaName, Class<T> metaType, T metaValue) {
      builder.append(
          String.format(
              "@%s(%s = %s.%s)\n",
              annotation.getSimpleName(), metaName, metaType.getName(), metaValue.name()));
      return this;
    }

    public PojoFields className(String name) {
      return new ClassName(builder).className(name);
    }

    public PojoFields className(Name name) {
      return new ClassName(builder).className(name);
    }
  }

  public static class ClassName {
    private final StringBuilder builder;

    public ClassName(StringBuilder builder) {
      this.builder = builder;
    }

    public PojoFields className(String name) {
      builder.append(String.format("public class %s {\n", name));
      return new PojoFields(builder, name);
    }

    public PojoFields className(Name name) {
      return className(name.asString());
    }
  }

  public static class PojoFields {
    private final StringBuilder builder;
    private final String className;
    private final PList<TypeAndName> fields;

    private PojoFields(StringBuilder builder, String className, PList<TypeAndName> fields) {
      this.builder = builder;
      this.className = className;
      this.fields = fields;
    }

    private PojoFields(StringBuilder builder, String className) {
      this.builder = builder;
      this.className = className;
      this.fields = PList.empty();
    }

    public PojoFields withField(String type, String name) {
      builder.append(String.format("  private final %s %s;\n", type, name));
      return new PojoFields(builder, className, fields.add(new TypeAndName(type, name)));
    }

    public PojoFields withField(String type, String name, Class<?> annotation) {
      builder.append(String.format("  @%s\n", annotation.getSimpleName()));
      builder.append(String.format("  private final %s %s;\n", type, name));
      return new PojoFields(builder, className, fields.add(new TypeAndName(type, name)));
    }

    public PojoConstructor constructor() {
      final String args =
          fields
              .map(TypeAndName::unwrapOptionalClass)
              .map(tan -> String.format("%s %s", tan.type, tan.name))
              .mkString(",");
      builder.append(String.format("  public %s(%s) {\n", className, args));
      fields.forEach(
          tan -> {
            if (tan.isOptionalClass()) {
              builder.append(
                  String.format("    this.%s = Optional.ofNullable(%s);\n", tan.name, tan.name));
            } else {
              builder.append(String.format("    this.%s = %s;\n", tan.name, tan.name));
            }
          });
      builder.append("  }\n");
      return new PojoConstructor(builder);
    }

    private static class TypeAndName {
      private final String type;
      private final String name;

      public TypeAndName(String type, String name) {
        this.type = type;
        this.name = name;
      }

      public boolean isOptionalClass() {
        return type.startsWith("Optional<") && type.endsWith(">");
      }

      public TypeAndName unwrapOptionalClass() {
        if (isOptionalClass()) {
          return new TypeAndName(type.substring("Optional<".length(), type.length() - 1), name);
        } else {
          return this;
        }
      }
    }
  }

  public static class PojoConstructor {
    private final StringBuilder builder;

    private PojoConstructor(StringBuilder builder) {
      this.builder = builder;
    }

    public String create() {
      builder.append("}\n");
      return builder.toString();
    }
  }
}
