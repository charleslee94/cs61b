package graph;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/** Assorted graph algorithms.
 *  @author Charles Lee
 */

public final class Graphs {

    /* A* Search Algorithms */

    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the edge weighter EWEIGHTER.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, uses VWEIGHTER to set the weight of vertex v
     *  to the weight of a minimal path from V0 to v, for each v in
     *  the returned path and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *              < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.  If V1 is
     *  unreachable from V0, returns null and sets the minimum path weights of
     *  all reachable nodes.  The distance to a node unreachable from V0 is
     *  Double.POSITIVE_INFINITY. */
    public static <VLabel, ELabel> List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 Graph<VLabel, ELabel>.Vertex V1,
                 Distancer<? super VLabel> h,
                 Weighter<? super VLabel> vweighter,
                 Weighting<? super ELabel> eweighter) {
        Comparator<Graph<VLabel, ELabel>.Vertex> comV = compV(V1, h, vweighter);
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> ope =
            new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(G.vertexSize(),
                comV);
        TreeSet<Graph<VLabel, ELabel>.Vertex> closed =
            new TreeSet<Graph<VLabel, ELabel>.Vertex>();
        HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>.Edge> from =
            new HashMap<Graph<VLabel, ELabel>.Vertex, Graph<VLabel, ELabel>
        .Edge>();
        Iteration<Graph<VLabel, ELabel>.Vertex> vertices = G.vertices();
        while (vertices.hasNext()) {
            Graph<VLabel, ELabel>.Vertex node = vertices.next();
            if (node == V0) {
                vweighter.setWeight(node.getLabel(), 0.0);
            } else {
                vweighter.setWeight(node.getLabel(), Double.POSITIVE_INFINITY);
            }
        }
        ope.add(V0);
        while (!ope.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex currentv = ope.poll();
            if (currentv.equals(V1)) {
                return reconstruct(from, V0, currentv);
            }
            Iteration<Graph<VLabel, ELabel>.Edge> edges = G.outEdges(currentv);
            while (edges.hasNext()) {
                Graph<VLabel, ELabel>.Edge e = edges.next();
                Graph<VLabel, ELabel>.Vertex suc = e.getV(currentv);
                double gscore = vweighter.weight(currentv.getLabel())
                    + h.dist(currentv.getLabel(), suc.getLabel());
                double fscore = gscore + h.dist(suc.getLabel(),
                    V1.getLabel());
                if (closed.contains(suc) && fscore >= fscore(suc, V1, h,
                    vweighter)) {
                    continue;
                }
                if ((!ope.contains(suc) || fscore >= fscore(suc, V1, h,
                    vweighter))) {
                    from.put(suc,  e);
                }
                vweighter.setWeight(suc.getLabel(), gscore);
                if (!ope.contains(suc)) {
                    ope.add(suc);
                }
            }
        }
        return null;
    }

    /** Function that returns an ARRAYLIST RESULT that is a list of edges that
     * uses HASH, CURRENT and START. Compares VLABEL ELABEL. */
    private static <VLabel, ELabel> ArrayList<Graph<VLabel, ELabel>.Edge>
    reconstruct(HashMap<Graph<VLabel, ELabel>.Vertex
        , Graph<VLabel, ELabel>.Edge> hash
        , Graph<VLabel, ELabel>.Vertex current
        , Graph<VLabel, ELabel>.Vertex start) {
        ArrayList<Graph<VLabel, ELabel>.Edge> result =
            new ArrayList<Graph<VLabel, ELabel>.Edge>();
        if (hash.containsKey(current)) {
            result = reconstruct(hash, start, hash.get(current).getV(current));
            result.add(hash.get(current));
            return result;
        } else {
            return result;
        }
    }
    /** Compares using H, VWEIGHTER to compare  VLABEL and ELABEL and GOAL.
     * Returns COMPV*/
    private static <VLabel, ELabel> Comparator<Graph<VLabel, ELabel>.Vertex>
    compV(final Graph<VLabel, ELabel>.Vertex goal
        , final Distancer<? super VLabel> h
        , final Weighter<? super VLabel> vweighter) {
        Comparator<Graph<VLabel, ELabel>.Vertex> comp =
            new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex v0,
                    Graph<VLabel, ELabel>.Vertex v1) {
                    double f0 = fscore(v0, goal, h, vweighter);
                    double f1 = fscore(v1, goal, h, vweighter);
                    return Double.compare(f0, f1);
                }
            };
        return comp;
    }
    /** Helper function that returns the F_SCORE by comparing
     *  CURRENT and GOAL using H and VWEIGHTER.
     *  Compares VLABEL and ELABEL. */
    private static <VLabel, ELabel> double fscore(
        Graph<VLabel, ELabel>.Vertex current
        , Graph<VLabel, ELabel>.Vertex goal
        , Distancer<? super VLabel> h
        , Weighter<? super VLabel> vweighter) {
        double curW = vweighter.weight(current.getLabel());
        double heur = h.dist(current.getLabel(), goal.getLabel());
        return curW + heur;
    }
    /** Returns a path from V0 to V1 in G of minimum weight, according
     *  to the weights of its edge labels.  VLABEL and ELABEL are the types of
     *  vertex and edge labels.  Assumes that H is a distance measure
     *  between vertices satisfying the two properties:
     *     a. H.dist(v, V1) <= shortest path from v to V1 for any v, and
     *     b. H.dist(v, w) <= H.dist(w, V1) + weight of edge (v, w), where
     *        v and w are any vertices in G.
     *
     *  As a side effect, sets the weight of vertex v to the weight of
     *  a minimal path from V0 to v, for each v in the returned path
     *  and for each v such that
     *       minimum path length from V0 to v + H.dist(v, V1)
     *           < minimum path length from V0 to V1.
     *  The final weights of other vertices are not defined.
     *
     *  This function has the same effect as the 6-argument version of
     *  shortestPath, but uses the .weight and .setWeight methods of
     *  the edges and vertices themselves to determine and set
     *  weights. If V1 is unreachable from V0, returns null and sets
     *  the minimum path weights of all reachable nodes.  The distance
     *  to a node unreachable from V0 is Double.POSITIVE_INFINITY. */
    public static
    <VLabel extends Weightable, ELabel extends Weighted>
    List<Graph<VLabel, ELabel>.Edge>
    shortestPath(Graph<VLabel, ELabel> G,
                 Graph<VLabel, ELabel>.Vertex V0,
                 Graph<VLabel, ELabel>.Vertex V1,
                 Distancer<? super VLabel> h) {
        Weighter<VLabel> vweighter = new Weighter<VLabel>() {
            @Override
            public void setWeight(VLabel x, double v) {
                x.setWeight(v);
            }
            @Override
            public double weight(VLabel x) {
                return x.weight();
            }
        };

        Weighting<ELabel> eweighter = new Weighting<ELabel>() {
            @Override
            public double weight(ELabel x) {
                return x.weight();
            }
        };
        return shortestPath(G, V0, V1, h, vweighter, eweighter);
    }


    /** Returns a distancer whose dist method always returns 0. */
    public static final Distancer<Object> ZERO_DISTANCER =
        new Distancer<Object>() {
            @Override
            public double dist(Object v0, Object v1) {
                return 0.0;
            }
        };
}
