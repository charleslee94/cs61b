package jump61;

import static org.junit.Assert.*;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.junit.Test;

public class GameTest {
    public void setUp() {
        output = new OutputStreamWriter(System.out);
        game = new Game(new InputStreamReader(System.in), output, output,
            new OutputStreamWriter(System.err));
    }

    @Test
    public void testMoves() {
        setUp();
        game.makeMove(6, 6);
        game.makeMove(6, 5);
        assertEquals("Wrong move.", game.getMut().spots(6, 5), 1);
        assertEquals("Wrong move.", game.getMut().spots(6, 6), 1);
        game.makeMove(6, 6);
        game.makeMove(6, 5);
        game.makeMove(6, 6);
        assertEquals("Wrong move.", game.getMut().spots(6, 5), 3);
        assertEquals("Wrong move.", game.getMut().spots(6, 6), 1);
        assertEquals("Wrong move.", game.getMut().spots(5, 6), 1);
    }
    @Test
    public void testFails() {
        setUp();
        int[] move = {10, 10};
        Boolean bool = game.getMove(move);
        game.makeMove(10, 10);
        assertEquals("Illegal move did not error.", bool, false);
    }
    private Game game;
    /** Writer output. */
    private Writer output;
}
