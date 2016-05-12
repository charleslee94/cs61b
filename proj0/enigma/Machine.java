package enigma;

/** Class that represents a complete enigma machine.
 *  @author Charles Lee
 */
class Machine {

    /** Rotor list from 1 (Reflector) to 5 from left to right. */
    private Rotor[] rotorlist = new Rotor[5];

    /** Returns rotorlist rotors. */
    Rotor[] getRotors() {
        return rotorlist;
    }

    /** Set my rotors to (from left to right) ROTORS.  Initially, the rotor
     *  settings are all 'A'. */
    void replaceRotors(Rotor[] rotors) {
        rotorlist[0] = rotors[0];
        rotorlist[1] = rotors[1];
        rotorlist[2] = rotors[2];
        rotorlist[3] = rotors[3];
        rotorlist[4] = rotors[4];
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        int count = 0;
        int set;
        char[] result = new char[msg.length()];
        while (count < msg.length()) {
            boolean[] notches = new boolean[4];
            notches[3] = true;
            int a = 1;
            if (rotorlist[4].atNotch()) {
                notches[2] = true;
            }
            if (rotorlist[3].atNotch()) {
                notches[1] = notches[2] = true;
            }
            while (a <= 3) {
                if (notches[a]) {
                    rotorlist[a + 1].advance();
                }
                a++;
            }
            set = rotorlist[4].convertForward(
                    Rotor.toIndex(msg.charAt(count)));
            set = rotorlist[3].convertForward(set);
            set = rotorlist[2].convertForward(set);
            set = rotorlist[1].convertForward(set);
            set = rotorlist[0].convertForward(set);
            set = rotorlist[1].convertBackward(set);
            set = rotorlist[2].convertBackward(set);
            set = rotorlist[3].convertBackward(set);
            set = rotorlist[4].convertBackward(set);
            result[count] = Rotor.toLetter(set);
            count++;
        }
        return new String(result);
    }
}
