package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.Booleans.not;
import static io.github.muehmar.pojoextension.Functions.mapFirst;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.exception.PojoExtensionException;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethodBuilder;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import io.github.muehmar.pojoextension.generator.model.type.Types;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class FieldBuilderProcessor {
  private FieldBuilderProcessor() {}

  public static PList<io.github.muehmar.pojoextension.generator.model.FieldBuilder> process(
      TypeElement element) {
    final Map<String, NonEmptyList<ElementAndAnnotation>> elementAndAnnotationByFieldName =
        PList.fromIter(element.getEnclosedElements())
            .flatMapOptional(FieldBuilderProcessor::getAnnotatedElements)
            .groupBy(elementAndAnnotation -> elementAndAnnotation.getFieldBuilder().fieldName());

    return elementAndAnnotationByFieldName.values().stream()
        .collect(PList.collector())
        .flatMapOptional(FieldBuilderProcessor::processElementsForSameField);
  }

  public static Optional<io.github.muehmar.pojoextension.generator.model.FieldBuilder>
      processElementsForSameField(NonEmptyList<ElementAndAnnotation> elementAndAnnotations) {
    final PList<FieldBuilderMethod> singleMethodMethods =
        elementAndAnnotations.toPList().flatMapOptional(FieldBuilderProcessor::processMethod);
    final PList<FieldBuilderMethod> classMethods =
        elementAndAnnotations.toPList().flatMap(FieldBuilderProcessor::processClass);
    return NonEmptyList.fromIter(singleMethodMethods.concat(classMethods))
        .map(
            methods -> {
              final boolean disableDefaultMethods =
                  extractDisableDefaultMethods(elementAndAnnotations);
              return new io.github.muehmar.pojoextension.generator.model.FieldBuilder(
                  disableDefaultMethods, methods);
            });
  }

  public static boolean extractDisableDefaultMethods(
      NonEmptyList<ElementAndAnnotation> elementAndAnnotations) {
    final boolean allFlagsReducedWithAnd =
        elementAndAnnotations
            .map(ElementAndAnnotation::isDisableDefaultMethods)
            .reduce(Boolean::logicalAnd);
    final boolean allFlagsReducedWithOr =
        elementAndAnnotations
            .map(ElementAndAnnotation::isDisableDefaultMethods)
            .reduce(Boolean::logicalOr);
    if (allFlagsReducedWithAnd == allFlagsReducedWithOr) {
      return allFlagsReducedWithAnd;
    } else {
      return throwDisableDefaultMethodsMismatch(
          elementAndAnnotations.head().getFieldBuilder().fieldName());
    }
  }

  private static PList<FieldBuilderMethod> processClass(ElementAndAnnotation elementAndAnnotation) {
    final Element element = elementAndAnnotation.getElement();

    if (not(element.getKind().isClass())) {
      return PList.empty();
    }

    final Name innerClassName = Name.fromString(element.getSimpleName().toString());

    return PList.fromIter(elementAndAnnotation.element.getEnclosedElements())
        .filter(e -> e.getKind().equals(ElementKind.METHOD))
        .map(ExecutableElement.class::cast)
        .map(method -> fromMethod(method, elementAndAnnotation.fieldBuilder, innerClassName));
  }

  private static Optional<FieldBuilderMethod> processMethod(
      ElementAndAnnotation elementAndAnnotation) {
    return Optional.of(elementAndAnnotation.getElement())
        .filter(e -> e.getKind().equals(ElementKind.METHOD))
        .map(ExecutableElement.class::cast)
        .map(method -> fromMethod(method, elementAndAnnotation.getFieldBuilder()));
  }

  private static FieldBuilderMethod fromMethod(
      ExecutableElement method, FieldBuilder fieldBuilder) {
    return fromMethod(method, fieldBuilder, Optional.empty());
  }

  private static FieldBuilderMethod fromMethod(
      ExecutableElement method, FieldBuilder fieldBuilder, Name innerClassName) {
    return fromMethod(method, fieldBuilder, Optional.of(innerClassName));
  }

  private static FieldBuilderMethod fromMethod(
      ExecutableElement method, FieldBuilder fieldBuilder, Optional<Name> innerClassName) {
    if (not(method.getModifiers().contains(Modifier.STATIC))) {
      throwMethodNotStaticException(method);
    }

    final Type returnType = TypeMirrorMapper.map(method.getReturnType());
    final PList<Argument> arguments = extractArgumentsFromMethod(method);

    return FieldBuilderMethodBuilder.create()
        .fieldName(Name.fromString(fieldBuilder.fieldName()))
        .methodName(Name.fromString(method.getSimpleName().toString()))
        .returnType(returnType)
        .arguments(arguments)
        .andAllOptionals()
        .innerClassName(innerClassName)
        .build();
  }

  private static PList<Argument> extractArgumentsFromMethod(ExecutableElement method) {
    final PList<Argument> arguments =
        PList.fromIter(method.getParameters()).map(ArgumentMapper::toArgument);
    if (method.isVarArgs()) {
      UnaryOperator<Argument> mapToVarargs =
          arg ->
              arg.getType()
                  .onArrayType(arrayType -> arg.withType(Types.varargs(arrayType.getItemType())))
                  .orElse(arg);

      return arguments.reverse().zipWithIndex().map(mapFirst(mapToVarargs));
    } else {
      return arguments;
    }
  }

  private static void throwMethodNotStaticException(ExecutableElement method) {
    final String message =
        String.format(
            "Method %s is not static but is annotated with @FieldBuilder. A method annotated "
                + "with @FieldBuilder must be static.",
            method.getSimpleName());
    throw new IllegalArgumentException(message);
  }

  private static <T> T throwDisableDefaultMethodsMismatch(String fieldName) {
    final String message =
        String.format(
            "Multiple @FieldBuilder annotations found for the field %s, but the flag 'disableDefaultMethods' "
                + "is enable and disabled at the same time. Consider using a static class for declaring multiple methods "
                + "or disable or enable the flags in all annotations for the same field.",
            fieldName);
    throw new IllegalArgumentException(message);
  }

  private static PojoExtensionException noMethodsFoundException(String fieldName) {
    final String message = String.format("", fieldName);
    throw new IllegalArgumentException(message);
  }

  private static Optional<ElementAndAnnotation> getAnnotatedElements(Element element) {
    return Optional.ofNullable(element.getAnnotation(FieldBuilder.class))
        .map(annotation -> new ElementAndAnnotation(element, annotation));
  }

  private static class ElementAndAnnotation {
    private final Element element;
    private final FieldBuilder fieldBuilder;

    public ElementAndAnnotation(Element element, FieldBuilder fieldBuilder) {
      this.element = element;
      this.fieldBuilder = fieldBuilder;
    }

    public Element getElement() {
      return element;
    }

    public FieldBuilder getFieldBuilder() {
      return fieldBuilder;
    }

    public boolean isDisableDefaultMethods() {
      return fieldBuilder.disableDefaultMethods();
    }
  }
}
