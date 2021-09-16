package io.github.muehmar.pojoextension.generator;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.data.PackageName;
import io.github.muehmar.pojoextension.data.Pojo;
import io.github.muehmar.pojoextension.data.PojoMember;
import io.github.muehmar.pojoextension.data.Type;
import java.util.function.Function;
import java.util.stream.IntStream;

public class JavaGenerator {
  private final Resolver resolver;

  public JavaGenerator(Resolver resolver) {
    this.resolver = resolver;
  }

  public void generate(Writer writer, Pojo pojo, PojoSettings settings) {
    printPackage(writer, pojo.getPackage());
    printImports(writer, pojo);
    printClassStart(writer, pojo);
    printConstructor(writer, pojo);

    printBuilder(writer, pojo, settings);
    printSafeBuilder(writer, pojo);

    printClassEnd(writer);
  }

  private void printPackage(Writer writer, PackageName packageName) {
    writer.println("package %s;", packageName.asString());
  }

  private void printImports(Writer writer, Pojo pojo) {
    printJavaUtilImports(writer);

    pojo.getMembers()
        .map(PojoMember::getType)
        .map(Type::getQualifiedName)
        .distinct(Function.identity())
        .forEach(classImport -> writer.println("import %s;", classImport.asString()));

    writer.println();
  }

  private void printJavaUtilImports(Writer writer) {
    writer.println();
    writer.println("import java.util.Objects;");
    writer.println("import java.util.Optional;");
  }

  private void printClassStart(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(0).println("public class %s {", resolver.className(pojo.getName()).asString());
  }

  private void printConstructor(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(1).println("private %s() {}", resolver.className(pojo.getName()).asString());
  }

  protected void printBuilder(Writer writer, Pojo pojo, PojoSettings settings) {
    if (settings.isDisableSafeBuilder()) {
      writer.println();
      writer.tab(1).println("public static Builder newBuilder() {");
      writer.tab(2).println("return new Builder();");
      writer.tab(1).println("}");
    }

    writer.println();
    writer.tab(1).println("public static final class Builder {");

    if (settings.isEnableSafeBuilder()) {
      writer.println();
      writer.tab(2).println("private Builder() {");
      writer.tab(2).println("}");
    }

    writer.println();
    pojo.getMembers()
        .forEach(
            member -> {
              final String type = resolver.className(member.getType().getClassName()).asString();
              final String fieldName = resolver.memberName(member.getName()).asString();
              writer.tab(2).println("private %s %s;", type, fieldName);
            });

    pojo.getMembers()
        .forEach(
            member -> {
              final String type = resolver.className(member.getType().getClassName()).asString();
              final String fieldName = resolver.memberName(member.getName()).asString();
              final String setterModifier =
                  settings.isEnableSafeBuilder() && member.isRequired() ? "private" : "public";

              // Normal setter
              writer.println();
              writer
                  .tab(2)
                  .println(
                      "%s Builder %s(%s %s) {",
                      setterModifier,
                      resolver.setterName(member.getName()).asString(),
                      type,
                      fieldName);
              writer.tab(3).println("this.%s = %s;", fieldName, fieldName);
              writer.tab(3).println("return this;");
              writer.tab(2).println("}");

              // Optional setter
              if (member.isOptional()) {
                writer.println();
                writer
                    .tab(2)
                    .println(
                        "%s Builder %s(Optional<%s> %s) {",
                        setterModifier,
                        resolver.setterName(member.getName()).asString(),
                        type,
                        fieldName);
                writer.tab(3).println("this.%s = %s.orElse(null);", fieldName, fieldName);
                writer.tab(3).println("return this;");
                writer.tab(2).println("}");
              }
            });

    writer.println();
    writer.tab(2).println("@SuppressWarnings(\"deprecation\")");
    writer.tab(2).println("public %s build() {", resolver.className(pojo.getPojoName()).asString());
    writer
        .tab(3)
        .println(
            "return new %s(%s);",
            resolver.className(pojo.getPojoName()).asString(), createNamesCommaSeparated(pojo));
    writer.tab(2).println("}");

    writer.tab(1).println("}");
  }

  protected void printSafeBuilder(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(1).println("public static Builder0 newBuilder() {");
    writer.tab(2).println("return new Builder0(new Builder());");
    writer.tab(1).println("}");

    final PList<PojoMember> optionalMembers = pojo.getMembers().filter(PojoMember::isOptional);
    final PList<PojoMember> requiredMembers = pojo.getMembers().filter(PojoMember::isRequired);

    IntStream.range(0, requiredMembers.size())
        .forEach(
            idx -> {
              final PojoMember member = requiredMembers.apply(idx);
              final String memberName = resolver.memberName(member.getName()).asString();
              final String memberType =
                  resolver.className(member.getType().getClassName()).asString();
              writer.println();
              writer.tab(1).println("public static final class Builder%d {", idx);

              writer.tab(2).println("private final Builder builder;");
              writer.tab(2).println("private Builder%d(Builder builder) {", idx);
              writer.tab(3).println("this.builder = builder;");
              writer.tab(2).println("}");

              writer.println();
              writer
                  .tab(2)
                  .println(
                      "public Builder%d %s(%s %s){",
                      idx + 1,
                      resolver.setterName(member.getName()).asString(),
                      memberType,
                      memberName);
              writer
                  .tab(3)
                  .println(
                      "return new Builder%d(builder.%s(%s));",
                      idx + 1, resolver.setterName(member.getName()).asString(), memberName);
              writer.tab(2).println("}");

              writer.tab(1).println("}");
            });

    // Builder after all required members have been set
    writer.println();
    writer.tab(1).println("public static final class Builder%d {", requiredMembers.size());
    writer.tab(2).println("private final Builder builder;");
    writer.tab(2).println("private Builder%d(Builder builder) {", requiredMembers.size());
    writer.tab(3).println("this.builder = builder;");
    writer.tab(2).println("}");
    writer.tab(2).println("public OptBuilder0 andAllOptionals(){");
    writer.tab(3).println("return new OptBuilder0(builder);");
    writer.tab(2).println("}");
    writer.tab(2).println("public Builder andOptionals(){");
    writer.tab(3).println("return builder;");
    writer.tab(2).println("}");
    writer.tab(2).println("public %s build(){", resolver.className(pojo.getPojoName()).asString());
    writer.tab(3).println("return builder.build();");
    writer.tab(2).println("}");
    writer.tab(1).println("}");

    IntStream.range(0, optionalMembers.size())
        .forEach(
            idx -> {
              final PojoMember member = optionalMembers.apply(idx);
              final String memberName = resolver.memberName(member.getName()).asString();
              final String memberType =
                  resolver.className(member.getType().getClassName()).asString();
              writer.println();
              writer.tab(1).println("public static final class OptBuilder%d {", idx);

              writer.tab(2).println("private final Builder builder;");
              writer.tab(2).println("private OptBuilder%d(Builder builder) {", idx);
              writer.tab(3).println("this.builder = builder;");
              writer.tab(2).println("}");

              writer.println();
              writer
                  .tab(2)
                  .println(
                      "public OptBuilder%d %s(%s %s){",
                      idx + 1,
                      resolver.setterName(member.getName()).asString(),
                      memberType,
                      memberName);
              writer
                  .tab(3)
                  .println(
                      "return new OptBuilder%d(builder.%s(%s));",
                      idx + 1, resolver.setterName(member.getName()).asString(), memberName);
              writer.tab(2).println("}");

              writer.println();
              writer
                  .tab(2)
                  .println(
                      "public OptBuilder%d %s(Optional<%s> %s){",
                      idx + 1,
                      resolver.setterName(member.getName()).asString(),
                      memberType,
                      memberName);
              writer
                  .tab(3)
                  .println(
                      "return new OptBuilder%d(%s.map(builder::%s).orElse(builder));",
                      idx + 1, memberName, resolver.setterName(member.getName()).asString());
              writer.tab(2).println("}");

              writer.tab(1).println("}");
            });

    // Final Builder
    writer.println();
    writer.tab(1).println("public static final class OptBuilder%d {", optionalMembers.size());
    writer.tab(2).println("private final Builder builder;");
    writer.tab(2).println("private OptBuilder%d(Builder builder) {", optionalMembers.size());
    writer.tab(3).println("this.builder = builder;");
    writer.tab(2).println("}");
    writer.tab(2).println("public %s build(){", resolver.className(pojo.getPojoName()).asString());
    writer.tab(3).println("return builder.build();");
    writer.tab(2).println("}");
    writer.tab(1).println("}");
  }

  private String createNamesCommaSeparated(Pojo pojo) {
    final PList<String> formattedPairs =
        pojo.getMembers()
            .map(member -> String.format("%s", resolver.memberName(member.getName()).asString()));

    return String.join(", ", formattedPairs);
  }

  private void printClassEnd(Writer writer) {
    writer.println("}");
  }
}
