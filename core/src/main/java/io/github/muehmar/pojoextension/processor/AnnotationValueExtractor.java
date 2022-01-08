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

  public static final String OPTIONAL_DETECTION = "optionalDetection";
  public static final String EXTENSION_NAME = "extensionName";
  public static final String ENABLE_SAFE_BUILDER = "enableSafeBuilder";
  public static final String BUILDER_NAME = "builderName";
  public static final String BUILDER_SET_METHOD_PREFIX = "builderSetMethodPrefix";
  public static final String BASE_CLASS_NAME = "baseClassName";
  public static final String ENABLE_BASE_CLASS = "enableBaseClass";
  public static final String ENABLE_EQUALS_AND_HASH_CODE = "enableEqualsAndHashCode";
  public static final String ENABLE_TO_STRING = "enableToString";
  public static final String ENABLE_WITHERS = "enableWithers";
  public static final String ENABLE_OPTIONAL_GETTERS = "enableOptionalGetters";
  public static final String ENABLE_MAPPERS = "enableMappers";

  private AnnotationValueExtractor() {}

  public static Optional<PList<OptionalDetection>> getOptionalDetection(
      AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror,
        new ExtensionValue<>(
            OPTIONAL_DETECTION,
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
    return getValue(annotationMirror, new ExtensionValue<>(EXTENSION_NAME, String.class::cast));
  }

  public static Optional<Boolean> getEnableSafeBuilder(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>(ENABLE_SAFE_BUILDER, Boolean.class::cast));
  }

  public static Optional<String> getBuilderName(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>(BUILDER_NAME, String.class::cast));
  }

  public static Optional<String> getBuilderSetMethodPrefix(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>(BUILDER_SET_METHOD_PREFIX, String.class::cast));
  }

  public static Optional<String> getBaseClassName(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>(BASE_CLASS_NAME, String.class::cast));
  }

  public static Optional<Boolean> getEnableBaseClass(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>(ENABLE_BASE_CLASS, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableEqualsAndHashCode(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>(ENABLE_EQUALS_AND_HASH_CODE, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableToString(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>(ENABLE_TO_STRING, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableWithers(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>(ENABLE_WITHERS, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableOptionalGetters(AnnotationMirror annotationMirror) {
    return getValue(
        annotationMirror, new ExtensionValue<>(ENABLE_OPTIONAL_GETTERS, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableMappers(AnnotationMirror annotationMirror) {
    return getValue(annotationMirror, new ExtensionValue<>(ENABLE_MAPPERS, Boolean.class::cast));
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
