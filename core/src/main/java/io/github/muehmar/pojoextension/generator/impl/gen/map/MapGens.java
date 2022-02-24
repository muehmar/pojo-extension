package io.github.muehmar.pojoextension.generator.impl.gen.map;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.DEFAULT;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_BIFUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_FUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_UNARYOPERATOR;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;
import io.github.muehmar.pojoextension.generator.impl.gen.instantiation.ConstructorCallGens;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;

public class MapGens {
  private MapGens() {}

  public static Generator<Pojo, PojoSettings> mapMethod() {
    final Name preferred = Name.fromString("T");
    return MethodGenBuilder.<Pojo, PojoSettings>create()
        .modifiers(DEFAULT)
        .singleGenericType(p -> p.findUnusedTypeVariableName(preferred))
        .returnType(p -> p.findUnusedTypeVariableName(preferred).asString())
        .methodName("map")
        .singleArgument(
            p ->
                String.format(
                    "Function<%s, %s> f",
                    p.getNameWithTypeVariables(), p.findUnusedTypeVariableName(preferred)))
        .content(mapMethodContent())
        .build()
        .append(w -> w.ref(JAVA_UTIL_FUNCTION))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> mapMethodContent() {
    return Generator.<Pojo, PojoSettings>of(
            (p, s, w) -> w.println("final %s self =", p.getNameWithTypeVariables()))
        .append(ConstructorCallGens.callWithNoFieldVariables("  "))
        .append(w -> w.println("return f.apply(self);"));
  }

  public static Generator<Pojo, PojoSettings> mapIfMethod() {
    return MethodGenBuilder.<Pojo, PojoSettings>create()
        .modifiers(DEFAULT)
        .noGenericTypes()
        .returnTypeName(Pojo::getNameWithTypeVariables)
        .methodName("mapIf")
        .arguments(
            p ->
                PList.of(
                    "boolean shouldMap",
                    String.format("UnaryOperator<%s> f", p.getNameWithTypeVariables())))
        .content(mapIfMethodContent())
        .build()
        .append(w -> w.ref(JAVA_UTIL_UNARYOPERATOR))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> mapIfMethodContent() {
    return Generator.<Pojo, PojoSettings>of(
            (p, s, w) -> w.println("final %s self =", p.getNameWithTypeVariables()))
        .append(ConstructorCallGens.callWithNoFieldVariables("  "))
        .append(w -> w.println("return shouldMap ? f.apply(self) : self;"));
  }

  public static Generator<Pojo, PojoSettings> mapIfPresentMethod() {
    final Name preferred = Name.fromString("T");
    return MethodGenBuilder.<Pojo, PojoSettings>create()
        .modifiers(DEFAULT)
        .singleGenericType(p -> p.findUnusedTypeVariableName(preferred))
        .returnTypeName(Pojo::getNameWithTypeVariables)
        .methodName("mapIfPresent")
        .arguments(
            p ->
                PList.of(
                    String.format("Optional<%s> value", p.findUnusedTypeVariableName(preferred)),
                    String.format(
                        "BiFunction<%s, %s, %s> f",
                        p.getNameWithTypeVariables(),
                        p.findUnusedTypeVariableName(preferred),
                        p.getNameWithTypeVariables())))
        .content(mapIfPresentMethodContent())
        .build()
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL).ref(JAVA_UTIL_BIFUNCTION))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> mapIfPresentMethodContent() {
    return Generator.<Pojo, PojoSettings>of(
            (p, s, w) -> w.println("final %s self =", p.getNameWithTypeVariables()))
        .append(ConstructorCallGens.callWithNoFieldVariables("  "))
        .append(w -> w.println("return value.map(v -> f.apply(self, v)).orElse(self);"));
  }
}
