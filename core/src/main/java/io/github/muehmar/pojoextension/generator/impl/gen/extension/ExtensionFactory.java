package io.github.muehmar.pojoextension.generator.impl.gen.extension;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.ConstructorGen;
import io.github.muehmar.pojoextension.generator.impl.gen.PackageGen;
import io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.CompleteSafeBuilderFactory;

public class ExtensionFactory {
  private ExtensionFactory() {}

  public static Generator<Pojo, PojoSettings> extensionClass() {
    final ConstructorGen<Pojo, PojoSettings> constructor =
        ConstructorGen.<Pojo, PojoSettings>modifiers(PRIVATE)
            .className(p -> p.getExtensionName().asString())
            .noArguments()
            .content("");

    final Generator<Pojo, PojoSettings> content =
        constructor.append(CompleteSafeBuilderFactory.completeSafeBuilder());

    return ClassGen.<Pojo, PojoSettings>topLevel()
        .packageGen(new PackageGen())
        .modifiers(PUBLIC)
        .className(p -> p.getExtensionName().asString())
        .content(content);
  }
}
