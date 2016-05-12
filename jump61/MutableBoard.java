package jump61;


import static jump61.Color.*;

/** A Jump61 board state.
 *  @author Charles Lee
 */
class MutableBoard extends Board {

    /** An N x N board in initial configuration. */
    MutableBoard(int N) {
        _N = N;
        _moves = 1;
        _grid = new Square[_N * _N];
        for (int i = 0; i < _grid.length; i++) {
            _grid[i] = new Square();
        }
    }
    /** A board whose initial contents are copied from BOARD0. Clears the
     *  undo history. */
    MutableBoard(Board board0) {
        copy(board0);
    }

    @Override
    void clear(int N) {
        _N = N;
        _grid = new Square[_N * _N];
        _lastBoard = null;
        _moves = 1;
        for (int i = 0; i < _grid.length; i++) {
            _grid[i] = new Square();
        }
    }

    @Override
    void copy(Board board) {
        _N = board.size();
        _moves = board.numMoves();
        _grid = new Square[board.size() * board.size()];
        if (board._grid != null) {
            for (int i = 0; i < _grid.length; i++) {
                _grid[i] = board._grid[i].cop();
            }
        }
        _lastBoard = board.getLast();
    }

    @Override
    int size() {
        return _N;
    }

    @Override
    int spots(int r, int c) {
        int n = sqNum(r, c);
        return _grid[n].getSpots();
    }

    @Override
    int spots(int n) {
        return _grid[n].getSpots();
    }

    @Override
    Color color(int r, int c) {
        int n = sqNum(r, c);
        return _grid[n].getCol();
    }

    @Override
    Color color(int n) {
        return _grid[n].getCol();
    }

    @Override
    int numMoves() {
        return _moves;
    }

    @Override
    int numOfColor(Color color) {
        int counter = 0;
        for (Square elem : _grid) {
            if (elem.getCol().equals(color)) {
                counter++;
            }
        }
        return counter;
    }

    @Override
    void addSpot(Color player, int r, int c) {
        _lastBoard = new MutableBoard(this);
        _moves++;
        int n = sqNum(r, c);
        _grid[n].setCol(player);
        _grid[n].addS();
        jump(n);
    }

    @Override
    void addSpot(Color player, int n) {
        _lastBoard = new MutableBoard(this);
        _moves++;
        _grid[n].setCol(player);
        _grid[n].addS();
        jump(n);
    }
    /** Adds spots during jumps. Takes PLAYER and N. */
    void addJumpSpot(Color player, int n) {
        _grid[n].setCol(player);
        _grid[n].addS();
    }
    @Override
    void set(int r, int c, int num, Color player) {
        int n = sqNum(r, c);
        _lastBoard = null;
        _grid[n].setCol(player);
        _grid[n].setSpots(num);
    }

    @Override
    void set(int n, int num, Color player) {
        _lastBoard = null;
        _grid[n].setCol(player);
        _grid[n].setSpots(num);
    }

    @Override
    void setMoves(int num) {
        assert num > 0;
        _moves = num;
        _lastBoard = null;
    }

    @Override
    void undo() {
        copy(_lastBoard);
    }
    /** Do all jumping on this board, assuming that initially, S is the only
     *  square that might be over-full. */
    private void jump(int S) {
        int k = 0;
        int[] neighbors = { S - 1, S + 1, S - size(), S + size()};
        if (gameOver() || !(_grid[S].getSpots() > neighbors(S))) {
            return;
        }
        while (k < 4) {
            try {
                addJumpSpot(_grid[S].getCol(), neighbors[k]);
                if (_grid[S].getSpots() > 1) {
                    _grid[S].minusS();
                }
                k++;
            } catch (ArrayIndexOutOfBoundsException e) {
                k++;
            }
        }
        for (k = 0; k < 4; k++) {
            try {
                jump(neighbors[k]);
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }
    }

    /** Total combined number of moves by both sides. */
    protected int _moves;
    /** Convenience variable: size of board (squares along one edge). */
    private int _N;
}
