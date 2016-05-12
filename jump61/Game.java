package jump61;

import java.io.Reader;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.Scanner;
import java.util.Random;

import static jump61.Color.*;

/** Main logic for playing (a) game(s) of Jump61.
 *  @author Charles Lee
 */
class Game {

    /** Name of resource containing help message. */
    private static final String HELP = "jump61/Help.txt";

    /** A new Game that takes command/move input from INPUT, prints
     *  normal output on OUTPUT, prints prompts for input on PROMPTS,
     *  and prints error messages on ERROROUTPUT. The Game now "owns"
     *  INPUT, PROMPTS, OUTPUT, and ERROROUTPUT, and is responsible for
     *  closing them when its play method returns. */
    Game(Reader input, Writer prompts, Writer output, Writer errorOutput) {
        _board = new MutableBoard(Defaults.BOARD_SIZE);
        _readonlyBoard = new ConstantBoard(_board);
        _prompter = new PrintWriter(prompts, true);
        _inp = new Scanner(input);
        _inp.useDelimiter("(?m)$|^|\\p{Blank}+");
        _out = new PrintWriter(output, true);
        _err = new PrintWriter(errorOutput, true);
        _player1 = new HumanPlayer(this, RED);
        _player2 = new AI(this, BLUE);
    }

    /** Returns a readonfly view of the game board.  This board remains valid
     *  throughout the session. */
    Board getBoard() {
        return _readonlyBoard;
    }

    /** Play a session of Jump61.  This may include multiple games,
     *  and proceeds until the user exits.  Returns an exit code: 0 is
     *  normal; any positive quantity indicates an error.  */
    int play() {
        _out.println("Welcome to " + Defaults.VERSION);
        while (!_playing) {
            promptForNext();
            readExecuteCommand();
        }
        restartGame();
        _out.flush();
        return 0;
    }

    /** Get a move from my input and place its row and column in
     *  MOVE.  Returns true if this is successful, false if game stops
     *  or ends first. */
    boolean getMove(int[] move) {
        while (_playing && _move[0] == 0 && promptForNext()) {
            readExecuteCommand();
        }
        if (_move[0] > 0) {
            move[0] = _move[0];
            move[1] = _move[1];
            _move[0] = 0;
            return true;
        } else {
            return false;
        }
    }

    /** Add a spot to R C, if legal to do so. */
    void makeMove(int r, int c) {
        if (_board.isLegal(_board.whoseMove(), r, c)) {
            _board.addSpot(_board.whoseMove(), r, c);
        }
    }

    /** Add a spot to square #N, if legal to do so. */
    void makeMove(int n) {
        if (_board.isLegal(_board.whoseMove(), n)) {
            _board.addSpot(_board.whoseMove(), n);
        }
    }

    /** Return a random integer in the range [0 .. N), uniformly
     *  distributed.  Requires N > 0. */
    int randInt(int n) {
        return _random.nextInt(n);
    }

    /** Send a message to the user as determined by FORMAT and ARGS, which
     *  are interpreted as for String.format or PrintWriter.printf. */
    void message(String format, Object... args) {
        _out.printf(format, args);
    }

    /** Check whether we are playing and there is an unannounced winner.
     *  If so, announce and stop play. */
    private void checkForWin() {
        if (_board.gameOver()) {
            announceWinner();
            _playing = false;
        }
    }

    /** Send announcement of winner to my user output. */
    private void announceWinner() {
        _out.write(_board.getWinner().toCapitalizedString() + " wins.\n");
    }

    /** Make player from STRINGS an AI for subsequent moves. */
    private void setAuto(String[] strings) {
        _playing = false;
        Color col = WHITE;
        try {
            col = Color.parseColor(strings[1]);
        } catch (IllegalArgumentException e) {
            reportError("Incorrect input; should be red or blue.");
            return;
        }
        if (col == RED) {
            _player1 = new AI(this, RED);
        }
        if (col == BLUE) {
            _player2 = new AI(this, BLUE);
        }
    }

    /** Make player from STRINGS take manual input. */
    private void setManual(String[] strings) {
        _playing = false;
        Color col = WHITE;
        try {
            col = Color.parseColor(strings[1]);
        } catch (IllegalArgumentException e) {
            reportError("Incorrect input; should be red or blue.");
            return;
        }
        if (col == RED) {
            _player1 = new HumanPlayer(this, RED);
        }
        if (col == BLUE) {
            _player2 = new HumanPlayer(this, BLUE);
        }
    }
    /** Stop any current game and clear the board to its initial
     *  state. */
    private void clear() {
        _playing = false;
        _board.clear(_board.size());
    }

    /** Print the current board using standard board-dump format. */
    private void dump() {
        _out.println(_board + "\n");
    }

    /** Print a help message. */
    private void help() {
        Main.printHelpResource(HELP, _out);
    }

    /** Stop any current game and set the move number to N. */
    private void setMoveNumber(int n) {
        _playing = false;
        _board.setMoves(n);
    }

    /** Seed the random-number generator with SEED. */
    private void setSeed(long seed) {
        _random.setSeed(seed);
    }

    /** Place SPOTS spots on square R:C and color the square red or
     *  blue depending on whether COLOR is "r" or "b".  If SPOTS is
     *  0, clears the square, ignoring COLOR.  SPOTS must be less than
     *  the number of neighbors of square R, C. */
    private void setSpots(int r, int c, int spots,
        String color) throws GameException {
        int n = _board.sqNum(r, c);
        color = color.toUpperCase();
        Color col = WHITE;
        if (spots > _board.neighbors(n)) {
            reportError("Can't set more spots on a space than it has neighbors."
                + "Space %d has %d spots and %d neighbors", n, _board.spots(n),
                _board.neighbors(n));
            return;
        }
        if (spots == 0) {
            _board.set(r, c, 0, WHITE);
        } else {
            if (color.equals("RED") ||  color.equals("R")) {
                col = RED;
            }
            if (color.equals("BLUE") || color.equals("B")) {
                col = BLUE;
            }
            _board.set(r,  c, spots, col);
        }
    }
    /** setSpots for input from STRINGS that must be parsed first. */
    private void setSpots(String[] strings) {
        if (strings.length != 5) {
            reportError("Incorrect number or format of inputs.");
            return;
        }
        try {
            setSpots(Integer.parseInt(strings[1]),
                Integer.parseInt(strings[2]),
                Integer.parseInt(strings[3]), strings[4]);
        } catch (NumberFormatException e) {
            reportError("Incorrect format of parameters.");
        }
    }

    /** Stop any current game and set the board to an empty N x N board
     *  with numMoves() == 0.  */
    private void setSize(int n) {
        _playing = false;
        _board.clear(n);
    }

    /** Begin accepting moves for game.  If the game is won,
     *  immediately print a win message and end the game. */
    private void restartGame() {
        _playing = true;
        while (_playing) {
            checkForWin();
            _player1.makeMove();
            checkForWin();
            if (!_playing) {
                break;
            }
            _player2.makeMove();
            checkForWin();
        }
    }

    /** Save move R C in _move.  Error if R and C do not indicate an
     *  existing square on the current board. */
    private void saveMove(int r, int c) {
        if (!_board.exists(r, c)) {
            reportError("move %d %d out of bounds", r, c);
            return;
        }
        _move[0] = r;
        _move[1] = c;
    }

    /** Read and execute one command.  Leave the input at the start of
     *  a line, if there is more input. */
    private void readExecuteCommand() {
        if (_inp.hasNext()) {
            executeCommand(_inp.nextLine());
        } else {
            System.exit(0);
        }
    }

    /** Gather arguments and execute command CMND.  Throws GameException
     *  on errors. */
    private void executeCommand(String cmnd) throws GameException {
        String[] strings = cmnd.split(" ");
        if (cmnd.matches("\\d+\\s\\d+") && _playing) {
            saveMove(Integer.parseInt(strings[0]),
                Integer.parseInt(strings[1]));
            return;
        }
        switch (strings[0]) {
        case "\n": case "\r\n": case "":
            return;
        case "#":
            break;
        case "clear":
            clear();
            break;
        case "start":
            restartGame();
            break;
        case "quit":
            _out.flush(); _err.flush();
            System.exit(0);
            break;
        case "auto":
            setAuto(strings);
            break;
        case "manual":
            setManual(strings);
            break;
        case "size":
            try {
                setSize(Integer.parseInt(strings[1]));
            } catch (NumberFormatException e) {
                reportError("Erroneous input.");
            }
            break;
        case "move":
            try {
                setMoveNumber(Integer.parseInt(strings[1]));
            } catch (NumberFormatException e) {
                reportError("Erroneous input.");
            }
            break;
        case "set":
            setSpots(strings);
            break;
        case "dump":
            dump();
            break;
        case "seed":
            try {
                setSeed(Integer.parseInt(strings[1]));
            } catch (NumberFormatException e) { reportError("Wrong input."); }
            break;
        case "help":
            help();
            break;
        default:
            reportError("Bad command: '%s'", cmnd);
        }
    }

    /** Print a prompt and wait for input. Returns true iff there is another
     *  token. */
    private boolean promptForNext() {
        if (_playing) {
            _prompter.printf("%s>", _board.whoseMove().toString());
        } else {
            _prompter.printf(">");
        }
        return true;
    }

    /** Send an error message to the user formed from arguments FORMAT
     *  and ARGS, whose meanings are as for printf. */
    void reportError(String format, Object... args) {
        _err.print("Error: ");
        _err.printf(format, args);
        _err.println();
    }

    /** Writer on which to print prompts for input. */
    private final PrintWriter _prompter;
    /** Scanner from current game input.  Initialized to return
     *  newlines as tokens. */
    private final Scanner _inp;
    /** Outlet for responses to the user. */
    private final PrintWriter _out;
    /** Outlet for error responses to the user. */
    private final PrintWriter _err;

    /** The board on which I record all moves. */
    private final Board _board;
    /** A readonly view of _board. */
    private final Board _readonlyBoard;

    /** A pseudo-random number generator used by players as needed. */
    private final Random _random = new Random();

    /** True iff a game is currently in progress. */
    private boolean _playing;
    /** Player 1. */
    private Player _player1;
    /** Player2. */
    private Player _player2;
    /** Returns the Mutable board. */
    Board getMut() {
        return _board;
    }
   /** Used to return a move entered from the console.  Allocated
     *  here to avoid allocations. */
    private final int[] _move = new int[2];
}
