package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.RefsGen;
import io.github.muehmar.pojoextension.generator.impl.gen.getter.GetterGens;
import io.github.muehmar.pojoextension.generator.impl.gen.map.MapGens;
import io.github.muehmar.pojoextension.generator.impl.gen.withers.WithGens;
import io.github.muehmar.pojoextension.generator.model.FieldGetter;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoAndField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import java.util.function.Function;

public class ExtensionGens {
  private ExtensionGens() {}

  public static Generator<Pojo, PojoSettings> extensionInterface() {
    return JavaGenerators.<Pojo, PojoSettings>classGen()
        .ifc()
        .topLevel()
        .packageGen(new PackageGen())
        .noModifiers()
        .className((p, s) -> s.extensionName(p).asString() + p.getGenericTypeDeclarationSection())
        .noSuperClass()
        .noInterfaces()
        .content(content())
        .build()
        .append(RefsGen.genericRefs());
  }

  private static Generator<Pojo, PojoSettings> content() {
    final Function<Pojo, Iterable<FieldGetter>> toFieldGetter =
        pojo -> pojo.getFields().map(pojo::getMatchingGetterOrThrow);

    final Generator<PojoAndField, PojoSettings> optionalNewLine =
        Generator.<PojoAndField, PojoSettings>emptyGen()
            .appendConditionally(
                wf -> wf.getField().isOptional(), ((data, settings, writer) -> writer.println()));

    return Generator.<Pojo, PojoSettings>emptyGen()
        .appendNewLine()
        .appendList(getterMethod().appendNewLine(), toFieldGetter)
        .appendList(WithGens.withMethod().appendNewLine(), Pojo::getPojoAndFields)
        .appendList(WithGens.optionalWithMethod().append(optionalNewLine), Pojo::getPojoAndFields)
        .appendList(
            GetterGens.optionalGetterMethod().append(optionalNewLine), Pojo::getPojoAndFields)
        .append(MapGens.mapMethod())
        .appendNewLine()
        .append(MapGens.mapIfMethod())
        .appendNewLine()
        .append(MapGens.mapIfPresentMethod());
  }

  public static Generator<FieldGetter, PojoSettings> getterMethod() {
    return JavaGenerators.<FieldGetter, PojoSettings>methodGen()
        .modifiers()
        .noGenericTypes()
        .returnType(fg -> fg.getGetter().getReturnType().getTypeDeclaration().asString())
        .methodName(fg -> fg.getGetter().getName().asString())
        .noArguments()
        .noBody()
        .build();
  }
}
