package tex61;

import java.util.ArrayList;

/** An object that receives a sequence of words of text and formats
 *  the words into filled and justified text lines that are sent to a receiver.
 *  @author Charles Lee
 */
class LineAssembler {

    /** A new, empty line assembler with default settings of all
     *  parameters, sending finished lines to PAGES. */
    LineAssembler(PageAssembler pages) {
        _pages = pages;
        _textWidth = Defaults.TEXT_WIDTH;
        _indentation = Defaults.INDENTATION;
        _parIndentation = Defaults.PARAGRAPH_INDENTATION;
        _parSkip = Defaults.PARAGRAPH_SKIP;
        _fill = true;
        _justify = true;
        _words = new ArrayList<String>();
        _chars = 0;
        _current = "";
        _first = false;
        _formattedLines = new ArrayList<String>();
        _end = false;
    }
    /** True if endnotemode on.  */
    private boolean _end;
    /** List of formatted lines. */
    private ArrayList<String> _formattedLines;
    /** returns the FORMATTEDLINES ArrayList. */
    ArrayList<String> getterLines() {
        return _formattedLines;
    }

    /** Add TEXT to the word currently being built. */
    void addText(String text) {
        _current += text;
        finishWord();
    }
    /** Finish the current word, if any, and add to words being accumulated. */
    void finishWord() {
        addWord(_current);
        _chars += _current.length();
        _current = "";
    }

    /** Add WORD to the formatted text. */
    void addWord(String word) {
        if (_chars + _words.size() + word.length()
                + _parIndentation + _indentation <= _textWidth) {
            _words.add(word);
        } else {
            if (_words.size() == 0) {
                _words.add(word);
            } else {
                outputLine(_final);
                if (_end) {
                    setIndentation(4);
                }
                addWord(word);
            }
        }
    }
    /** Add LINE to our output, with no preceding paragraph skip.  There must
     *  not be an unfinished line pending. */
    void addLine(String line) {
        _formattedLines.add(line);
    }

    /** Set the current indentation to VAL. VAL >= 0. */
    void setIndentation(int val) throws IllegalArgumentException {
        if (val == (int) val) {
            if (val >= 0) {
                _indentation = val;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    /** Set the current paragraph indentation to VAL. VAL >= 0. */
    void setParIndentation(int val) throws IllegalArgumentException {
        if (val == (int) val) {
            if (val >= Defaults.ENDNOTE_PARAGRAPH_INDENTATION) {
                _parIndentation = val;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
    /** Set the text width to VAL, where VAL >= 0. */
    void setTextWidth(int val) throws IllegalArgumentException {
        if (val == (int) val) {
            if (val >= 0) {
                _textWidth = val;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
    /** Iff ON, set fill mode. */
    void setFill(boolean on) {
        _fill = on;
    }
    /** Iff ON, set justify mode (which is active only when filling is
     *  also on). */
    void setJustify(boolean on) {
        if (_fill) {
            _justify = on;
        }
        _justify = false;
    }

    /** Set paragraph skip to VAL.  VAL >= 0. */
    void setParSkip(int val) throws IllegalArgumentException {
        if (val == (int) val) {
            if (val >= 0) {
                _parSkip = val;
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
    /** Set page height to VAL > 0. */
    void setTextHeight(int val) throws IllegalArgumentException {
        if (val == (int) val) {
            if (val >= 0) {
                _pages.setTextHeight(val);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }
    /** Process the end of the current input line.  No effect if
     *  current line accumulator is empty or in fill mode.  Otherwise,
     *  adds a new complete line to the finished line queue and clears
     *  the line accumulator. */
    void newLine() {
        outputLine(_final);
    }

    /** If there is a current unfinished paragraph pending, close it
     *  out and start a new one. */
    void endParagraph() {
        outputLine(true);
        if (!_end) {
            _first = true;
        } else {
            _first = false;
        }
    }

    /** Transfer contents of _words to _pages, adding INDENT characters of
     *  indentation, and a total of SPACES spaces between words, evenly
     *  distributed.  Assumes _words is not empty.  Clears _words and _chars. */
    private void emitLine(int indent, int spaces) {
        String ind = "";
        int count = 0;
        int last = 0;
        while (count < indent) {
            ind += " ";
            count++;
        }
        _words.set(0, ind + _words.get(0));
        int wordsize = _words.size();
        if (spaces >= 3 * (wordsize - 1)) {
            for (int i = 0; i < wordsize - 1; i++) {
                _words.set(i, _words.get(i) + "   ");
            }
        } else {
            for (int i = 0; i < wordsize - 1; i++) {
                int end = (int) ((.5 + spaces * ((float)
                        (i + 1)) / (wordsize - 1))) - last;
                last += end;
                String spacespace = "";
                int j = 0;
                while (j < end) {
                    spacespace += " ";
                    j += 1;
                }
                _words.set(i + 1, spacespace + _words.get(i + 1));
            }
        }
        String line = "";
        for (String elem: _words) {
            line += elem;
        }
        addLine(line);
        _words.clear();
        _chars = 0;
    }

    /** If the line accumulator is non-empty, justify its current
     *  contents, if needed, add a new complete line to _pages,
     *  and clear the line accumulator. LASTLINE indicates the last line
     *  of a paragraph. */
    private void outputLine(boolean lastLine) {
        int spaces = 0;
        int indent = _indentation + _parIndentation;
        _parIndentation = 0;
        int counter = 0;
        if (_first) {
            while (counter < _parSkip) {
                counter++;
                addLine("");
            }
        }
        _first = false;
        if (!lastLine && _fill) {
            if (_words.size() == 1) {
                emitLine(indent, 0);
            }
            if (!_words.isEmpty() && _justify) {
                spaces = _textWidth - indent - _chars;
                emitLine(indent, spaces);
            }
        } else {
            spaces = _words.size() - 1;
            emitLine(indent, spaces);
        }
    }
    /** Sets endnote variable to true. */
    void setEnd() {
        _end = true;
    }
    /** Destination given in constructor for formatted lines. */
    private final PageAssembler _pages;
    /** TEXTWIDTH variable. */
    private int _textWidth;
    /** Indentation variable. */
    private int _indentation;
    /** Paragraph Indendation variable. */
    private int _parIndentation;
    /** Paragraph skip variable. */
    private int _parSkip;
    /** Fill variable. */
    private boolean _fill;
    /** Justify variable. */
    private boolean _justify;
    /** Words of a line. */
    private ArrayList<String> _words;
    /** The current word being processed. */
    private String _current;
    /** Returns _Words. */
    ArrayList<String> getWor() {
        return _words;
    }
    /** The number of characters in a line including spaces. */
    private int _chars;
    /** True if line is first line of a new paragraph. */
    private boolean _first;
    /** True if line is final line of a paragraph. */
    private boolean _final;
    /** Returns _fill. */
    boolean getFill() {
        return _fill;
    }
    /** Returns INDENTATION. */
    int getInd() {
        return _indentation;
    }
    /** Returns _PARINDENTATION. */
    int getParInd() {
        return _parIndentation;
    }
}
