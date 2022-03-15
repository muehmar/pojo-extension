package io.github.muehmar.pojoextension.example.customannotations;

import lombok.Value;

@Value
@DisabledMappers
public class DisabledMappersClass implements DisabledMappersClassExtension {
  String prop1;
}
