package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;

public class BuildMethodGen extends MethodGen<Pojo, PojoSettings> {
  public BuildMethodGen() {
    super(
        (pojo, settings) -> JavaModifiers.of(PUBLIC),
        (pojo, settings) -> pojo.getPojoName().asString(),
        (pojo, settings) -> "build",
        (pojo, settings) -> PList.empty(),
        (pojo, settings, writer) ->
            writer.println(
                "return new %s(%s);",
                pojo.getPojoName().asString(),
                pojo.getFields().map(f -> f.getName().asString()).mkString(", ")));
  }
}
