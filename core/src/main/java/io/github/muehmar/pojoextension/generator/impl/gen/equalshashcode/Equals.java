package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.STATIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_ARRAYS;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OBJECTS;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.impl.gen.Annotations;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Equals {
  private Equals() {}

  public static Generator<Pojo, PojoSettings> equalsMethod() {
    final Generator<Pojo, PojoSettings> method =
        MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
            .returnType("boolean")
            .methodName("equals")
            .singleArgument(pojo -> "Object obj")
            .content("return equals(self(), obj);");
    return Annotations.<Pojo, PojoSettings>overrideAnnotation().append(method);
  }

  public static Generator<Pojo, PojoSettings> staticEqualsMethod() {
    final Function<Pojo, PList<String>> arguments =
        p -> PList.of(String.format("%s o1", p.getName()), "Object obj");

    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC, STATIC)
        .returnType("boolean")
        .methodName("equals")
        .arguments(arguments)
        .content(staticEqualsMethodContent());
  }

  private static Generator<Pojo, PojoSettings> staticEqualsMethodContent() {
    return staticEqualsCheckIdentity()
        .append(staticEqualsCheckNullAndSameClass())
        .append(staticEqualsCastObjectToCompare())
        .append(staticEqualsCompareFields(), Pojo::getFields);
  }

  private static Generator<Pojo, PojoSettings> staticEqualsCheckIdentity() {
    return Generator.ofWriterFunction(w -> w.println("if (o1 == obj) return true;"));
  }

  private static UnaryOperator<Writer> staticEqualsCheckNullAndSameClass() {
    return w -> w.println("if (obj == null || o1.getClass() != obj.getClass()) return false;");
  }

  private static Generator<Pojo, PojoSettings> staticEqualsCastObjectToCompare() {
    return (p, s, w) -> w.println("final %s o2 = (%s) obj;", p.getName(), p.getName());
  }

  private static Generator<PList<PojoField>, PojoSettings> staticEqualsCompareFields() {
    return (fields, s, w) -> {
      final Writer writerAfterFirstField =
          fields
              .headOption()
              .map(field -> staticEqualsCompareField().generate(field, s, w.print("return ")))
              .orElse(w.print("return true"));
      return fields
          .drop(1)
          .foldLeft(
              writerAfterFirstField,
              (writer, field) ->
                  staticEqualsCompareField()
                      .generate(field, s, writer.println().tab(2).print("&& ")))
          .println(";");
    };
  }

  private static Generator<PojoField, PojoSettings> staticEqualsCompareField() {
    return (f, s, w) -> {
      final Name pascalCaseName = f.getName().toPascalCase();
      if (f.getType().isArray()) {
        return w.print("Arrays.equals(o1.get%s(), o2.get%s())", pascalCaseName, pascalCaseName)
            .ref(JAVA_UTIL_ARRAYS);
      } else if (f.getType().equals(Type.primitiveDouble())) {
        return w.print(
            "Double.compare(o1.get%s(), o2.get%s()) == 0", pascalCaseName, pascalCaseName);
      } else if (f.getType().isPrimitive()) {
        return w.print("o1.get%s() == o2.get%s()", pascalCaseName, pascalCaseName);
      } else {
        return w.print("Objects.equals(o1.get%s(), o2.get%s())", pascalCaseName, pascalCaseName)
            .ref(JAVA_UTIL_OBJECTS);
      }
    };
  }
}
