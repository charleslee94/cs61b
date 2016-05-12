package enigma;

/** Class that represents a reflector in the enigma.
 *  @author Charles Lee
 */
class Reflector extends Rotor {

    /** Reflector constructor. Takes String PARAM
    and int SETTING. */
    public Reflector(String param, int setting) {
        super(param, setting);
    }

    @Override
    boolean atNotch() {
        return false;
    }


    @Override
    boolean hasInverse() {
        return false;
    }

    /** Returns a useless value; should never be called. */
    @Override
    int convertBackward(int unused) {
        throw new UnsupportedOperationException();
    }

}
