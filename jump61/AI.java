package jump61;

/** An automated Player.
 *  @author Charles Lee
 */
class AI extends Player {

    /** A new player of GAME initially playing COLOR that chooses
     *  moves automatically.
     */
    AI(Game game, Color color) {
        super(game, color);
        _mutable = (MutableBoard) getGame().getMut();
        _move = new int[2];
    }
    @Override
    void makeMove() {
        Game game = getGame();
        _move = minmax(getColor(), _mutable, 4, Integer.MAX_VALUE);
        game.makeMove(_move[0]);
        game.message("%s moves %d %d. \n", getColor().toCapitalizedString(),
            _mutable.row(_move[0]), _mutable.col(_move[0]));
    }

    /** Return the minimum of CUTOFF and the minmax value of board B
     *  (which must be mutable) for player P to a search depth of D
     *  (where D == 0 denotes evaluating just the next move).
     *  If MOVES is not null and CUTOFF is not exceeded, set MOVES to
     *  a list of all highest-scoring moves for P; clear it if
     *  non-null and CUTOFF is exceeded. the contents of B are
     *  invariant over this call. */
    private int[] minmax(Color p, Board b, int d, int cutoff) {
        if (d == 0 || b.gameOver()) {
            return new int[] {-1, staticEval(p, b)};
        }
        int size = getGame().getBoard().size();
        int[] ab = {-1, -Integer.MAX_VALUE};
        for (int i = 0; i < size * size; i++) {
            if (b.isLegal(p, i)) {
                b.addSpot(p, i);
                int[] other = minmax(p.opposite(), b, d - 1,
                    -ab[1]);
                b.undo();
                if (-other[1] >= ab[1]) {
                    ab = new int[] {i, -other[1]};
                    if (ab[1] >= cutoff) {
                        break;
                    }
                }
            }
        }
        return ab;
    }

    /** Returns heuristic value of board B for player P.
     *  Higher is better for P. */
    private int staticEval(Color p, Board b) {
        int good = 0;
        int bad = 0;
        if (b.gameOver()) {
            if (b.getGrid()[0].getCol() == p) {
                return Integer.MAX_VALUE;
            } else {
                return -Integer.MAX_VALUE;
            }
        }
        for (Board.Square spaces : b.getGrid()) {
            if (spaces.getCol() == p) {
                good++;
            }
            if (spaces.getCol() == p.opposite()) {
                bad++;
            }
        }
        return good - bad;
    }
    /** MutableBoard for moves. */
    private MutableBoard _mutable;
    /** Int array to hold move. */
    private int[] _move;
}
