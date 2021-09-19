package io.github.muehmar.pojoextension.annotations.processor;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.OptionalDetection;
import java.util.Objects;

public class DetectionSettings {
  private final PList<OptionalDetection> optionalDetections;

  public DetectionSettings(PList<OptionalDetection> optionalDetections) {
    this.optionalDetections = optionalDetections;
  }

  public PList<OptionalDetection> getOptionalDetections() {
    return optionalDetections;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DetectionSettings detectionSettings = (DetectionSettings) o;
    return Objects.equals(optionalDetections, detectionSettings.optionalDetections);
  }

  @Override
  public int hashCode() {
    return Objects.hash(optionalDetections);
  }

  @Override
  public String toString() {
    return "Settings{" + "optionalDetections=" + optionalDetections + '}';
  }
}
