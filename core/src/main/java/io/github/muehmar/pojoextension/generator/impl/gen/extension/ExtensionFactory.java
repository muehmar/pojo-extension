package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.ABSTRACT;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PROTECTED;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode.Equals;
import io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode.HashCode;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.CompleteSafeBuilderFactory;
import io.github.muehmar.pojoextension.generator.impl.gen.tostring.ToString;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.With;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.data.WithField;
import java.util.function.Function;

public class ExtensionFactory {
  private ExtensionFactory() {}

  public static Generator<Pojo, PojoSettings> extensionClass() {

    final Function<Pojo, PList<WithField>> toWithFields =
        pojo -> pojo.getFields().map(field -> WithField.of(pojo, field));

    final Generator<WithField, PojoSettings> optionalNewLine =
        Generator.<WithField, PojoSettings>emptyGen()
            .appendConditionally(
                wf -> wf.getField().isOptional(), ((data, settings, writer) -> writer.println()));

    final Generator<Pojo, PojoSettings> content =
        constructor()
            .appendNewLine()
            .append(selfMethod())
            .appendNewLine()
            .appendList(With.withMethod().appendNewLine(), toWithFields)
            .appendList(With.staticWithMethod().appendNewLine(), toWithFields)
            .appendList(With.optionalWithMethod().append(optionalNewLine), toWithFields)
            .appendList(With.staticOptionalWithMethod().append(optionalNewLine), toWithFields)
            .append(CompleteSafeBuilderFactory.completeSafeBuilder())
            .appendNewLine()
            .append(Equals.equalsMethod())
            .appendNewLine()
            .append(Equals.staticEqualsMethod())
            .appendNewLine()
            .append(HashCode.hashCodeMethod())
            .appendNewLine()
            .append(HashCode.staticHashCodeMethod())
            .appendNewLine()
            .appendNoSettings(ToString.toStringMethod())
            .appendNewLine()
            .appendNoSettings(ToString.staticToStringMethod());

    return ClassGen.<Pojo, PojoSettings>topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC, ABSTRACT)
        .className((p, s) -> s.extensionName(p).asString())
        .content(content);
  }

  private static Generator<Pojo, PojoSettings> constructor() {
    return ConstructorGen.<Pojo, PojoSettings>modifiers(PROTECTED)
        .className((p, s) -> s.extensionName(p).asString())
        .noArguments()
        .content(
            (p, s, w) ->
                w.println("final Object o = this;")
                    .println("if(!(o instanceof %s))", p.getName())
                    .tab(1)
                    .println(
                        "throw new IllegalArgumentException(\"Only class %s can extend %s.\");",
                        p.getName(), s.extensionName(p)));
  }

  private static Generator<Pojo, PojoSettings> selfMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PRIVATE)
        .returnType(pojo -> pojo.getName().asString())
        .methodName("self")
        .noArguments()
        .content(
            (p, s, w) ->
                w.println("final Object self = this;").println("return (%s)self;", p.getName()));
  }
}
