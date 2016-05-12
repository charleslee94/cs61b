package tex61;
import java.io.StringWriter;
import java.io.PrintWriter;
import static org.junit.Assert.*;

import org.junit.Test;

public class LineAssemblerTest {

    private void setUp() {
        output = new StringWriter();
        _out = new PrintWriter(output);
        _pages = new PagePrinter(_out);
        _line = new LineAssembler(_pages);
    }
    @Test
    public void testAddText() {
        setUp();
        _line.addText(text);
        assertEquals("Text doesn't match", _line.getWor().get(0), text);
    }
    @Test
    public void testAddWord() {
        setUp();
        _line.addWord(text);
        assertEquals("Text doesn't match", _line.getWor().get(0), text);
    }
    @Test
    public void testAddLine() {
        setUp();
        _line.addLine(text);
        String line =  _line.getterLines().get(0);
        assertEquals("Lines do not match", line, text);
    }
    @Test
    public void testSetJustify() {
        setUp();
        _line.setJustify(true);
        assertEquals("Both justify and fill are true", true, _line.getFill());
    }
    @Test
    public void testSetting() {
        setUp();
        int val = 5;
        _line.setIndentation(val);
        _line.setParIndentation(val);
        assertEquals("Both are 5", _line.getInd(), _line.getParInd());
    }
    /** StringWriter OUTPUT. */
    private StringWriter output;
    /** PrintWriter _OUT. */
    private PrintWriter _out;
    /** PagePrinter _PAGES. */
    private PagePrinter _pages;
    /** LineAssembler _LINE. */
    private LineAssembler _line;
    /** String TEXT. */
    private String text = "Hello World";

}
