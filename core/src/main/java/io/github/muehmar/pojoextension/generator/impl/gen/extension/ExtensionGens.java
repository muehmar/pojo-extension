package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.ABSTRACT;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PROTECTED;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode.EqualsGens;
import io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode.HashCodeGens;
import io.github.muehmar.pojoextension.generator.impl.gen.map.MapGens;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.CompleteSafeBuilderGens;
import io.github.muehmar.pojoextension.generator.impl.gen.tostring.ToStringGens;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.WithGens;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.data.WithField;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ExtensionGens {
  private ExtensionGens() {}

  public static Generator<Pojo, PojoSettings> extensionClass() {
    return ClassGen.<Pojo, PojoSettings>topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC, ABSTRACT)
        .className((p, s) -> s.extensionName(p).asString() + p.getGenericTypeDeclarationSection())
        .content(content());
  }

  private static Generator<Pojo, PojoSettings> content() {
    final Function<Pojo, PList<WithField>> toWithFields =
        pojo -> pojo.getFields().map(field -> WithField.of(pojo, field));

    final Generator<WithField, PojoSettings> optionalNewLine =
        Generator.<WithField, PojoSettings>emptyGen()
            .appendConditionally(
                wf -> wf.getField().isOptional(), ((data, settings, writer) -> writer.println()));

    final BiPredicate<Pojo, PojoSettings> nonDiscreteBuilder =
        (p, s) -> s.getDiscreteBuilder().isDisabled();

    return constructor()
        .appendNewLine()
        .append(selfMethod())
        .appendNewLine()
        .appendList(WithGens.withMethod().appendNewLine(), toWithFields)
        .appendList(WithGens.staticWithMethod().appendNewLine(), toWithFields)
        .appendList(WithGens.optionalWithMethod().append(optionalNewLine), toWithFields)
        .appendList(WithGens.staticOptionalWithMethod().append(optionalNewLine), toWithFields)
        .append(MapGens.mapMethod())
        .appendNewLine()
        .append(MapGens.mapIfMethod())
        .appendNewLine()
        .append(MapGens.mapIfPresentMethod())
        .appendNewLine()
        .appendConditionally(nonDiscreteBuilder, CompleteSafeBuilderGens.newBuilderMethod())
        .appendNewLine()
        .appendConditionally(nonDiscreteBuilder, CompleteSafeBuilderGens.completeSafeBuilder())
        .appendNewLine()
        .append(EqualsGens.equalsMethod())
        .appendNewLine()
        .append(EqualsGens.staticEqualsMethod())
        .appendNewLine()
        .append(HashCodeGens.hashCodeMethod())
        .appendNewLine()
        .append(HashCodeGens.staticHashCodeMethod())
        .appendNewLine()
        .append(ToStringGens.toStringMethod())
        .appendNewLine()
        .append(ToStringGens.staticToStringMethod());
  }

  public static Generator<Pojo, PojoSettings> constructor() {
    return ConstructorGen.<Pojo, PojoSettings>modifiers(PROTECTED)
        .className((p, s) -> s.extensionName(p).asString())
        .noArguments()
        .content(
            (p, s, w) ->
                w.println("final Object o = this;")
                    .println(
                        "if(!(o instanceof %s%s))",
                        p.getName(), p.getTypeVariablesWildcardSection())
                    .tab(1)
                    .println(
                        "throw new IllegalArgumentException(\"Only class %s can extend %s.\");",
                        p.getName(), s.extensionName(p)));
  }

  public static Generator<Pojo, PojoSettings> selfMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(pojo -> pojo.getName().asString())
        .methodName("self")
        .noArguments()
        .content(
            (p, s, w) ->
                w.println("final Object self = this;")
                    .println("return (%s%s)self;", p.getName(), p.getTypeVariablesSection()));
  }
}
