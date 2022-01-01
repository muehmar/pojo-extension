package io.github.muehmar.pojoextension.generator.impl.gen.equalshashcode;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.FieldGetter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.Pojo;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.UnaryOperator;

import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_ARRAYS;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_OBJECTS;

public class EqualsGens {
  private EqualsGens() {}

  public static Generator<Pojo, PojoSettings> genEqualsMethod() {
    final Generator<Pojo, PojoSettings> method =
        MethodGen.<Pojo, PojoSettings>modifiers()
            .noGenericTypes()
            .returnType("boolean")
            .methodName("genEquals")
            .singleArgument(pojo -> "Object obj")
            .content(genEqualsMethodContent());
    return method.filter((p, s) -> s.getEqualsHashCodeAbility().isEnabled());
  }

  private static Generator<Pojo, PojoSettings> genEqualsMethodContent() {
    return genEqualsCheckIdentity()
        .append(genEqualsCheckNullAndSameClass())
        .append(genEqualsCastObjectToCompare())
        .append(genEqualsCompareFields(), Pojo::getAllGettersOrThrow);
  }

  private static Generator<Pojo, PojoSettings> genEqualsCheckIdentity() {
    return Generator.ofWriterFunction(w -> w.println("if (this == obj) return true;"));
  }

  private static UnaryOperator<Writer> genEqualsCheckNullAndSameClass() {
    return w -> w.println("if (obj == null || this.getClass() != obj.getClass()) return false;");
  }

  private static Generator<Pojo, PojoSettings> genEqualsCastObjectToCompare() {
    return (p, s, w) ->
        w.println(
            "final %s%s other = (%s%s) obj;",
            p.getName(),
            p.getTypeVariablesWildcardSection(),
            p.getName(),
            p.getTypeVariablesWildcardSection());
  }

  private static Generator<PList<FieldGetter>, PojoSettings> genEqualsCompareFields() {
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

  private static Generator<FieldGetter, PojoSettings> staticEqualsCompareField() {
    return (fg, s, w) -> {
      final Name getterName = fg.getGetter().getName();
      if (fg.getField().getType().isArray()) {
        return w.print("Arrays.equals(%s(), other.%s())", getterName, getterName)
            .ref(JAVA_UTIL_ARRAYS);
      } else if (fg.getField().getType().equals(Type.primitiveDouble())) {
        return w.print("Double.compare(%s(), other.%s()) == 0", getterName, getterName);
      } else if (fg.getField().getType().isPrimitive()) {
        return w.print("%s() == other.%s()", getterName, getterName);
      } else {
        return w.print("Objects.equals(%s(), other.%s())", getterName, getterName)
            .ref(JAVA_UTIL_OBJECTS);
      }
    };
  }
}
