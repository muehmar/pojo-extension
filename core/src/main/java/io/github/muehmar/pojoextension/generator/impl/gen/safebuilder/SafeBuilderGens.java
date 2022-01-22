package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.FINAL;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.Generators;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.BuilderField;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.FieldBuilderField;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.data.FullBuilderField;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/** Factory which creates the classes which forms the SafeBuilder. */
public class SafeBuilderGens {

  private static final String BUILDER_ASSIGNMENT = "this.builder = builder;";

  private SafeBuilderGens() {}

  public static Generator<FullBuilderField, PojoSettings> fieldBuilderClass() {
    return ClassGen.<FullBuilderField, PojoSettings>clazz()
        .nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(SafeBuilderGens::classDeclaration)
        .noSuperClassAndInterface()
        .content(builderClassContent())
        .append(RefsGen.genericRefs(), FullBuilderField::getPojo);
  }

  private static String rawClassName(BuilderField field) {
    final String prefix = field.getField().isRequired() ? "" : "Opt";
    return String.format("%sBuilder%d", prefix, field.getIndex());
  }

  private static String rawClassName(FullBuilderField field) {
    return rawClassName(field.getBuilderField());
  }

  private static String classDeclaration(BuilderField field) {
    return rawClassName(field) + field.getPojo().getGenericTypeDeclarationSection();
  }

  private static String classDeclaration(FullBuilderField field) {
    return classDeclaration(field.getBuilderField());
  }

  private static String nextClassTypeVariables(BuilderField field) {
    return nextRawClassName(field) + field.getPojo().getTypeVariablesSection();
  }

  private static String nextClassTypeVariables(FullBuilderField field) {
    return nextClassTypeVariables(field.getBuilderField());
  }

  private static String nextClassDiamond(BuilderField field) {
    return nextRawClassName(field) + field.getPojo().getDiamond();
  }

  private static String nextRawClassName(BuilderField field) {
    return rawClassName(field.withIndex(field.getIndex() + 1));
  }

  public static Generator<FullBuilderField, PojoSettings> builderClassContent() {
    return SafeBuilderGens.<PojoSettings>fieldDeclaration()
        .contraMap(FullBuilderField::getPojo)
        .append(newLine())
        .append(constructor())
        .append(newLine())
        .append(setMethod())
        .appendConditionally(
            (f, s) -> f.getField().isOptional(),
            Generators.<FullBuilderField, PojoSettings>newLine().append(setMethodOptional()));
  }

  public static <A> Generator<Pojo, A> fieldDeclaration() {
    return (p, s, w) -> w.println("private final Builder%s builder;", p.getTypeVariablesSection());
  }

  public static Generator<FullBuilderField, PojoSettings> constructor() {
    return ConstructorGen.<FullBuilderField, PojoSettings>modifiers(PRIVATE)
        .className(SafeBuilderGens::rawClassName)
        .singleArgument(
            f -> String.format("Builder%s builder", f.getPojo().getTypeVariablesSection()))
        .content(BUILDER_ASSIGNMENT);
  }

  public static <A> Generator<Pojo, A> andAllOptionalsMethod() {
    return MethodGen.<Pojo, A>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> "OptBuilder0" + p.getTypeVariablesSection())
        .methodName("andAllOptionals")
        .noArguments()
        .content(p -> String.format("return new OptBuilder0%s(builder);", p.getDiamond()));
  }

  public static <A> Generator<Pojo, A> andOptionalsMethod() {
    return MethodGen.<Pojo, A>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> "Builder" + p.getTypeVariablesSection())
        .methodName("andOptionals")
        .noArguments()
        .content("return builder;");
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> p.getName().asString() + p.getTypeVariablesSection())
        .methodName("build")
        .noArguments()
        .content("return builder.build();");
  }

  public static Generator<FullBuilderField, PojoSettings> setMethod() {
    final Generator<FullBuilderField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(builder.%s(%s));",
                nextClassDiamond(f.getBuilderField()),
                f.getField().builderSetMethodName(s),
                f.getField().getName());

    return MethodGen.<FullBuilderField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SafeBuilderGens::nextClassTypeVariables)
        .methodName((f, s) -> f.getField().builderSetMethodName(s).asString())
        .singleArgument(
            f ->
                String.format(
                    "%s %s", f.getField().getType().getTypeDeclaration(), f.getField().getName()))
        .content(content)
        .append(RefsGen.fieldRefs(), FullBuilderField::getField);
  }

  public static Generator<FullBuilderField, PojoSettings> setMethodOptional() {
    final Generator<FullBuilderField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(%s.map(builder::%s).orElse(builder));",
                nextClassDiamond(f.getBuilderField()),
                f.getField().getName(),
                f.getField().builderSetMethodName(s));

    return MethodGen.<FullBuilderField, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SafeBuilderGens::nextClassTypeVariables)
        .methodName((f, s) -> f.getField().builderSetMethodName(s).asString())
        .singleArgument(
            f ->
                String.format(
                    "Optional<%s> %s",
                    f.getField().getType().getTypeDeclaration(), f.getField().getName()))
        .content(content)
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL))
        .append(RefsGen.fieldRefs(), FullBuilderField::getField);
  }

  public static Generator<FullBuilderField, PojoSettings> fieldBuilderMethods() {
    final Generator<FieldBuilderField, PojoSettings> content =
        (f, s, w) ->
            w.println(
                "return new %s(builder.%s(%s.%s(%s)));",
                nextClassDiamond(f.getBuilderField()),
                f.getField().builderSetMethodName(s),
                f.getPojo().getName(),
                f.getFieldBuilderMethod().getMethodName(),
                f.getFieldBuilderMethod().getArgumentNames().mkString(", "));

    final Function<FieldBuilderField, String> nextClassTypeVariables =
        f -> SafeBuilderGens.nextClassTypeVariables(f.getBuilderField());

    final Generator<FieldBuilderField, PojoSettings> singleMethod =
        MethodGen.<FieldBuilderField, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(nextClassTypeVariables)
            .methodName((f, s) -> f.getFieldBuilderMethod().getMethodName().asString())
            .arguments(
                f ->
                    f.getFieldBuilderMethod()
                        .getArguments()
                        .map(
                            arg ->
                                String.format(
                                    "%s %s", arg.getType().getTypeDeclaration(), arg.getName())))
            .content(content)
            .append(RefsGen.fieldBuilderMethodRefs(), FieldBuilderField::getFieldBuilderMethod);

    return Generator.<FullBuilderField, PojoSettings>emptyGen()
        .appendList(singleMethod, FullBuilderField::getFieldBuilderFields);
  }

  public static Generator<Pojo, PojoSettings> finalRequiredBuilder() {
    final ToIntFunction<Pojo> builderNumber =
        pojo -> pojo.getFields().filter(PojoField::isRequired).size();

    final Generator<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className(p -> String.format("Builder%d", builderNumber.applyAsInt(p)))
            .singleArgument(p -> String.format("Builder%s builder", p.getTypeVariablesSection()))
            .content(BUILDER_ASSIGNMENT);

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

    return ClassGen.<Pojo, PojoSettings>clazz()
        .nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(
            (p, s) ->
                String.format(
                    "Builder%d%s",
                    builderNumber.applyAsInt(p), p.getGenericTypeDeclarationSection()))
        .noSuperClassAndInterface()
        .content(content)
        .append(RefsGen.genericRefs());
  }

  public static Generator<Pojo, PojoSettings> finalOptionalBuilder() {
    final ToIntFunction<Pojo> builderNumber =
        pojo -> pojo.getFields().filter(PojoField::isOptional).size();

    final Generator<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className(p -> String.format("OptBuilder%d", builderNumber.applyAsInt(p)))
            .singleArgument(p -> String.format("Builder%s builder", p.getTypeVariablesSection()))
            .content(BUILDER_ASSIGNMENT);

    final Generator<Pojo, PojoSettings> content =
        SafeBuilderGens.<PojoSettings>fieldDeclaration()
            .append(newLine())
            .append(constructor)
            .append(newLine())
            .append(buildMethod());

    return ClassGen.<Pojo, PojoSettings>clazz()
        .nested()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(
            (p, s) ->
                String.format(
                    "OptBuilder%d%s",
                    builderNumber.applyAsInt(p), p.getGenericTypeDeclarationSection()))
        .noSuperClassAndInterface()
        .content(content)
        .append(RefsGen.genericRefs());
  }
}
