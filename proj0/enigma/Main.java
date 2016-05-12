package enigma;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

/** Enigma simulator.
 *  @author Charles Lee
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified in the input from the standard input.  Print the
     *  results on the standard output. Exits normally if there are
     *  no errors in the input; otherwise with code 1. */
    public static void main(String[] unused) {
        Machine M;
        BufferedReader input =
            new BufferedReader(new InputStreamReader(System.in));

        M = null;

        boolean configured = false;

        try {
            while (true) {
                String line = input.readLine();
                if (line == null) {
                    break;
                }
                if (isConfigurationLine(line)) {
                    M = new Machine();
                    configured = true;
                    configure(M, line);
                } else {
                    if (!configured) {
                        throw new IOException();
                    }
                    printMessageLine(M.convert(standardize(line)));
                }
            }
        } catch (IOException excp) {
            System.err.printf("Input error: %s%n", excp.getMessage());
            System.exit(1);
        }
    }

    /** Return true iff LINE is an Enigma configuration line. */
    private static boolean isConfigurationLine(String line) {
        String rotors = "I II III IV V VI VII VIII";
        String[] line2 = line.split(" ");
        if (line2.length != 7 || !line2[0].equals("*")) {
            return false;
        }
        String one = line2[3];
        String two = line2[4];
        String three = line2[5];
        int set = parsePosition(line2[6].charAt(0));
        int set2 = parsePosition(line2[6].charAt(1));
        int set3 = parsePosition(line2[6].charAt(2));
        int set4 = parsePosition(line2[6].charAt(3));

        if (!line2[1].equals("B") && !line2[1].equals("C")) {
            System.exit(1);
        }

        if (!line2[2].equals("BETA") && !line2[2].equals("GAMMA")) {
            System.exit(1);
        }

        one = line2[3];
        two = line2[4];
        three = line2[5];
        if (!rotors.contains(one) || !rotors.contains(two)
            || !rotors.contains(three) || rotors.equals(two)
            || rotors.equals(three) || rotors.equals(three)) {
            System.exit(1);
        }

        if (line2[6].length() != 4) {
            System.exit(1);
        }
        if (set == -1 || set2 == -1
                || set3 == -1 || set4 == -1) {
            System.exit(1);
        }

        return true;
    }

    /** Returns parsed position (int) of CH. If there's an error, returns -1. */
    static int parsePosition(char ch) {
        int x = ch - 'A';
        if (x < Rotor.ALPHABET_SIZE && x >= 0) {
            return x;
        } else {
            return -1;
        }
    }

        /** String rotors = "I II III IV V VI VII VIII";
        String[] line2 = line.split("\\s+");
        String[] four = line2[6].split("");
        if (line2.length > 7) {
            return false;
        }
        if (line2[0].equals("*")) {
            if (line2[1].equals("B") || line2[1].equals("C")) {
                if (line2[2].equals("DELTA") || line2[2].equals("GAMMA")) {
                    if (rotors.contains(line2[3])) {
                        if (rotors.contains(line2[4])
                            && !line2[3].equals(line2[4])) {
                            if (rotors.contains(line2[5])
                                && !line2[5].equals(line2[4])
                                && !line2[3].equals(line2[5])) {
                                if (four.length == 5) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    } */


    /** Configure M according to the specification given on CONFIG,
     *  which must have the format specified in the assignment. */
    private static void configure(Machine M, String config) {
        String[] line = config.split("\\s+");
        if (line.length != 7) {
            System.exit(1);
        }

        Reflector ref = new Reflector(line[1], 0);
        FixedRotor fix = new FixedRotor(line[2], (Rotor.toIndex(
            line[6].charAt(0))));
        Rotor three = new Rotor(line[3], Rotor.toIndex(line[6].charAt(1)));
        Rotor four = new Rotor(line[4], Rotor.toIndex(line[6].charAt(2)));
        Rotor five = new Rotor(line[5], Rotor.toIndex(line[6].charAt(3)));
        Rotor[] rotors = {ref, fix, three, four, five};
        M.replaceRotors(rotors);

    }

    /** Return the result of converting LINE to all upper case,
     *  removing all blanks and tabs.  It is an error if LINE contains
     *  characters other than letters and blanks. */
    private static String standardize(String line) {
        String delim = "[]+";
        String capital = line.toUpperCase();
        String line2 = capital.replaceAll("\\W", "");
        return line2;

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private static void printMessageLine(String msg) {
        int count = 0;
        for (int i = 0; i < msg.length(); i++) {
            System.out.print(msg.charAt(i));
            count++;
            if (count % 5 == 0 && i != msg.length() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }
}

