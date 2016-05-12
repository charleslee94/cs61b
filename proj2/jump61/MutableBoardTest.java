package jump61;

import static org.junit.Assert.*;

import org.junit.Test;

public class MutableBoardTest {

    @Test
    public void test() {
        int n = 0;
        MutableBoard B = new MutableBoard(5);
        B.set(n, 1, Color.RED);
        if (B.isNeighbors(n + 1, n)) {
            B.set(n + 1, 1, Color.RED);
        }
        if (B.isNeighbors(n - 1, n)) {
            B.set(n - 1, 1, Color.RED);
        }
        if (B.isNeighbors(n + B.size(), n)) {
            B.set(n + B.size(), 1, Color.RED);
        }
        if (B.isNeighbors(n - B.size(), n)) {
            B.set(n - B.size(), 1, Color.RED);
        }
        //System.out.println(B);
        assertEquals(B.spots(n + 1), 1);
        assertEquals(B.spots(n - 1), 1);
        assertEquals(B.spots(n + B.size()), 1);
        assertEquals(B.spots(n - B.size()), 1);
    }

}
