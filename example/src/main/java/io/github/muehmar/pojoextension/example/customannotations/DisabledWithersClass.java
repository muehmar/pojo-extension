package io.github.muehmar.pojoextension.example.customannotations;

import lombok.Value;

@Value
@DisabledWithers
public class DisabledWithersClass implements DisabledWithersClassExtension {
  String prop1;
}
