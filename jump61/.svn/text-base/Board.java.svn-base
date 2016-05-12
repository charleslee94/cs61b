package jump61;
import static jump61.Color.*;

/** Represents the state of a Jump61 game.  Squares are indexed either by
 *  row and column (between 1 and size()), or by square number, numbering
 *  squares by rows, with squares in row 1 numbered 0 - size()-1, in
 *  row 2 numbered size() - 2*size() - 1, etc.
 *  @author Charles Lee
 */
abstract class Board {

    /** (Re)initialize me to a cleared board with N squares on a side. Clears
     *  the undo history and sets the number of moves to 0. */
    void clear(int N) {
        unsupported("clear");
    }

    /** Copy the contents of BOARD into me. */
    void copy(Board board) {
        unsupported("copy");
    }

    /** Return the number of rows and of columns of THIS. */
    abstract int size();

    /** Returns the number of spots in the square at row R, column C,
     *  1 <= R, C <= size (). */
    abstract int spots(int r, int c);

    /** Returns the number of spots in square #N. */
    abstract int spots(int n);

    /** Returns the color of square #N, numbering squares by rows, with
     *  squares in row 1 number 0 - size()-1, in row 2 numbered
     *  size() - 2*size() - 1, etc. */
    abstract Color color(int n);

    /** Returns the color of the square at row R, column C,
     *  1 <= R, C <= size(). */
    abstract Color color(int r, int c);

    /** Returns the total number of moves made (red makes the odd moves,
     *  blue the even ones). */
    abstract int numMoves();

    /** Returns the Color of the player who would be next to move.  If the
     *  game is won, this will return the loser (assuming legal position). */
    Color whoseMove() {
        if (numMoves() % 2 == 0) {
            return BLUE;
        } else {
            return RED;
        }
    }

    /** Return true iff row R and column C denotes a valid square. */
    final boolean exists(int r, int c) {
        return 1 <= r && r <= size() && 1 <= c && c <= size();
    }

    /** Return true iff S is a valid square number. */
    final boolean exists(int s) {
        int N = size();
        return 0 <= s && s < N * N;
    }
    /** Return the row number for square #N. */
    final int row(int n) {
        return (int) Math.floor(n / size()) + 1;
    }

    /** Return the column number for square #N. */
    final int col(int n) {
        return n % size() + 1;
    }

    /** Return the square number of row R, column C. */
    final int sqNum(int r, int c) {
        return (r - 1) * size() + (c - 1);
    }


    /** Returns true iff it would currently be legal for PLAYER to add a spot
        to square at row R, column C. */
    boolean isLegal(Color player, int r, int c) {
        if (exists(r, c)) {
            if (color(r, c).equals(player) || color(r, c).equals(WHITE)) {
                return true;
            }
        }
        return false;
    }

    /** Returns true iff it would currently be legal for PLAYER to add a spot
     *  to square #N. */
    boolean isLegal(Color player, int n) {
        if (exists(n)) {
            if (color(n).equals(player) || color(n).equals(WHITE)) {
                return true;
            }
        }
        return false;
    }

    /** Returns true iff PLAYER is allowed to move at this point. */
    boolean isLegal(Color player) {
        if (player.equals(whoseMove())) {
            return true;
        }
        return false;
    }

    /** Returns the winner of the current position, if the game is over,
     *  and otherwise null. */
    final Color getWinner() {
        Color player = WHITE;
        if (numMoves() % 2 == 0) {
            player = RED;
        } else {
            player = BLUE;
        }
        return player;
    }

    /** Return the number of squares of given COLOR. */
    abstract int numOfColor(Color color);

    /** Add a spot from PLAYER at row R, column C.  Assumes
     *  isLegal(PLAYER, R, C). */
    void addSpot(Color player, int r, int c) {
        unsupported("addSpot");
    }

    /** Add a spot from PLAYER at square #N.  Assumes isLegal(PLAYER, N). */
    void addSpot(Color player, int n) {
        unsupported("addSpot");
    }

    /** Set the square at row R, column C to NUM spots (0 <= NUM), and give
     *  it color PLAYER if NUM > 0 (otherwise, white).  Clear the undo
     *  history. */
    void set(int r, int c, int num, Color player) {
        unsupported("set");
    }

    /** Set the square #N to NUM spots (0 <= NUM), and give it color PLAYER
     *  if NUM > 0 (otherwise, white).  Clear the undo history. */
    void set(int n, int num, Color player) {
        unsupported("set");
    }

    /** Set the current number of moves to N.  Clear the undo history. */
    void setMoves(int n) {
        unsupported("setMoves");
    }

    /** Undo the effects one move (that is, one addSpot command).  One
     *  can only undo back to the last point at which the undo history
     *  was cleared, or the construction of this Board. */
    void undo() {
        unsupported("undo");
    }

    /** Returns my dumped representation. */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("===\n");
        final int n = size();
        for (int row = 1; row <= n; row++) {
            sb.append("    ");
            for (int col = 1; col <= n; col++) {
                final Square square = getGrid()[sqNum(row, col)];
                sb.append(square.toString());
                final boolean lastCol = (col == (n));
                sb.append(lastCol ? "\n" : " ");
            }
        }
        sb.append("===");
        return sb.toString();
    }

    /** Returns the number of neighbors of the square at row R, column C. */
    int neighbors(int r, int c) {
        if ((r == 1 || r == size())) {
            if (c == 1 || c == size()) {
                return 2;
            }
            return 3;
        }
        if ((c == 1 || c == size())) {
            return 3;
        }
        return 4;
    }
    /** Returns the number of neighbors of square #N. */
    int neighbors(int n) {
        int r = row(n);
        int c = col(n);
        return neighbors(r, c);
    }

    /** Indicate fatal error: OP is unsupported operation. */
    private void unsupported(String op) {
        String msg = String.format("'%s' operation not supported", op);
        throw new UnsupportedOperationException(msg);
    }
    /** Checks to see if the game is over.
     * Returns false if there is no winner. */
    public boolean gameOver() {
        Color winner = _grid[0].getCol();
        if (winner == WHITE) {
            return false;
        }
        for (Square spaces : _grid) {
            if (spaces.getCol() != winner) {
                return false;
            }
        }
        return true;
    }

    /** The length of an end of line on this system. */
    private static final int NL_LENGTH =
        System.getProperty("line.separator").length();
    /** A board before the last move was made. */
    protected Board _lastBoard;
    /** Returns _LASTBOARD. */
    Board getLast() {
        return _lastBoard;
    }
    /** Set of squares representing a game board. */
    protected Square[] _grid;
    /** Returns _grid. */
    Square[] getGrid() {
        return _grid;
    }
    /** Sets the _grid to N. */
    void setGrid(int N) {
        _grid = new Square[N];
    }
    /** Class representing each individual square in a N x N board. */
    class Square {
        /** Default color of a Square. */
        private Color _color = WHITE;
        /** Default number of spots in a Square. */
        private int _spots = 0;
        @Override
        /** Returns string of THIS. */
        public String toString() {
            if (_color == WHITE) {
                return "--";
            }
            String col;
            if (_color == RED) {
                col = "r";
            } else {
                col = "b";
            }
            return String.format("%d%s", _spots, col);
        }
        /** Returns color of THIS. */
        Color getCol() {
            return _color;
        }
        /** Sets COLOR of THIS. */
        void setCol(Color color) {
            this._color = color;
        }
        /** Returns spots of THIS. */
        public int getSpots() {
            return _spots;
        }
        /** Sets _spots to SPOTS. */
        public void setSpots(int spots) {
            this._spots = spots;
        }
        /** Adds 1 spot to square. */
        public void addS() {
            _spots += 1;
        }
        /** Subtracts 1 spot from square. */
        public void minusS() {
            _spots -= 1;
        }
        /** Returns a copy of itself. */
        public Square cop() {
            Square sq = new Square();
            sq.setSpots(this._spots);
            sq.setCol(this._color);
            return sq;
        }
    }
}
