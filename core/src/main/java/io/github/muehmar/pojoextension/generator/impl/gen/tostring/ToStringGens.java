package io.github.muehmar.pojoextension.generator.impl.gen.tostring;

import static io.github.muehmar.pojoextension.generator.data.OptionalFieldRelation.SAME_TYPE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;

import ch.bluecare.commons.data.Pair;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.impl.gen.Annotations;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import java.util.function.Function;

public class ToStringGens {
  private ToStringGens() {}

  public static Generator<Pojo, Void> toStringMethod() {
    final Generator<Pojo, Void> method =
        MethodGen.<Pojo, Void>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("toString")
            .noArguments()
            .content("return toString(self());");
    return Annotations.<Pojo, Void>overrideAnnotation().append(method);
  }

  public static Generator<Pojo, Void> staticToStringMethod() {
    final Function<Pojo, String> argument = p -> String.format("%s self", p.getName());
    return MethodGen.<Pojo, Void>modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType("String")
        .methodName("toString")
        .singleArgument(argument)
        .content(staticToStringContent());
  }

  private static Generator<Pojo, Void> staticToStringContent() {
    return Generator.<Pojo, Void>of((p, s, w) -> w.println("return \"%s{\"", p.getName()))
        .appendList(toStringFieldLine(), Pojo::getAllGettersOrThrow)
        .append(w -> w.tab(2).println("+ '}';"));
  }

  private static Generator<FieldGetter, Void> toStringFieldLine() {
    return (fg, s, w) -> {
      final Pair<String, String> wrapper = getWrapper(fg);
      return w.tab(2)
          .println(
              "+ \"%s=%s\" + self.%s()%s",
              fg.getField().getName(), wrapper.first(), fg.getGetter().getName(), wrapper.second());
    };
  }

  private static Pair<String, String> getWrapper(FieldGetter fg) {
    return fg.getField().getType().equals(Type.string()) && fg.getRelation().equals(SAME_TYPE)
        ? Pair.of("'", " + '\\''")
        : Pair.of("", "");
  }
}
