package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.BuilderField;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.BuilderFieldWithMethod;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.model.IndexedField;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.BuildMethod;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/** Factory which creates the classes which forms the SafeBuilder. */
public class SafeBuilderGens {

  private static final String BUILDER_ASSIGNMENT = "this.builder = builder;";

  private SafeBuilderGens() {}

  public static Generator<BuilderField, PojoSettings> fieldBuilderClass() {
    return JavaGenerators.<BuilderField, PojoSettings>classGen()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(f -> classDeclaration(f.getIndexedField()))
        .noSuperClass()
        .noInterfaces()
        .content(builderClassContent())
        .build()
        .append(RefsGen.genericRefs(), BuilderField::getPojo);
  }

  private static String rawClassName(IndexedField field) {
    final String prefix = field.getField().isRequired() ? "" : "Opt";
    return String.format("%sBuilder%d", prefix, field.getIndex());
  }

  private static String classDeclaration(IndexedField field) {
    return rawClassName(field) + field.getPojo().getGenericTypeDeclarationSection();
  }

  private static String nextClassTypeVariables(IndexedField field) {
    return nextRawClassName(field) + field.getPojo().getTypeVariablesSection();
  }

  private static String nextClassDiamond(IndexedField field) {
    return nextRawClassName(field) + field.getPojo().getDiamond();
  }

  private static String nextRawClassName(IndexedField field) {
    return rawClassName(field.withIndex(field.getIndex() + 1));
  }

  public static Generator<BuilderField, PojoSettings> builderClassContent() {
    return SafeBuilderGens.<PojoSettings>fieldDeclaration()
        .contraMap(BuilderField::getPojo)
        .append(newLine())
        .append(constructor())
        .append(newLine())
        .append(setMethod())
        .appendConditionally(BuilderField::hasFieldBuilder, fieldBuilderMethods().prependNewLine())
        .appendConditionally(BuilderField::isFieldOptional, setMethodOptional().prependNewLine());
  }

  public static <A> Generator<Pojo, A> fieldDeclaration() {
    return (p, s, w) -> w.println("private final Builder%s builder;", p.getTypeVariablesSection());
  }

  public static Generator<BuilderField, PojoSettings> constructor() {
    return JavaGenerators.<BuilderField, PojoSettings>constructorGen()
        .modifiers(PRIVATE)
        .className(f -> rawClassName(f.getIndexedField()))
        .singleArgument(
            f -> String.format("Builder%s builder", f.getPojo().getTypeVariablesSection()))
        .content(BUILDER_ASSIGNMENT)
        .build();
  }

  public static <A> Generator<Pojo, A> andAllOptionalsMethod() {
    return JavaGenerators.<Pojo, A>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> "OptBuilder0" + p.getTypeVariablesSection())
        .methodName("andAllOptionals")
        .noArguments()
        .content(p -> String.format("return new OptBuilder0%s(builder);", p.getDiamond()))
        .build();
  }

  public static <A> Generator<Pojo, A> andOptionalsMethod() {
    return JavaGenerators.<Pojo, A>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> "Builder" + p.getTypeVariablesSection())
        .methodName("andOptionals")
        .noArguments()
        .content("return builder;")
        .build();
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    final Function<Pojo, String> createReturnType =
        p ->
            p.getBuildMethod()
                .map(BuildMethod::getReturnType)
                .map(Type::getTypeDeclaration)
                .orElseGet(p::getNameWithTypeVariables)
                .asString();
    return JavaGenerators.<Pojo, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(createReturnType)
        .methodName("build")
        .noArguments()
        .content("return builder.build();")
        .build();
  }

  public static Generator<BuilderField, PojoSettings> setMethod() {
    final Generator<BuilderField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(builder.%s(%s));",
                nextClassDiamond(f.getIndexedField()),
                f.getField().builderSetMethodName(s),
                f.getField().getName());

    return JavaGenerators.<BuilderField, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> nextClassTypeVariables(f.getIndexedField()))
        .methodName((f, s) -> f.getField().builderSetMethodName(s).asString())
        .singleArgument(
            f ->
                String.format(
                    "%s %s", f.getField().getType().getTypeDeclaration(), f.getField().getName()))
        .content(content)
        .build()
        .append(RefsGen.fieldRefs(), BuilderField::getField)
        .filter(BuilderField::isEnableDefaultMethods);
  }

  public static Generator<BuilderField, PojoSettings> setMethodOptional() {
    final Generator<BuilderField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(builder.%s(%s));",
                nextClassDiamond(f.getIndexedField()),
                f.getField().builderSetMethodName(s),
                f.getField().getName());

    return JavaGenerators.<BuilderField, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> nextClassTypeVariables(f.getIndexedField()))
        .methodName((f, s) -> f.getField().builderSetMethodName(s).asString())
        .singleArgument(
            f ->
                String.format(
                    "Optional<%s> %s",
                    f.getField().getType().getTypeDeclaration(), f.getField().getName()))
        .content(content)
        .build()
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
        .append(RefsGen.fieldRefs(), BuilderField::getField)
        .filter(BuilderField::isEnableDefaultMethods);
  }

  public static Generator<BuilderField, PojoSettings> fieldBuilderMethods() {
    final Generator<BuilderFieldWithMethod, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(builder.%s(%s%s.%s(%s)));",
                nextClassDiamond(f.getIndexedField()),
                f.getField().builderSetMethodName(s),
                f.getPojo().getName(),
                f.getFieldBuilderMethod()
                    .getInnerClassName()
                    .map(name -> name.prefix(".").asString())
                    .orElse(""),
                f.getFieldBuilderMethod().getMethodName(),
                f.getFieldBuilderMethod().getArgumentNames().mkString(", "));

    final Function<BuilderFieldWithMethod, String> nextClassTypeVariables =
        f -> SafeBuilderGens.nextClassTypeVariables(f.getIndexedField());

    final Generator<BuilderFieldWithMethod, PojoSettings> singleMethod =
        JavaGenerators.<BuilderFieldWithMethod, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(nextClassTypeVariables)
            .methodName(f -> f.getFieldBuilderMethod().getMethodName().asString())
            .arguments(f -> f.getFieldBuilderMethod().getArguments().map(Argument::formatted))
            .content(content)
            .build()
            .append(
                RefsGen.fieldBuilderMethodRefs(), BuilderFieldWithMethod::getFieldBuilderMethod);

    return Generator.<BuilderField, PojoSettings>emptyGen()
        .appendList(
            singleMethod,
            BuilderField::getBuilderFieldsWithMethod,
            Generator.ofWriterFunction(Writer::println));
  }

  public static Generator<Pojo, PojoSettings> finalRequiredBuilder() {
    final ToIntFunction<Pojo> builderNumber =
        pojo -> pojo.getFields().filter(PojoField::isRequired).size();

    final Generator<Pojo, PojoSettings> constructor =
        JavaGenerators.<Pojo, PojoSettings>constructorGen()
            .modifiers(PRIVATE)
            .className(p -> String.format("Builder%d", builderNumber.applyAsInt(p)))
            .singleArgument(p -> String.format("Builder%s builder", p.getTypeVariablesSection()))
            .content(BUILDER_ASSIGNMENT)
            .build();

    final Generator<Pojo, PojoSettings> content =
        SafeBuilderGens.<PojoSettings>fieldDeclaration()
            .append(newLine())
            .append(constructor)
            .append(newLine())
            .append(andAllOptionalsMethod())
            .append(newLine())
            .append(andOptionalsMethod())
            .append(newLine())
            .append(buildMethod());

    return JavaGenerators.<Pojo, PojoSettings>classGen()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(
            (p, s) ->
                String.format(
                    "Builder%d%s",
                    builderNumber.applyAsInt(p), p.getGenericTypeDeclarationSection()))
        .noSuperClass()
        .noInterfaces()
        .content(content)
        .build()
        .append(RefsGen.genericRefs());
  }

  public static Generator<Pojo, PojoSettings> finalOptionalBuilder() {
    final ToIntFunction<Pojo> builderNumber =
        pojo -> pojo.getFields().filter(PojoField::isOptional).size();

    final Generator<Pojo, PojoSettings> constructor =
        JavaGenerators.<Pojo, PojoSettings>constructorGen()
            .modifiers(PRIVATE)
            .className(p -> String.format("OptBuilder%d", builderNumber.applyAsInt(p)))
            .singleArgument(p -> String.format("Builder%s builder", p.getTypeVariablesSection()))
            .content(BUILDER_ASSIGNMENT)
            .build();

    final Generator<Pojo, PojoSettings> content =
        SafeBuilderGens.<PojoSettings>fieldDeclaration()
            .append(newLine())
            .append(constructor)
            .append(newLine())
            .append(buildMethod());

    return JavaGenerators.<Pojo, PojoSettings>classGen()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(
            (p, s) ->
                String.format(
                    "OptBuilder%d%s",
                    builderNumber.applyAsInt(p), p.getGenericTypeDeclarationSection()))
        .noSuperClass()
        .noInterfaces()
        .content(content)
        .build()
        .append(RefsGen.genericRefs());
  }
}
