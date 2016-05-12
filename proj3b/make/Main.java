package make;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;

import graph.DirectedGraph;
import graph.Graph;
import graph.RejectException;
import graph.StopException;
import graph.Traversal;

import org.junit.Rule;

/** Initial class for the 'make' program.
 *  @author Charles Lee
 */
public final class Main {

    /** Entry point for the CS61B make program.  ARGS may contain options
     *  and targets:
     *      [ -f MAKEFILE ] [ -D FILEINFO ] TARGET1 TARGET2 ...
     * @throws FileNotFoundException
     */
    public static void main(String... args) throws FileNotFoundException {
        String makefileName;
        String fileInfoName;
        if (args.length == 0) {
            usage();
        }
        makefileName = "Makefile";
        fileInfoName = "fileinfo";

        int a;
        for (a = 0; a < args.length; a += 1) {
            if (args[a].equals("-f")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    makefileName = args[a];
                }
            } else if (args[a].equals("-D")) {
                a += 1;
                if (a == args.length) {
                    usage();
                } else {
                    fileInfoName = args[a];
                }
            } else if (args[a].startsWith("-")) {
                usage();
            } else {
                break;
            }
        }

        ArrayList<String> targets = new ArrayList<String>();

        for (; a < args.length; a += 1) {
            targets.add(args[a]);
        }

        make(makefileName, fileInfoName, targets);
    }

    /** Carry out the make procedure using MAKEFILENAME as the makefile,
     *  taking information on the current file-system state from FILEINFONAME,
     *  and building TARGS, or the first target in the makefile if TARGETS
     *  is empty.
     */
    private static void make(String makefileName, String fileInfoName,
        List<String> targs) throws FileNotFoundException {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        Scanner file, make;
        try {
            file = new Scanner(new File(fileInfoName));
            make = new Scanner(new File(makefileName));
        } catch (IOException e) {
            usage();
            return;
        }
        int time = Integer.parseInt(file.next());
        file.nextLine();
        ArrayList<Rule> rlist = new ArrayList<Rule>();
        int i = 1;
        while (file.hasNext()) {
            String[] words = file.nextLine().split("\\s+");
            try {
                map.put(words[0], Integer.parseInt(words[1]));
            } catch (IllegalArgumentException e) {
                System.err.printf("Error at %d", i);
                System.exit(1);
            }
            i++;
        }
        file.close();
        i = 1;
        Rule rule = null;
        while (make.hasNextLine()) {
            String s = make.nextLine();
            String[] words;
            if (s.matches("\\s*\r*\n*") || s.startsWith("#")) {
                continue;
            } else if (s.matches("\\s+.+")) {
                rule.addArg(s);
            } else {
                if (rule != null) {
                    rlist.add(new Rule(rule));
                    rule = null;
                }
                words = s.split("\\s+");
                if (words[0].matches(".*[:=\\#].*:+")) {
                    System.out.printf("Error at %d", i);
                    System.exit(1);
                }
                if (words.length > 0) {
                    rule = new Rule(words[0].substring(0
                        , words[0].length() - 1));
                    for (int n = 1; n < words.length; n++) {
                        rule.addDep(words[n]);
                    }
                }
            }
            i++;
        }
        if (rule != null) {
            rlist.add(new Rule(rule));
        }
        make.close();
        cmndpro(rlist, map, targs, time);
    }
    /** Make TARGETS from RLIST at TIME mapping Names in HASH to CHANGEDATES. */
    private static void cmndpro(ArrayList<Rule> rlist
        , final HashMap<String, Integer> hash, final List<String> targets
        , final int time) {
        final Graph<Rule, String> make = new DirectedGraph<Rule, String>();
        final ArrayList<Rule> rulist = cancel(rlist);
        final Hashtable<Rule, Graph<Rule, String>.Vertex> vertices =
            new Hashtable<Rule, Graph<Rule, String>.Vertex>();
        final HashSet<Graph<Rule, String>.Vertex> marked =
            new HashSet<Graph<Rule, String>.Vertex>();
        Traversal<Rule, String> trav = new Traversal<Rule, String>() {
            protected void preVisit(Graph<Rule, String>.Edge edg,
            Graph<Rule, String>.Vertex v0) {
            }
            protected void visit(Graph<Rule, String>.Vertex ver) {
                if (!older(hash, ver.getLabel(), time)) {
                    throw new RejectException();
                }
                marked.add(ver);
                Rule rule = ver.getLabel();
                for (String p : rule._deplist) {
                    Rule prereq = hasTarget(rulist, p);
                    for (Graph<Rule, String>.Vertex vert
                        : make.successors(ver)) {
                        if (vert.getLabel() == prereq) {
                            throw new StopException();
                        }
                    }
                    vertices.put(prereq,
                            make.add(prereq));
                    make.add(vertices.get(rule), vertices.get(prereq));
                }
            }
            protected void postVisit(Graph<Rule, String>.Vertex v) {
                for (String elem : v.getLabel()._arglist) {
                    System.out.println(elem);
                }
            }
        };
        for (String target : targets) {
            vertices.put(hasTarget(rlist, target)
                , make.add(hasTarget(rlist, target)));
        }
        if (targets.isEmpty()) {
            targets.add(rlist.get(0)._target);
        }
        try {
            for (String target : targets) {
                Rule rule = hasTarget(rlist, target);
                Graph<Rule, String>.Vertex vertex = vertices.get(rule);
                if (vertex == null) {
                    continue;
                }
                trav.depthFirstTraverse(make, vertex);
            }
        } catch (StopException e) {
            System.err.println("Error: infinite cycle.");
            System.exit(1);
        }
    }
    /** Returns true if target RULE in HASH is older than Dep by TIME. */
    private static boolean older(HashMap<String, Integer> hash
        , Rule rule, int time) {
        String tar = rule._target;
        if (hash.containsKey(tar)) {
            for (String prereq : rule._deplist) {
                if (hash.containsKey(prereq)) {
                    if (hash.get(prereq) > hash.get(tar)) {
                        hash.put(tar, time + 1);
                        return true;
                    }
                } else if (!hash.containsKey(prereq)) {
                    return true;
                }
            }
            return false;
        } else {
            hash.put(tar, time + 1);
            return true;
        }
    }
    /** Returns rule if RLIST contains rule with TARGET. */
    private static Rule hasTarget(ArrayList<Rule> rlist, String target) {
        for (Rule r : rlist) {
            if (r._target.equals(target)) {
                return r;
            }
        }
        return new Rule(target);
    }
    /** Cancels out one of a duplicate Rule in RLIST.
     * Returns finished RLIST. */
    private static ArrayList<Rule> cancel(ArrayList<Rule> rlist) {
        for (Rule rule : rlist) {
            for (Rule dup : rlist) {
                if (rule._target.equals(dup._target) && rule != dup) {
                    if (rule._arglist.size() != 0) {
                        if (dup._arglist.size() != 0) {
                            System.err.printf("Error: More than one command"
                                    + "set for target %s.", dup._target);
                            System.exit(1);
                        }
                    }
                    if (dup._arglist.size() != 0) {
                        if (rule._arglist.size() != 0) {
                            System.err.printf("Error: More than one command"
                                    + "set for target %s.", dup._target);
                            System.exit(1);
                        }
                    }
                    rule._deplist.addAll(dup._deplist);
                    rlist.remove(dup);
                }
            }
        }
        return rlist;
    }

    /** Print a brief usage message and exit program abnormally. */
    private static void usage() {
        System.out.println("Error: Incorrect usage.");
        System.exit(1);
    }
    /** Subclass rule. Has a target, list of dependencies and commands. */
    static class Rule {
        /** Constructs Rule. TARGET is the target, DEPLIST is a list of
         * dependencies, ARGLIST is a list of args.
         */
        Rule(String target) {
            _target = target;
            _deplist = new ArrayList<String>();
            _arglist = new ArrayList<String>();
        }
        /** Makes a new rule from CLONE. */
        Rule(Rule clone) {
            _target = clone._target;
            _deplist = clone._deplist;
            _arglist = clone._arglist;
        }
        /** Add DEP to deplist. */
        void addDep(String dep) {
            _deplist.add(dep);
        }
        /** Add ARG to arglist. */
        void addArg(String arg) {
            _arglist.add(arg);
        }
        /** Target of rule. */
        private String _target;
        /** ArrayList of Dependencies. */
        private ArrayList<String> _deplist;
        /** ArrayList of Arguments. */
        private ArrayList<String> _arglist;
    }

}
