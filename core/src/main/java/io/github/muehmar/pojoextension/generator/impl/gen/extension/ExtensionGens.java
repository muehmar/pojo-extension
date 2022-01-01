package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
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

  public static Generator<Pojo, PojoSettings> extensionInterface() {
    return ClassGen.<Pojo, PojoSettings>ifc()
        .topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC)
        .className((p, s) -> s.extensionName(p).asString() + p.getGenericTypeDeclarationSection())
        .content(content());
  }

  private static Generator<Pojo, PojoSettings> content() {
    final Function<Pojo, PList<WithField>> toWithFields =
        pojo -> pojo.getFields().map(field -> WithField.of(pojo, field));

    final Function<Pojo, PList<FieldGetter>> toFieldGetter =
        pojo -> pojo.getFields().map(pojo::getMatchingGetterOrThrow);

    final Generator<WithField, PojoSettings> optionalNewLine =
        Generator.<WithField, PojoSettings>emptyGen()
            .appendConditionally(
                wf -> wf.getField().isOptional(), ((data, settings, writer) -> writer.println()));

    final BiPredicate<Pojo, PojoSettings> nonDiscreteBuilder =
        (p, s) -> s.getDiscreteBuilder().isDisabled();

    return Generator.<Pojo, PojoSettings>emptyGen()
        .appendNewLine()
        .appendList(getterMethod().appendNewLine(), toFieldGetter)
        .appendList(WithGens.withMethod().appendNewLine(), toWithFields)
        .appendList(WithGens.optionalWithMethod().append(optionalNewLine), toWithFields)
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
        .append(EqualsGens.genEqualsMethod())
        .appendNewLine()
        .append(HashCodeGens.genHashCodeMethod())
        .appendNewLine()
        .append(ToStringGens.toStringMethod())
        .appendNewLine()
        .append(ToStringGens.staticToStringMethod());
  }

  public static Generator<FieldGetter, PojoSettings> getterMethod() {
    return MethodGen.<FieldGetter, PojoSettings>modifiers()
        .noGenericTypes()
        .returnType(fg -> fg.getGetter().getReturnType().getTypeDeclaration().asString())
        .methodName(fg -> fg.getGetter().getName().asString())
        .noArguments()
        .noBody();
  }
}
