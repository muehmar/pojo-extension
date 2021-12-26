package io.github.muehmar.pojoextension.generator.impl.gen.map;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_BIFUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_FUNCTION;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_UNARYOPERATOR;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;

public class MapGens {
  private MapGens() {}

  public static Generator<Pojo, PojoSettings> mapMethod() {
    final Name preferred = Name.fromString("T");
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .singleGenericType(p -> p.findUnusedTypeVariableName(preferred))
        .returnType(p -> p.findUnusedTypeVariableName(preferred).asString())
        .methodName("map")
        .singleArgument(
            p ->
                String.format(
                    "Function<%s%s, %s> f",
                    p.getName(),
                    p.getTypeVariablesSection(),
                    p.findUnusedTypeVariableName(preferred)))
        .content("return f.apply(self());")
        .append(w -> w.ref(JAVA_UTIL_FUNCTION))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> mapIfMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> p.getName().asString() + p.getTypeVariablesSection())
        .methodName("mapIf")
        .arguments(
            p ->
                PList.of(
                    "boolean shouldMap",
                    String.format(
                        "UnaryOperator<%s%s> f", p.getName(), p.getTypeVariablesSection())))
        .content("return shouldMap ? f.apply(self()) : self();")
        .append(w -> w.ref(JAVA_UTIL_UNARYOPERATOR))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }

  public static Generator<Pojo, PojoSettings> mapIfPresentMethod() {
    final Name preferred = Name.fromString("T");
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .singleGenericType(p -> p.findUnusedTypeVariableName(preferred))
        .returnType(p -> p.getName().asString() + p.getTypeVariablesSection())
        .methodName("mapIfPresent")
        .arguments(
            p ->
                PList.of(
                    String.format("Optional<%s> value", p.findUnusedTypeVariableName(preferred)),
                    String.format(
                        "BiFunction<%s%s, %s, %s%s> f",
                        p.getName(),
                        p.getTypeVariablesSection(),
                        p.findUnusedTypeVariableName(preferred),
                        p.getName(),
                        p.getTypeVariablesSection())))
        .content("return value.map(v -> f.apply(self(), v)).orElseGet(this::self);")
        .append(w -> w.ref(JAVA_UTIL_OPTIONAL).ref(JAVA_UTIL_BIFUNCTION))
        .filter((p, s) -> s.getMappersAbility().isEnabled());
  }
}
