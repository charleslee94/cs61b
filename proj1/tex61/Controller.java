package tex61;

import java.io.PrintWriter;
import java.util.ArrayList;

/** Receives (partial) words and commands, performs commands, and
 *  accumulates and formats words into lines of text, which are sent to a
 *  designated PageAssembler.  At any given time, a Controller has a
 *  current word, which may be added to by addText, a current list of
 *  words that are being accumulated into a line of text, and a list of
 *  lines of endnotes.
 *  @author Charles
 */
class Controller {

    /** A new Controller that sends formatted output to OUT. */
    Controller(PrintWriter out) {
        _out = out;
        _endnoteMode = false;
        _refNum = 0;
        _list = new ArrayList<String>();
        _pages = new PageCollector(_list);
        _line = new LineAssembler(_pages);
        _endNotesList = new ArrayList<String>();
        _newpages = new PageCollector(_line.getterLines());
        _endNotes = new LineAssembler(_pages);
        _endPage = new LineAssembler(_pages);
        _printer = new PagePrinter(_out);
        _endpages = new PageCollector(_endNotes.getterLines());
    }
    /** ArrayList for LineAssembler. */
    private ArrayList<String> _list;
    /** ArrayList of endnotes. */
    private ArrayList<String> _endNotesList;
    /** Returns endnote list. */
    ArrayList<String> getNoteslist() {
        return _endNotesList;
    }
    /** PageCollector for endnotes. */
    private PageCollector _endpages;
    /** PageCollector for pages. */
    private PageCollector _newpages;
    /** PagePrinter for printing pages. */
    private PagePrinter _printer;
    /** PrintWriter that prints. */
    private PrintWriter _out;
    /** LineAssembler for endnotes. */
    private LineAssembler _endPage;
    /** Return endnoteMode. */
    boolean getEnd() {
        return _endnoteMode;
    }

    /** Add TEXT to the end of the word of formatted text currently
     *  being accumulated. */
    void addText(String text) {
        if (_endnoteMode) {
            _endPage.addText(text);
        } else {
            _line.addText(text);
        }
    }

    /** Finish any current word of text and, if present, add to the
     *  list of words for the next line.  Has no effect if no unfinished
     *  word is being accumulated. */
    void endWord() {
        _line.finishWord();
    }

    /** Finish any current word of formatted text and process an end-of-line
     *  according to the current formatting parameters. */
    void addNewline() {
        _line.newLine();
    }

    /** Finish any current word of formatted text, format and output any
     *  current line of text, and start a new paragraph. */
    void endParagraph() {
        _line.endParagraph();
        _line.setParIndentation(Defaults.PARAGRAPH_INDENTATION);
    }

    /** If valid, process TEXT into an endnote, first appending a reference
     *  to it to the line currently being accumulated. */
    void formatEndnote(String text) {
        _refNum++;
        setEndnoteMode();
        _line.addText("[" + _refNum + "]");
        setNormalMode();
    }
    /** holds Endnotes in place with String TEXT. */
    void holdEndnote(String text) {
        _endNotesList.add(text);
    }
    /** Set the current text height (number of lines per page) to VAL, if
     *  it is a valid setting.  Ignored when accumulating an endnote. */
    void setTextHeight(int val) {
        _line.setTextHeight(val);
    }

    /** Set the current text width (width of lines including indentation)
     *  to VAL, if it is a valid setting. */
    void setTextWidth(int val) {
        _line.setTextWidth(val);
    }

    /** Set the current text indentation (number of spaces inserted before
     *  each line of formatted text) to VAL, if it is a valid setting. */
    void setIndentation(int val) {
        _line.setIndentation(val);
    }

    /** Set the current paragraph indentation (number of spaces inserted before
     *  first line of a paragraph in addition to indentation) to VAL, if it is
     *  a valid setting. */
    void setParIndentation(int val) {
        _line.setParIndentation(val);
    }

    /** Set the current paragraph skip (number of blank lines inserted before
     *  a new paragraph, if it is not the first on a page) to VAL, if it is
     *  a valid setting. */
    void setParSkip(int val) {
        _line.setParSkip(val);
    }

    /** Iff ON, begin filling lines of formatted text. */
    void setFill(boolean on) {
        _line.setFill(on);
    }

    /** Iff ON, begin justifying lines of formatted text whenever filling is
     *  also on. */
    void setJustify(boolean on) {
        _line.setJustify(on);
    }

    /** Finish the current formatted document or endnote (depending on mode).
     *  Formats and outputs all pending text. */
    void close() {
        addNewline();
        writeEndnotes();
        for (String elem: _newpages.getLines()) {
            _printer.write(elem);
        }
        for (String x: _endpages.getLines()) {
            _printer.write(x);
        }
        _out.close();
    }

    /** Start directing all formatted text to the endnote assembler. */
    private void setEndnoteMode() {
        _endnoteMode = true;

    }

    /** Return to directing all formatted text to _mainText. */
    private void setNormalMode() {
        _endnoteMode = false;
    }

    /** Write all accumulated endnotes to _mainText. */
    void writeEndnotes() {
        int k = 0;
        while (k < _refNum) {
            String[] ends = _endNotesList.get(k).split("\\s+");
            for (String elem: ends) {
                elem = elem.replaceAll("\\s",  "");
            }
            _endNotes.setIndentation(0);
            _endNotes.setParIndentation(0);
            _endNotes.addText("[" + (k + 1) + "]");
            for (String text: ends) {
                _endNotes.addText(text);
                _endNotes.setEnd();
            }
            _endNotes.setParSkip(0);
            _endNotes.endParagraph();
            k++;
        }
    }

    /** True iff we are currently processing an endnote. */
    private boolean _endnoteMode;
    /** Number of next endnote. */
    private int _refNum;
    /** LineAssembler created for lines. */
    private LineAssembler _line;
    /** Returns LineAssembler _LINE. */
    LineAssembler getline() {
        return _line;
    }
    /** PageCollector collects pages of lines. */
    private PageCollector _pages;
    /** LineAssembler for endnote lines. */
    private LineAssembler _endNotes;
    /** Returns LineAssembler _ENDNOTES. */
    LineAssembler getEndnotes() {
        return _endNotes;
    }
}

