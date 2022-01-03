package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.RecordExtension;
import java.util.Optional;

@RecordExtension
public record SampleRecord(long id, String name, Optional<String> data)
    implements SampleRecordExtension {}
