package io.github.muehmar.pojoextension.generator.writer;

import ch.bluecare.commons.data.PList;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public final class WriterImpl implements Writer {
  private static final int DEFAULT_SPACES_PER_TAB = 2;
  private static final String NEWLINE_STRING = "\n";

  private final PList<String> refs;
  private final int refsLineNumber;
  private final PList<Line> lines;

  private final String tab;

  private final int tabs;
  private final boolean newline;

  private WriterImpl(
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

  public static WriterImpl createDefault() {
    return ofSpacesPerIndent(DEFAULT_SPACES_PER_TAB);
  }

  public static WriterImpl ofSpacesPerIndent(int spacesPerIndent) {
    final String tab = new String(new char[spacesPerIndent]).replace("\0", " ");
    return new WriterImpl(PList.empty(), -1, PList.single(Line.empty()), tab, 0, true);
  }

  private WriterImpl appendToLastLine(String fragment) {
    final PList<Line> newLines =
        this.lines
            .headOption()
            .map(l -> l.append(fragment))
            .map(l -> newline ? l.prepend(createTabs(tabs)) : l)
            .map(l -> this.lines.tail().cons(l))
            .orElse(this.lines);
    return new WriterImpl(refs, refsLineNumber, newLines, tab, tabs, false);
  }

  @Override
  public Writer print(String string, Object... args) {
    return appendToLastLine(String.format(string, args));
  }

  @Override
  public Writer println() {
    return new WriterImpl(refs, refsLineNumber, lines.cons(Line.empty()), tab, 0, true);
  }

  @Override
  public Writer tab(int tabs) {
    return new WriterImpl(refs, refsLineNumber, lines, tab, tabs, newline);
  }

  @Override
  public Writer append(int tabs, Writer other) {
    final int usedRefsLineNumber =
        this.refsLineNumber > 0 ? this.refsLineNumber : other.getRefsLineNumber();

    final UnaryOperator<Line> indentLine = line -> line.prepend(createTabs(tabs));

    final PList<Line> newLines = other.getLines().map(indentLine).concat(getLines());

    return new WriterImpl(
        this.refs.concat(other.getRefs()),
        usedRefsLineNumber,
        newLines.cons(Line.empty()),
        tab,
        0,
        true);
  }

  private PList<String> createTabs(int tabs) {
    return PList.range(0, tabs).map(ignore -> tab);
  }

  @Override
  public PList<Line> getLines() {
    return newline ? lines.drop(1) : lines;
  }

  @Override
  public Writer empty() {
    return new WriterImpl(PList.empty(), -1, PList.single(Line.empty()), tab, 0, true);
  }

  @Override
  public Writer ref(String ref) {
    return new WriterImpl(refs.cons(ref), refsLineNumber, lines, tab, tabs, newline);
  }

  @Override
  public PList<String> getRefs() {
    return refs;
  }

  @Override
  public Writer printRefs() {
    return new WriterImpl(refs, lines.size() - (newline ? 1 : 0), lines, tab, tabs, newline);
  }

  @Override
  public int getRefsLineNumber() {
    return refsLineNumber;
  }

  @Override
  public String asString() {
    final StringBuilder sb = new StringBuilder();
    final Consumer<Line> applyStringBuilder =
        line -> {
          sb.append(line.removeTrailingBlankFragments().asStringBuilder());
          sb.append(NEWLINE_STRING);
        };

    final PList<Line> reversedLines = getLines().reverse();
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
}
