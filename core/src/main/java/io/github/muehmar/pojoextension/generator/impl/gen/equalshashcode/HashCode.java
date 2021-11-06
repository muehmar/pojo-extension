package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_ARRAYS;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OBJECTS;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Updater;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.Annotations;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HashCode {

  private HashCode() {}

  public static Generator<Pojo, PojoSettings> hashCodeMethod() {
    final Generator<Pojo, PojoSettings> method =
        MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
            .returnType("int")
            .methodName("hashCode")
            .noArguments()
            .contentWriter(w -> w.println("return hashCode(self());"));
    return Annotations.<Pojo, PojoSettings>overrideAnnotation().append(method);
  }

  public static Generator<Pojo, PojoSettings> staticHashCodeMethod() {
    final Function<Pojo, String> argument = p -> String.format("%s o", p.getName());
    final BiFunction<Writer, PojoField, Writer> arrayHashCode =
        (w, field) ->
            w.print("Arrays.hashCode(o.get%s())", field.getName().toPascalCase())
                .ref(JAVA_UTIL_ARRAYS);
    final BiFunction<Writer, PojoField, Writer> arrayHashCodeAdd =
        (w, field) -> arrayHashCode.apply(w.print("result = 31 * result + "), field).println(";");
    final BiFunction<Writer, PList<PojoField>, Writer> objectsHashCode =
        (w, fields) ->
            w.print(
                    "Objects.hash(%s)",
                    fields
                        .map(f -> String.format("o.get%s()", f.getName().toPascalCase()))
                        .mkString(", "))
                .ref(JAVA_UTIL_OBJECTS);
    final Generator<Pojo, PojoSettings> content =
        (p, s, w) -> {
          if (p.getFields().isEmpty()) {
            return w.println("return 0;");
          }

          final PList<PojoField> nonArrayFields =
              p.getFields().filter(f -> f.getType().isNotArray());
          final PList<PojoField> arrayFields = p.getFields().filter(f -> f.getType().isArray());

          final PList<PojoField> remainingArrayFields =
              nonArrayFields.nonEmpty() ? arrayFields : arrayFields.drop(1);

          return Updater.initial(w)
              .update(wr -> wr.print("int result = "))
              .updateConditionally(
                  nonArrayFields.nonEmpty(), wr -> objectsHashCode.apply(wr, nonArrayFields))
              .updateConditionally(
                  nonArrayFields.isEmpty(), wr -> arrayHashCode.apply(wr, arrayFields.head()))
              .update(wr -> wr.println(";"))
              .update(wr -> remainingArrayFields.foldLeft(wr, arrayHashCodeAdd))
              .update(wr -> wr.println("return result;"))
              .get();
        };
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .returnType("int")
        .methodName("hashCode")
        .singleArgument(argument)
        .content(content);
  }
}
