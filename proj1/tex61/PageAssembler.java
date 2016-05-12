package tex61;

/** A PageAssembler accepts complete lines of text (minus any
 *  terminating newlines) and turns them into pages, adding form
 *  feeds as needed.  It prepends a form feed (Control-L  or ASCII 12)
 *  to the first line of each page after the first.  By overriding the
 *  'write' method, subtypes can determine what is done with
 *  the finished lines.
 *  @author Charles Lee
 */
abstract class PageAssembler {

    /** Create a new PageAssembler that sends its output to OUT.
     *  Initially, its text height is unlimited. It prepends a form
     *  feed character to the first line of each page except the first. */
    PageAssembler() {
        textHeight = Integer.MAX_VALUE;
        numLines = 0;
    }

    /** Add LINE to the current page, starting a new page with it if
     *  the previous page is full. A null LINE indicates a skipped line,
     *  and has no effect at the top of a page. */
    void addLine(String line) {
        if (getNumlines() > getHeight()) {
            if (line.equals("")) {
                return;
            }
            write("/f" + line);
        } else {
            write(line);
            numLines++;
        }
    }

    /** Set text height to VAL, where VAL > 0. */
    void setTextHeight(int val) {
        val = textHeight;
    }
    /** Perform final disposition of LINE, as determined by the
     *  concrete subtype. */
    abstract void write(String line);

    /** Int textHeight of page. */
    private int textHeight;
    /** Returns INT TextHeight variable. */
    int getHeight() {
        return textHeight;
    }
    /**  INT number of lines that are on the page. */
    private int numLines;
    /** Returns NUMLINES. */
    int getNumlines() {
        return numLines;
    }
}
