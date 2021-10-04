package io.github.muehmar.pojoextension.generator.writer;

import ch.bluecare.commons.data.PList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class Writer {
  private static final int DEFAULT_SPACES_PER_TAB = 2;
  private static final String NEWLINE_STRING = "\n";

  private final PList<String> refs;
  private final int refsLineNumber;
  private final PList<Line> lines;

  private final String tab;

  private final int tabs;
  private final boolean newline;

  private Writer(
      PList<String> refs,
      int refsLineNumber,
      PList<Line> lines,
      String tab,
      int tabs,
      boolean newline) {
    this.refs = refs;
    this.refsLineNumber = refsLineNumber;
    this.lines = lines;
    this.tab = tab;
    this.tabs = tabs;
    this.newline = newline;
  }

  public static Writer createDefault() {
    return ofSpacesPerIndent(DEFAULT_SPACES_PER_TAB);
  }

  public static Writer ofSpacesPerIndent(int spacesPerIndent) {
    final String tab = new String(new char[spacesPerIndent]).replace("\0", " ");
    return new Writer(PList.empty(), -1, PList.single(Line.empty()), tab, 0, true);
  }

  private Writer appendToLastLine(String fragment) {
    final PList<Line> newLines =
        this.lines
            .headOption()
            .map(l -> l.append(fragment))
            .map(l -> newline ? l.prepend(createTabs(tabs)) : l)
            .map(l -> this.lines.tail().cons(l))
            .orElse(this.lines);
    return new Writer(refs, refsLineNumber, newLines, tab, tabs, false);
  }

  public Writer print(String string, Object... args) {
    return appendToLastLine(String.format(string, args));
  }

  public Writer println() {
    return new Writer(refs, refsLineNumber, lines.cons(Line.empty()), tab, 0, true);
  }

  public Writer tab(int tabs) {
    return new Writer(refs, refsLineNumber, lines, tab, tabs, newline);
  }

  public Writer append(int tabs, Writer other) {
    final int usedRefsLineNumber =
        this.refsLineNumber > 0 ? this.refsLineNumber : other.refsLineNumber;

    final UnaryOperator<Line> indentLine = line -> line.prepend(createTabs(tabs));

    final PList<Line> newLines =
        other.getLinesDroppingLastNewline().map(indentLine).concat(getLinesDroppingLastNewline());

    return new Writer(
        this.refs.concat(other.refs),
        usedRefsLineNumber,
        newLines.cons(Line.empty()),
        tab,
        0,
        true);
  }

  private PList<String> createTabs(int tabs) {
    return PList.range(0, tabs).map(ignore -> tab);
  }

  private PList<Line> getLinesDroppingLastNewline() {
    return newline ? lines.drop(1) : lines;
  }

  public Writer empty() {
    return new Writer(PList.empty(), -1, PList.single(Line.empty()), tab, 0, true);
  }

  public Writer ref(String ref) {
    return new Writer(refs.cons(ref), refsLineNumber, lines, tab, tabs, newline);
  }

  public PList<String> getRefs() {
    return refs;
  }

  public Writer printRefs() {
    return new Writer(refs, lines.size() - (newline ? 1 : 0), lines, tab, tabs, newline);
  }

  public String asString() {
    final StringBuilder sb = new StringBuilder();
    final Consumer<Line> applyStringBuilder =
        line -> {
          sb.append(line.removeTrailingBlankFragments().asStringBuilder());
          sb.append(NEWLINE_STRING);
        };

    final PList<Line> reversedLines = getLinesDroppingLastNewline().reverse();
    reversedLines.take(Math.max(refsLineNumber, 0)).forEach(applyStringBuilder);

    if (refsLineNumber >= 0) {
      refs.distinct(Function.identity())
          .sort(Comparator.comparing(Function.identity()))
          .filter(ref -> !ref.startsWith("java.lang"))
          .map(ref -> String.format("import %s;", ref))
          .forEach(ref -> sb.append(ref).append(NEWLINE_STRING));
    }

    reversedLines.drop(Math.max(refsLineNumber, 0)).forEach(applyStringBuilder);
    return sb.toString();
  }

  public Writer print(char value) {
    return print("" + value);
  }

  public Writer print(int value) {
    return print("" + value);
  }

  public Writer print(String string) {
    return print(string, new Object[0]);
  }

  public Writer println(char value) {
    return print(value).println();
  }

  public Writer println(int value) {
    return print(value).println();
  }

  public Writer println(String string) {
    return print(string).println();
  }

  public Writer println(String string, Object... args) {
    return print(string, args).println();
  }

  public Writer append(Writer other) {
    return append(0, other);
  }
}
