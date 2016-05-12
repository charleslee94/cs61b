package jump61;


/** A Player that gets its moves from manual input.
 *  @author Charles Lee
 */
class HumanPlayer extends Player {

    /** A new player initially playing COLOR taking manual input of
     *  moves from GAME's input source. */
    HumanPlayer(Game game, Color color) {
        super(game, color);
        move = new int[2];
    }

    @Override
    void makeMove() {
        Game game = getGame();
        Board board = getBoard();
        if (!game.getMove(move)) {
            return;
        }
        if (board.isLegal(getColor(), move[0], move[1])) {
            game.makeMove(move[0], move[1]);
            return;
        }
        game.reportError("Illegal move.");
        makeMove();
    }
    /** length 2 int array that holds moves. */
    private int[] move;
}
