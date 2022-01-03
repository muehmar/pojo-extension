package io.github.muehmar.pojoextension.generator.impl.gen.baseclass;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.ABSTRACT;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.Annotations;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;

public class BaseClassGens {
  private BaseClassGens() {}

  public static Generator<Pojo, PojoSettings> baseClass() {
    return ClassGen.<Pojo, PojoSettings>clazz()
        .topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC, ABSTRACT)
        .className((p, s) -> s.baseClassName(p).asString() + p.getGenericTypeDeclarationSection())
        .noSuperClass()
        .singleInterface((p, s) -> s.extensionName(p).asString() + p.getTypeVariablesSection())
        .content(baseClassContent())
        .append(RefsGen.genericRefs());
  }

  private static Generator<Pojo, PojoSettings> baseClassContent() {
    return Generator.<Pojo, PojoSettings>emptyGen()
        .appendConditionally(
            (p, s) -> s.getEqualsHashCodeAbility().isEnabled(), equalsMethod().appendNewLine())
        .appendConditionally(
            (p, s) -> s.getEqualsHashCodeAbility().isEnabled(), hashCodeMethod().appendNewLine())
        .appendConditionally((p, s) -> s.getToStringAbility().isEnabled(), toStringMethod());
  }

  private static Generator<Pojo, PojoSettings> equalsMethod() {
    final MethodGen<Pojo, PojoSettings> method =
        MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("equals")
            .singleArgument(ignore -> "Object o")
            .content("return genEquals(o);");
    return Annotations.<Pojo, PojoSettings>overrideAnnotation().append(method);
  }

  private static Generator<Pojo, PojoSettings> hashCodeMethod() {
    final MethodGen<Pojo, PojoSettings> method =
        MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("hashCode")
            .noArguments()
            .content("return genHashCode();");
    return Annotations.<Pojo, PojoSettings>overrideAnnotation().append(method);
  }

  private static Generator<Pojo, PojoSettings> toStringMethod() {
    final MethodGen<Pojo, PojoSettings> method =
        MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("toString")
            .noArguments()
            .content("return genToString();");
    return Annotations.<Pojo, PojoSettings>overrideAnnotation().append(method);
  }
}
