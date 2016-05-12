package trip;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

/** Initial class for the 'trip' program.
 *  @author Charles Lee
 */
public final class Main {

    /** Entry point for the CS61B trip program.  ARGS may contain options
     *  and targets:
     *      [ -m MAP ] [ -o OUT ] [ REQUEST ]
     *  where MAP (default Map) contains the map data, OUT (default standard
     *  output) takes the result, and REQUEST (default standard input) contains
     *  the locations along the requested trip.
     */
    public static void main(String... args) {
        String mapFileName;
        String outFileName;
        String requestFileName;

        mapFileName = "Map";
        outFileName = requestFileName = null;

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-m")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    mapFileName = args[a];
                }
            } else if (args[a].equals("-o")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    outFileName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        if (a == args.length - 1) {
            requestFileName = args[a];
        } else if (a > args.length) {
            usage();
        }

        if (requestFileName != null) {
            try {
                System.setIn(new FileInputStream(requestFileName));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s.%n", requestFileName);
                System.exit(1);
            }
        }

        if (outFileName != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(outFileName),
                                              true));
            } catch  (FileNotFoundException e) {
                System.err.printf("Could not open %s for writing.%n",
                                  outFileName);
                System.exit(1);
            }
        }

        trip(mapFileName);
    }

    /** Print a trip for the request on the standard input to the standard
     *  output, using the map data in MAPFILENAME.
     */
    private static void trip(String mapFileName) {
        Scanner sc;
        try {
            sc = new Scanner(new File(mapFileName));
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
            return;
        }
        ArrayList<Location> loc = new ArrayList<Location>();
        ArrayList<Distance> dist = new ArrayList<Distance>();
        try {
            int i = 1;
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                String[] words = s.trim().split("\\s+");
                if (words[0].equals("L")) {
                    loc.add(new Location(words[1]
                        , Integer.parseInt(words[2])
                        , Integer.parseInt(words[3])));
                } else if (words[0].equals("R")) {
                    for (Location l : loc) {
                        if (l._place.equals(words[1])) {
                            break;
                        } else {
                            System.err.printf("Error in line %d", i);
                        }
                    }
                    for (Location l : loc) {
                        if (l._place.equals(words[5])) {
                            break;
                        } else {
                            System.err.printf("Error in line %d", i);
                        }
                    }
                    dist.add(new Distance(words[1], words[2]
                        , Float.parseFloat(words[3]), words[4]
                            , words[5]));
                } else {
                    continue;
                }
                i++;
            }
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (IndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        sc.close();
    }
    /** Subclass representing location. */
    static class Location {
        /** Creates location, that has a PLACE and X and Y coordinates. */
        Location(String place, int x, int y) {
            _place = place;
            lat = x;
            lon = y;
        }
        /** String representing place of the location. */
        private String _place;
        /** Integer representing latitude X of place. */
        private int lat;
        /** Integer longitude Y of place. */
        private int lon;
    }
    /** Subclass representing distance between locations. */
    static class Distance {
        /** Distance Constructor. Starts at START, follows ROAD for DIST
         * going DIR direction and ends at END.
         * @param start
         * @param road
         * @param dist
         * @param dir
         * @param end
         */
        Distance(String start, String road, float dist
            , String dir, String end) {
            _start = start;
            _road = road;
            _dist = dist;
            _dir = dir;
            _end = end;
        }
        /** Represents start of the distance. */
        private String _start;
        /** The road distance represents. */
        private String _road;
        /** Numerical representation from start to end. */
        private float _dist;
        /** Direction of road. N E S W. */
        private String _dir;
        /** Endpoint of this distance.*/
        private String _end;
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.exit(1);
    }

}
