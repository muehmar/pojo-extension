package io.github.muehmar.pojoextension.processor;

import static io.github.muehmar.pojoextension.Booleans.not;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.generator.model.Argument;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethod;
import io.github.muehmar.pojoextension.generator.model.FieldBuilderMethodBuilder;
import io.github.muehmar.pojoextension.generator.model.Name;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.util.Optional;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class FieldBuilderProcessor {
  private FieldBuilderProcessor() {}

  public static PList<FieldBuilderMethod> process(TypeElement element) {
    final PList<ElementAndAnnotation> elementAndAnnotations =
        PList.fromIter(element.getEnclosedElements())
            .flatMapOptional(FieldBuilderProcessor::getAnnotatedElements);

    return elementAndAnnotations
        .flatMap(FieldBuilderProcessor::processClass)
        .concat(elementAndAnnotations.flatMapOptional(FieldBuilderProcessor::processMethod));
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
    final PList<Argument> arguments =
        PList.fromIter(method.getParameters()).map(ArgumentMapper::toArgument);

    return FieldBuilderMethodBuilder.create()
        .fieldName(Name.fromString(fieldBuilder.fieldName()))
        .methodName(Name.fromString(method.getSimpleName().toString()))
        .returnType(returnType)
        .arguments(arguments)
        .andAllOptionals()
        .innerClassName(innerClassName)
        .build();
  }

  private static void throwMethodNotStaticException(ExecutableElement method) {
    final String message =
        String.format(
            "Method %s is not static but is annotated with @FieldBuilder. A method annotated "
                + "with @FieldBuilder must be static.",
            method.getSimpleName());
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
  }
}
