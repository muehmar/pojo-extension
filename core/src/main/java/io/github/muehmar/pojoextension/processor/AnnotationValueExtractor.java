package io.github.muehmar.pojoextension.processor;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.Optionals;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;

public class AnnotationValueExtractor {
  private AnnotationValueExtractor() {}

  public static Optional<PList<OptionalDetection>> getOptionalDetection(
      AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror,
        new ExtensionValue<>(
            "optionalDetection",
            v ->
                PList.fromIter((Iterable<Object>) v)
                    .flatMapOptional(
                        o -> {
                          final String s = o.toString();
                          final int index = s.lastIndexOf(".");
                          final String name = index >= 0 ? s.substring(index + 1) : s;
                          return OptionalDetection.fromString(name);
                        })));
  }

  public static Optional<String> getExtensionName(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>("extensionName", String.class::cast));
  }

  public static Optional<Boolean> getEnableSafeBuilder(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>("enableSafeBuilder", Boolean.class::cast));
  }

  public static Optional<String> getBuilderName(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>("builderName", String.class::cast));
  }

  public static Optional<String> getBuilderSetMethodPrefix(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>("builderSetMethodPrefix", String.class::cast));
  }

  public static Optional<String> getBaseClassName(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>("baseClassName", String.class::cast));
  }

  public static Optional<Boolean> getEnableBaseClass(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>("enableBaseClass", Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableEqualsAndHashCode(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>("enableEqualsAndHashCode", Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableToString(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>("enableToString", Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableWithers(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>("enableWithers", Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableOptionalGetters(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>("enableOptionalGetters", Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableMappers(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>("enableMappers", Boolean.class::cast));
  }

  private static <T> Optional<T> getValue(
      AnnotationMirror annotationMirror, ExtensionValue<T> extensionValue) {
    final Function<AnnotationValue, Optional<T>> mapAnnotationValue =
        val -> {
          try {
            return Optional.of(extensionValue.map.apply(val.getValue()));
          } catch (ClassCastException e) {
            return Optional.empty();
          }
        };

    final Optional<T> explicitValue =
        PList.fromIter(annotationMirror.getElementValues().entrySet())
            .find(e -> e.getKey().getSimpleName().toString().equals(extensionValue.name))
            .map(Map.Entry::getValue)
            .flatMap(mapAnnotationValue);

    final Supplier<Optional<T>> defaultValue =
        () ->
            PList.fromIter(annotationMirror.getAnnotationType().asElement().getEnclosedElements())
                .filter(e -> e.getKind().equals(ElementKind.METHOD))
                .map(ExecutableElement.class::cast)
                .find(e -> e.getSimpleName().toString().equals(extensionValue.name))
                .map(ExecutableElement::getDefaultValue)
                .flatMap(mapAnnotationValue);

    return Optionals.or(explicitValue, defaultValue);
  }

  private static class ExtensionValue<T> {
    private final String name;
    private final Function<Object, T> map;

    public ExtensionValue(String name, Function<Object, T> map) {
      this.name = name;
      this.map = map;
    }
  }
}
