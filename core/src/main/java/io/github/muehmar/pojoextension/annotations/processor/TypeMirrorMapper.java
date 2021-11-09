package io.github.muehmar.pojoextension.annotations.processor;

import static javax.lang.model.type.TypeKind.BOOLEAN;
import static javax.lang.model.type.TypeKind.BYTE;
import static javax.lang.model.type.TypeKind.CHAR;
import static javax.lang.model.type.TypeKind.DOUBLE;
import static javax.lang.model.type.TypeKind.FLOAT;
import static javax.lang.model.type.TypeKind.INT;
import static javax.lang.model.type.TypeKind.LONG;
import static javax.lang.model.type.TypeKind.SHORT;
import static javax.lang.model.type.TypeKind.VOID;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Type;
import java.util.function.Function;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class TypeMirrorMapper {
  private static final PList<Mapper> MAPPER_MAP = createMapperMap();

  private TypeMirrorMapper() {}

  public static Type map(TypeMirror typeMirror) {
    return MAPPER_MAP
        .find(mapper -> mapper.getTypeKind().equals(typeMirror.getKind()))
        .map(mapper -> mapper.apply(typeMirror))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "TypeKind "
                        + typeMirror.getKind()
                        + " not supported for TypeMirror "
                        + typeMirror));
  }

  private static PList<Mapper> createMapperMap() {
    return PList.of(
        declaredTypeMapper(),
        arrayTypeMapper(),
        Mapper.forFixMapping(BOOLEAN, Type.primitive("boolean")),
        Mapper.forFixMapping(CHAR, Type.primitive("char")),
        Mapper.forFixMapping(INT, Type.primitive("int")),
        Mapper.forFixMapping(FLOAT, Type.primitive("float")),
        Mapper.forFixMapping(DOUBLE, Type.primitive("double")),
        Mapper.forFixMapping(LONG, Type.primitive("long")),
        Mapper.forFixMapping(SHORT, Type.primitive("short")),
        Mapper.forFixMapping(BYTE, Type.primitive("byte")),
        Mapper.forFixMapping(VOID, Type.voidType()));
  }

  private static Mapper declaredTypeMapper() {
    return Mapper.forKindAndClass(
        TypeKind.DECLARED,
        DeclaredType.class,
        declaredType -> {
          final PList<Type> typeParameters =
              PList.fromIter(declaredType.getTypeArguments()).map(TypeMirrorMapper::map);

          return Type.fromClassName(declaredType.toString()).withTypeParameters(typeParameters);
        });
  }

  private static Mapper arrayTypeMapper() {
    return Mapper.forKindAndClass(
        TypeKind.ARRAY,
        ArrayType.class,
        arrayType -> map(arrayType.getComponentType()).withIsArray(true));
  }

  private static class Mapper {
    private final TypeKind typeKind;
    private final Function<TypeMirror, Type> f;

    public Mapper(TypeKind typeKind, Function<TypeMirror, Type> f) {
      this.typeKind = typeKind;
      this.f = f;
    }

    public static Mapper forFixMapping(TypeKind typeKind, Type type) {
      return new Mapper(typeKind, ignore -> type);
    }

    public static <T extends TypeMirror> Mapper forKindAndClass(
        TypeKind typeKind, Class<T> clazz, Function<T, Type> f) {
      return new Mapper(
          typeKind,
          typeMirror -> {
            try {
              final T casted = clazz.cast(typeMirror);
              return f.apply(casted);
            } catch (ClassCastException e) {
              throw new IllegalStateException(
                  "TypeMirror of kind "
                      + typeKind
                      + " expected to be in instance of "
                      + clazz.getName()
                      + "but is "
                      + typeMirror.getClass().getName());
            }
          });
    }

    public TypeKind getTypeKind() {
      return typeKind;
    }

    public Type apply(TypeMirror typeMirror) {
      return f.apply(typeMirror);
    }
  }
}
