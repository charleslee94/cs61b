package tex61;
import java.io.StringWriter;
import java.io.PrintWriter;

import static org.junit.Assert.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class InputParserTest {
    /** Sets up test. */
    private void setUp() {
        output = new StringWriter();
        _out = new PrintWriter(output);
        _pages = new PagePrinter(_out);
        _line = new LineAssembler(_pages);
        _cont = new Controller(_out);
        _input = new InputParser(string, _cont);
    }

    @Test
    public void testText() {
        _input.process();
    }
    /** PrintWriter _out. */
    private PrintWriter _out;
    /** String. */
    private String string = "I LOVE PICKLES";
    /** LineAssembler _LINE. */
    private LineAssembler _line;
    /** Controller CONT. */
    private Controller _cont;
    /** PageAssembler _PAGES. */
    private PageAssembler _pages;
    /**StringWriter OUTPUT. */
    private StringWriter output;
    /** LineAssembler for endnotes _endpage. */
    private InputParser _input;
}
