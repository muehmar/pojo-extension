package io.github.muehmar.pojoextension.generator.impl.gen.map;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_BIFUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_FUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_UNARYOPERATOR;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;

public class MapGens {
  private MapGens() {}

  public static Generator<Pojo, PojoSettings> mapMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName("map")
        .singleArgument(p -> String.format("Function<%s, T> f", p.getName()))
        .content("return f.apply(self());")
        .append(w -> w.ref(JAVA_UTIL_FUNCTION))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> mapIfMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> p.getName().asString())
        .methodName("mapIf")
        .arguments(
            p -> PList.of("boolean shouldMap", String.format("UnaryOperator<%s> f", p.getName())))
        .content("return shouldMap ? f.apply(self()) : self();")
        .append(w -> w.ref(JAVA_UTIL_UNARYOPERATOR))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> mapIfPresentMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .genericTypes("T")
        .returnType(p -> p.getName().asString())
        .methodName("mapIfPresent")
        .arguments(
            p ->
                PList.of(
                    "Optional<T> value",
                    String.format("BiFunction<%s, T, %s> f", p.getName(), p.getName())))
        .content("return value.map(v -> f.apply(self(), v)).orElseGet(this::self);")
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL).ref(JAVA_UTIL_BIFUNCTION))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }
}
