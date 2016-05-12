package enigma;

/** Class that represents a rotor in the enigma machine.
 *  @author Charles Lee
 */
class Rotor {

    /** Size of alphabet used for plaintext and ciphertext. */
    static final int ALPHABET_SIZE = 26;

    /** Rotor constructor. takes String PARAM and int SETTING. */
    public Rotor(String param, int setting) {
        name = param;
        _setting = setting;
    }
    /** Instance variable name. */
    private String name;

    /** Return name. */
    String getName() {
        return name;
    }

    /** int used for magic numbers. */
    static final int ANTI_MAGIC = 11;

    /** Parses String A and returns an int. */
    static int parseName(String a) {
        if (a.equals("I")) {
            return 0;
        }
        if (a.equals("II")) {
            return 1;
        }
        if (a.equals("III")) {
            return 2;
        }
        if (a.equals("IV")) {
            return 3;
        }
        if (a.equals("V")) {
            return 4;
        }
        if (a.equals("VI")) {
            return 5;
        }
        if (a.equals("VII")) {
            return 6;
        }
        if (a.equals("VIII")) {
            return 7;
        }
        if (a.equals("BETA")) {
            return 8;
        }
        if (a.equals("GAMMA")) {
            return 9;
        }
        if (a.equals("B")) {
            return 10;
        }
        if (a.equals("C")) {
            return ANTI_MAGIC;
        }
        return -1;
    }

    /** My current setting (index 0..25, with 0 indicating that 'A'
     *  is showing). */
    private int _setting;

    /** Return my current rotational setting as an integer between 0
     *  and 25 (corresponding to letters 'A' to 'Z').  */
    int getSetting() {
        return _setting;
    }

    /** Returns the mod, A % B. */
    public static int mod(int a, int b) {
        int x = a % b;
        if (x < 0) {
            x += b;
        }
        return x;
    }

    /** Assuming that P is an integer in the range 0..25, returns the
     *  corresponding upper-case letter in the range A..Z. */
    static char toLetter(int p) {
        return (char) ('A' + p);

    }

    /** Assuming that C is an upper-case letter in the range A-Z, return the
     *  corresponding index in the range 0..25. Inverse of toLetter. */
    static int toIndex(char c) {
        return c - 'A';
    }

    /** Returns true iff this rotor has a ratchet and can advance. */
    boolean advances() {
        return true;
    }

    /** Returns true iff this rotor has a left-to-right inverse. */
    boolean hasInverse() {
        return true;
    }

    /** Set getSetting() to POSN.  */
    void set(int posn) {
        assert 0 <= posn && posn < ALPHABET_SIZE;
        _setting = posn;
    }

    /** Return the conversion of P (an integer in the range 0..25)
     *  according to my permutation. */
    int convertForward(int p) {
        return mod(PermutationData.ROTOR_SPECS[parseName(name)][1].charAt(
            (p + _setting) % ALPHABET_SIZE) - _setting - 'A', ALPHABET_SIZE);
    }

    /** Return the conversion of E (an integer in the range 0..25)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        return mod(PermutationData.ROTOR_SPECS[parseName(name)][2].charAt(
            (e + _setting) % ALPHABET_SIZE) - _setting - 'A', ALPHABET_SIZE);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        if (parseName(name) <= 4) {
            return PermutationData.ROTOR_SPECS[parseName(name)][3].charAt(0)
                == (char) ('A' + _setting);
        } else if (parseName(name) <= 7) {
            return ((char) ('A' + _setting)) == 'Z'
                || ((char) ('A' + _setting)) == 'M';
        }
        return false;
    }

    /** Advance me one position. */
    void advance() {
        this._setting = (this._setting + 1) % ALPHABET_SIZE;
    }
}
