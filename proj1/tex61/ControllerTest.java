package tex61;
import java.io.PrintWriter;
import java.io.StringWriter;
import static org.junit.Assert.*;

import org.junit.Test;

public class ControllerTest {
    /** Sets up test. */
    private void setUp() {
        output = new StringWriter();
        _out = new PrintWriter(output);
        _pages = new PagePrinter(_out);
        _line = new LineAssembler(_pages);
        _cont = new Controller(_out);


    }
    @Test
    public void testAddText() {
        setUp();
        _cont.addText(string);
        assertEquals("Didn't add string",
                _cont.getline().getWor().get(0), string);
    }
    @Test
    public void testFormatEndnote() {
        setUp();
        _cont.formatEndnote(string);
        assertEquals("Endnote mode is still true", _cont.getEnd(), false);
    }
    @Test
    public void testholdEndnotes() {
        setUp();
        _cont.holdEndnote(string);
        assertEquals("Not equal", _cont.getNoteslist().get(0), string);
    }
    @Test
    public void testSetJust() {
        setUp();
        _cont.setFill(true);
        assertEquals("Fill should be on", _line.getFill(), true);
    }
    /** PrintWriter _out. */
    private PrintWriter _out;
    /** String. */
    private String string = "HOWDY";
    /** LineAssembler _LINE. */
    private LineAssembler _line;
    /** Controller CONT. */
    private Controller _cont;
    /** PageAssembler _PAGES. */
    private PageAssembler _pages;
    /**StringWriter OUTPUT. */
    private StringWriter output;
    /** LineAssembler for endnotes _endpage. */
}
