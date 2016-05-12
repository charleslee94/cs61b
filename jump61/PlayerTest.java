package jump61;

import static org.junit.Assert.*;

import org.junit.Test;

public class PlayerTest {
    private void setUp() {
        human = new HumanPlayer(game, Color.BLUE);
    }
    @Test
    public void testColor() {
        setUp();
        Color h = human.getColor();
        assertEquals("Wrong color for HUMAN", h, Color.BLUE);
        human.setColor(Color.RED);
        assertEquals("Wrong color for HUMAN", human.getColor(), Color.RED);
    }
    /** The game. */
    private Game game;
    /** A human player. */
    private HumanPlayer human;
}
