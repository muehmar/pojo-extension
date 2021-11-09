package io.github.muehmar.pojoextension.generator.impl.gen.tostring;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.impl.gen.Annotations;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import java.util.function.Function;

public class ToString {
  private ToString() {}

  public static Generator<Pojo, Void> toStringMethod() {
    final Generator<Pojo, Void> method =
        MethodGen.<Pojo, Void>modifiers(PUBLIC)
            .returnType("String")
            .methodName("toString")
            .noArguments()
            .content("return toString(self());");
    return Annotations.<Pojo, Void>overrideAnnotation().append(method);
  }

  public static Generator<Pojo, Void> staticToStringMethod() {
    final Function<Pojo, String> argument = p -> String.format("%s o", p.getName());
    return MethodGen.<Pojo, Void>modifiers(PUBLIC, STATIC)
        .returnType("String")
        .methodName("toString")
        .singleArgument(argument)
        .content(Generator.emptyGen());
  }
}
