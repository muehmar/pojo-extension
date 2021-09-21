package io.github.muehmar.pojoextension.generator;

public interface Writer {
  Writer ref(String ref);

  Writer printRefs();

  default Writer print(char value) {
    return print("" + value);
  }

  default Writer print(int value) {
    return print("" + value);
  }

  default Writer print(String string) {
    return print(string, new Object[0]);
  }

  Writer print(String string, Object... args);

  Writer println();

  default Writer println(char value) {
    print(value);
    return println();
  }

  default Writer println(int value) {
    print(value);
    return println();
  }

  default Writer println(String string) {
    print(string);
    return println();
  }

  default Writer println(String string, Object... args) {
    print(string, args);
    return println();
  }

  Writer tab(int tabs);

  default Writer append(Writer other) {
    return append(0, other);
  }

  Writer append(int tabs, Writer other);
}
