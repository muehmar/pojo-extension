package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_ARRAYS;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OBJECTS;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Updater;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.Annotations;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HashCodeGens {

  private HashCodeGens() {}

  public static Generator<Pojo, PojoSettings> hashCodeMethod() {
    final Generator<Pojo, PojoSettings> method =
        MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("hashCode")
            .noArguments()
            .contentWriter(w -> w.println("return hashCode(self());"));
    return Annotations.<Pojo, PojoSettings>overrideAnnotation().append(method);
  }

  public static Generator<Pojo, PojoSettings> staticHashCodeMethod() {
    final Function<Pojo, String> argument = p -> String.format("%s o", p.getName());
    final Generator<Pojo, PojoSettings> content = staticHashCodeMethodContent();
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType("int")
        .methodName("hashCode")
        .singleArgument(argument)
        .content(content);
  }

  private static Generator<Pojo, PojoSettings> staticHashCodeMethodContent() {
    final BiFunction<Writer, FieldGetter, Writer> arrayHashCode = arrayHashCodeFragment();
    final BiFunction<Writer, FieldGetter, Writer> arrayHashCodeAdd = arrayHashCodeAddFragment();
    final BiFunction<Writer, PList<FieldGetter>, Writer> objectsHashCode =
        objectsHashCodeFragment();

    return (p, s, w) -> {
      final PList<FieldGetter> fieldGetters = p.getAllGettersOrThrow();
      if (fieldGetters.isEmpty()) {
        return w.println("return 0;");
      }

      final PList<FieldGetter> nonArrayFields =
          fieldGetters.filter(fg -> fg.getField().getType().isNotArray());
      final PList<FieldGetter> arrayFields =
          fieldGetters.filter(fg -> fg.getField().getType().isArray());

      final PList<FieldGetter> remainingArrayFields =
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
  }

  private static BiFunction<Writer, PList<FieldGetter>, Writer> objectsHashCodeFragment() {
    return (w, fields) ->
        w.print(
                "Objects.hash(%s)",
                fields.map(fg -> String.format("o.%s()", fg.getGetter().getName())).mkString(", "))
            .ref(JAVA_UTIL_OBJECTS);
  }

  private static BiFunction<Writer, FieldGetter, Writer> arrayHashCodeFragment() {
    return (w, fieldGetter) ->
        w.print("Arrays.hashCode(o.%s())", fieldGetter.getGetter().getName()).ref(JAVA_UTIL_ARRAYS);
  }

  private static BiFunction<Writer, FieldGetter, Writer> arrayHashCodeAddFragment() {
    return (w, fieldGetter) ->
        arrayHashCodeFragment().apply(w.print("result = 31 * result + "), fieldGetter).println(";");
  }
}
