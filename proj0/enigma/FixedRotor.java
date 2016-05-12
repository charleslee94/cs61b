package enigma;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author Charles Lee
 */
class FixedRotor extends Rotor {

    /** Takes String PARAM and int SETTING. */
    public FixedRotor(String param, int setting) {
        super(param, setting);
    }

    @Override
    boolean advances() {
        return false;
    }

    @Override
    boolean atNotch() {
        return false;
    }

    /** Fixed rotors do not advance. */
    @Override
    void advance() {
    }

}
