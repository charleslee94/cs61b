package graph;
import java.util.ArrayList;
import java.util.Iterator;

/* Do not add or remove public or protected members, or modify the signatures of
 * any public methods.  You may add bodies to abstract methods, modify
 * existing bodies, or override inherited methods.  */

/** An undirected graph with vertices labeled with VLABEL and edges
 *  labeled with ELABEL.
 *  @author Charles Lee
 */
public class UndirectedGraph<VLabel, ELabel> extends Graph<VLabel, ELabel> {

    /** An empty graph. */
    public UndirectedGraph() {
    }

    @Override
    public boolean isDirected() {
        return false;
    }

    @Override
    public int outDegree(Vertex v) {
        return inDegree(v) + getEdg().size();
    }
    @Override
    public Iteration<Vertex> successors(Vertex v) {
        ArrayList<Vertex> lv = new ArrayList<Vertex>();
        Iteration<Edge> edges = edges();
        for (Edge edg : edges) {
            if (edg.getV0() == v) {
                lv.add(edg.getV1());
            } else if (edg.getV1() == v) {
                lv.add(edg.getV0());
            }
        }
        return Iteration.iteration(lv.iterator());
    }
    @Override
    public Iteration<Edge> outEdges(Vertex v) {
        ArrayList<Edge> edges = new ArrayList<Edge>();
        Iterator<Edge> edgeiter = getEdg().iterator();
        while (edgeiter.hasNext()) {
            Edge edg = edgeiter.next();
            if (edg.getV0() == v || edg.getV1() == v) {
                edges.add(edg);
            }
        }
        return Iteration.iteration(edges.iterator());
    }
    @Override
    public boolean contains(Vertex v0, Vertex v1) {
        if (getVer().contains(v0)) {
            Iteration<Edge> edges = outEdges(v0);
            while (edges.hasNext()) {
                Edge edge = edges.next();
                if (edge.getV(v0).equals(v1)) {
                    return true;
                }
            }
            Iteration<Edge> edges2 = outEdges(v1);
            while (edges2.hasNext()) {
                Edge edge2 = edges2.next();
                if (edge2.getV(v1).equals(v0)) {
                    return true;
                }
            }
        }
        return false;
    }
}
