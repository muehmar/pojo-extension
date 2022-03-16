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

public class AnnotationMemberExtractor {

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

  private AnnotationMemberExtractor() {}

  public static Optional<PList<OptionalDetection>> getOptionalDetection(
      AnnotationMirror annotationMirror) {
    return getMember(
        annotationMirror,
        new ExtensionMember<>(
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
    return getMember(annotationMirror, new ExtensionMember<>(EXTENSION_NAME, String.class::cast));
  }

  public static Optional<Boolean> getEnableSafeBuilder(AnnotationMirror annotationMirror) {
    return getMember(
        annotationMirror, new ExtensionMember<>(ENABLE_SAFE_BUILDER, Boolean.class::cast));
  }

  public static Optional<String> getBuilderName(AnnotationMirror annotationMirror) {
    return getMember(annotationMirror, new ExtensionMember<>(BUILDER_NAME, String.class::cast));
  }

  public static Optional<String> getBuilderSetMethodPrefix(AnnotationMirror annotationMirror) {
    return getMember(
        annotationMirror, new ExtensionMember<>(BUILDER_SET_METHOD_PREFIX, String.class::cast));
  }

  public static Optional<Boolean> getEnableEqualsAndHashCode(AnnotationMirror annotationMirror) {
    return getMember(
        annotationMirror, new ExtensionMember<>(ENABLE_EQUALS_AND_HASH_CODE, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableToString(AnnotationMirror annotationMirror) {
    return getMember(
        annotationMirror, new ExtensionMember<>(ENABLE_TO_STRING, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableWithers(AnnotationMirror annotationMirror) {
    return getMember(annotationMirror, new ExtensionMember<>(ENABLE_WITHERS, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableOptionalGetters(AnnotationMirror annotationMirror) {
    return getMember(
        annotationMirror, new ExtensionMember<>(ENABLE_OPTIONAL_GETTERS, Boolean.class::cast));
  }

  public static Optional<Boolean> getEnableMappers(AnnotationMirror annotationMirror) {
    return getMember(annotationMirror, new ExtensionMember<>(ENABLE_MAPPERS, Boolean.class::cast));
  }

  private static <T> Optional<T> getMember(
      AnnotationMirror annotationMirror, ExtensionMember<T> extensionMember) {
    final Function<AnnotationValue, Optional<T>> mapAnnotationValue =
        val -> {
          try {
            return Optional.of(extensionMember.map.apply(val.getValue()));
          } catch (ClassCastException e) {
            return Optional.empty();
          }
        };

    final Optional<T> explicitValue =
        PList.fromIter(annotationMirror.getElementValues().entrySet())
            .find(e -> e.getKey().getSimpleName().toString().equals(extensionMember.name))
            .map(Map.Entry::getValue)
            .flatMap(mapAnnotationValue);

    final Supplier<Optional<T>> defaultValue =
        () ->
            PList.fromIter(annotationMirror.getAnnotationType().asElement().getEnclosedElements())
                .filter(e -> e.getKind().equals(ElementKind.METHOD))
                .map(ExecutableElement.class::cast)
                .find(e -> e.getSimpleName().toString().equals(extensionMember.name))
                .map(ExecutableElement::getDefaultValue)
                .flatMap(mapAnnotationValue);

    return Optionals.or(explicitValue, defaultValue);
  }

  private static class ExtensionMember<T> {
    private final String name;
    private final Function<Object, T> map;

    public ExtensionMember(String name, Function<Object, T> map) {
      this.name = name;
      this.map = map;
    }
  }
}
