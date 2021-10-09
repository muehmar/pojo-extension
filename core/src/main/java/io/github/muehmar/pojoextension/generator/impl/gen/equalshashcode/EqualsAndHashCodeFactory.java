package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import java.util.function.Function;

public class EqualsAndHashCodeFactory {
  private EqualsAndHashCodeFactory() {}

  public static Generator<Pojo, PojoSettings> equalsMethod() {
    final Function<Pojo, PList<String>> arguments =
        p -> PList.of(String.format("%s o1", p.getPojoName().asString()), "Object obj");

    final Generator<Pojo, PojoSettings> content =
        Generator.<Pojo, PojoSettings>ofWriterFunction(
                w -> w.println("if (o1 == obj) return true;"))
            .append(
                w ->
                    w.println("if (obj == null) || o1.getClass() != obj.getClass()) return false;"))
            .append(
                (p, s, w) ->
                    w.println(
                        "final %s o2 = (%s) obj;",
                        p.getPojoName().asString(), p.getPojoName().asString()))
            .append(
                (p, s, w) -> {
                  final PList<String> fields =
                      p.getFields()
                          .map(f -> f.getName().toPascalCase().asString())
                          .map(f -> String.format("Object.equals(o1.get%s(), o2.get%s())", f, f));

                  fields.

                  final String equals =
                      NonEmptyList.fromIter(fields)
                          .orElse(NonEmptyList.single("true"))
                          .toPList()
                          .mkString(" && ");
                  return w.println("return %s;", equals);
                });

    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .returnType("boolean")
        .methodName("equals")
        .arguments(arguments)
        .content(content);
  }

  public static Generator<Pojo, PojoSettings> hashCodeMethod() {
    final Function<Pojo, String> argument = p -> String.format("%s o", p.getPojoName().asString());
    final Generator<Pojo, PojoSettings> content =
        (p, s, w) -> {
          final String fields =
              p.getFields()
                  .map(f -> f.getName().toPascalCase().asString())
                  .map(f -> String.format("o.get%s()", f))
                  .mkString(", ");
          return w.println("return Objects.hashCode(%s);", fields);
        };
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .returnType("int")
        .methodName("hashCode")
        .singleArgument(argument)
        .content(content);
  }
}
