package graph;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.PriorityQueue;

/** Implements a generalized traversal of a graph.  At any given time,
 *  there is a particular set of untraversed vertices---the "fringe."
 *  Traversal consists of repeatedly removing an untraversed vertex
 *  from the fringe, visting it, and then adding its untraversed
 *  successors to the fringe.  The client can dictate an ordering on
 *  the fringe, determining which item is next removed, by which kind
 *  of traversal is requested.
 *     + A depth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at one end.  It also revisits the node
 *       itself after traversing all successors by calling the
 *       postVisit method on it.
 *     + A breadth-first traversal treats the fringe as a list, and adds
 *       and removes vertices at different ends.  It also revisits the node
 *       itself after traversing all successors as for depth-first
 *       traversals.
 *     + A general traversal treats the fringe as an ordered set, as
 *       determined by a Comparator argument.  There is no postVisit
 *       for this type of traversal.
 *  As vertices are added to the fringe, the traversal calls a
 *  preVisit method on the vertex.
 *
 *  Generally, the client will extend Traversal, overriding the visit,
 *  preVisit, and postVisit methods, as desired (by default, they do nothing).
 *  Any of these methods may throw StopException to halt the traversal
 *  (temporarily, if desired).  The preVisit method may throw a
 *  RejectException to prevent a vertex from being added to the
 *  fringe, and the visit method may throw a RejectException to
 *  prevent its successors from being added to the fringe.
 *  @author Charles Lee
 */
public class Traversal<VLabel, ELabel> {

    /** Perform a traversal of G over all vertices reachable from V.
     *  ORDER determines the ordering in which the fringe of
     *  untraversed vertices is visited.  The effect of specifying an
     *  ORDER whose results change as a result of modifications made during the
     *  traversal is undefined. */
    public void traverse(Graph<VLabel, ELabel> G,
                         Graph<VLabel, ELabel>.Vertex v,
                         final Comparator<VLabel> order) {
        final Comparator<Graph<VLabel, ELabel>.Vertex> vC =
            new Comparator<Graph<VLabel, ELabel>.Vertex>() {
                @Override
                public int compare(Graph<VLabel, ELabel>.Vertex u, Graph<VLabel,
                    ELabel>.Vertex v) {
                    return order.compare(u.getLabel(), v.getLabel());
                }
            };
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> fringe =
            new PriorityQueue<Graph<VLabel, ELabel>.Vertex>(G.vertexSize(), vC);
        fringe.add(v);
        while (!fringe.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex node = fringe.poll();
            if (!node.isMarked()) {
                try {
                    visit(node);
                } catch (StopException ex) {
                    _finalVertex = node;
                    return;
                } catch (RejectException ex) {
                    continue;
                }
            }
            node.mark();
            if (G.isDirected()) {
                isDir(true, G, node, fringe);
            } else {
                Iteration<Graph<VLabel, ELabel>.Vertex> neigh =
                    G.neighbors(node);
                while (neigh.hasNext()) {
                    Graph<VLabel, ELabel>.Vertex current = neigh.next();
                    if (!current.isMarked() && !fringe.contains(current)) {
                        Iteration<Graph<VLabel, ELabel>.Edge> edges =
                            G.outEdges(node);
                        Graph<VLabel, ELabel>.Edge edg = edges.next();
                        try {
                            preVisit(edg, node);
                        } catch (StopException e) {
                            _finalVertex = current;
                            _finalEdge = edg;
                            break;
                        } catch (RejectException e) {
                            continue;
                        }
                        fringe.add(current);
                    }
                }
            }
            for (Graph<VLabel, ELabel>.Edge  elem: G.outEdges(v)) {
                preVisit(elem, elem.getV(node));
                fringe.add(elem.getV(node));
            }
        }
    }
    /** Previsits to NODE in G using FRINGE if DIRECTED. */
    private void isDir(Boolean directed, Graph<VLabel, ELabel> G,
        Graph<VLabel, ELabel>.Vertex node,
        PriorityQueue<Graph<VLabel, ELabel>.Vertex> fringe) {
        if (directed) {
            Iteration<Graph<VLabel, ELabel>.Vertex> su = G.successors(node);
            while (su.hasNext()) {
                Graph<VLabel, ELabel>.Vertex current = su.next();
                if (!current.isMarked() && !fringe.contains(current)) {
                    Iteration<Graph<VLabel, ELabel>.Edge> ed = G.outEdges(node);
                    while (ed.hasNext()) {
                        Graph<VLabel, ELabel>.Edge edg = ed.next();
                        if (edg.getV(node).equals(current)) {
                            try {
                                preVisit(edg, node);
                            } catch (StopException e) {
                                break;
                            } catch (RejectException e) {
                                continue;
                            }
                            fringe.add(current);
                        }
                    }
                }
            }
        }
    }

    /** Performs a depth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it or removed from it at one end in
     *  an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void depthFirstTraverse(Graph<VLabel, ELabel> G,
                                   Graph<VLabel, ELabel>.Vertex v) {
        ArrayList<Graph<VLabel, ELabel>.Vertex> fringe =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> marked =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        fringe.add(v);
        while (!fringe.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex pop = fringe.get(fringe.size() - 1);
            fringe.remove(fringe.size() - 1);
            if (!pop.isMarked()) {
                try {
                    visit(pop);
                } catch (StopException e) {
                    Iteration<Graph<VLabel, ELabel>.Edge> edg = G.outEdges(pop);
                    Graph<VLabel, ELabel>.Edge edger = edg.next();
                    setF(pop, edger);
                    return;
                } catch (RejectException e) {
                    continue;
                }
                markMarked(pop, marked);
                for (Graph<VLabel, ELabel>.Vertex ver : marked) {
                    if (visited(ver, G)) {
                        try {
                            postVisit(ver);
                        } catch (StopException e) {
                            _finalVertex = ver;
                            return;
                        } catch (RejectException e) {
                            continue;
                        }
                        try {
                            postVisit(pop);
                        } catch (StopException e) {
                            _finalVertex = ver;
                            return;
                        } catch (RejectException e) {
                            continue;
                        }
                    }
                }
                Iteration<Graph<VLabel, ELabel>.Vertex> vertices =
                    G.successors(pop);
                while (vertices.hasNext()) {
                    Graph<VLabel, ELabel>.Vertex neigh = vertices.next();
                    if (!neigh.isMarked()) {
                        try {
                            preVisit(getEdge(pop, neigh, G), pop);
                        } catch (StopException e) {
                            setF(pop, getEdge(pop, neigh, G));
                            return;
                        } catch (RejectException e) {
                            continue;
                        }
                        fringe.add(neigh);
                    }
                }
            }
        }
    }
    /**Marks and adds POP to MARKED. */
    private void markMarked(Graph<VLabel, ELabel>.Vertex pop,
        ArrayList<Graph<VLabel, ELabel>.Vertex> marked) {
        pop.mark();
        marked.add(pop);
    }
    /** Sets V and E to final edge and vertex. */
    private void setF(Graph<VLabel, ELabel>.Vertex v,
        Graph<VLabel, ELabel>.Edge e) {
        _finalVertex = v;
        _finalEdge = e;
    }
    /** Returns true if the successors of a NODE have been visited
     * and marked in G. */
    private boolean visited(Graph<VLabel, ELabel>.Vertex node,
        Graph<VLabel, ELabel> G) {
        boolean bool = true;
        if (G.isDirected()) {
            Iteration<Graph<VLabel, ELabel>.Vertex> v = G.successors(node);
            while (v.hasNext()) {
                Graph<VLabel, ELabel>.Vertex  vertex = v.next();
                if (!vertex.isMarked()) {
                    bool = false;
                    break;
                }
            }
        } else {
            Iteration<Graph<VLabel, ELabel>.Vertex> v = G.neighbors(node);
            while (v.hasNext()) {
                Graph<VLabel, ELabel>.Vertex vertex = v.next();
                if (!vertex.isMarked()) {
                    bool = false;
                    break;
                }
            }
        }
        return bool;
    }

    /** Performs a breadth-first traversal of G over all vertices
     *  reachable from V.  That is, the fringe is a sequence and
     *  vertices are added to it at one end and removed from it at the
     *  other in an undefined order.  After the traversal of all successors of
     *  a node is complete, the node itself is revisited by calling
     *  the postVisit method on it. */
    public void breadthFirstTraverse(Graph<VLabel, ELabel> G,
                                     Graph<VLabel, ELabel>.Vertex v) {
        ArrayList<Graph<VLabel, ELabel>.Vertex> fringe =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        ArrayList<Graph<VLabel, ELabel>.Vertex> marked =
            new ArrayList<Graph<VLabel, ELabel>.Vertex>();
        fringe.add(v);
        while (!fringe.isEmpty()) {
            Graph<VLabel, ELabel>.Vertex vertex =
            fringe.get(0); fringe.remove(0);
            if (!vertex.isMarked()) {
                try {
                    visit(vertex);
                } catch (StopException e) {
                    Iteration<Graph<VLabel, ELabel>.Edge> edges =
                        G.outEdges(vertex);
                    setF(vertex, edges.next());
                    return;
                } catch (RejectException e) {
                    continue;
                }
                vertex.mark(); marked.add(vertex);
                for (Graph<VLabel, ELabel>.Vertex ver : marked) {
                    if (visited(vertex, G)) {
                        try {
                            postVisit(vertex);
                        } catch (StopException e) {
                            _finalVertex = ver;
                            return;
                        } catch (RejectException e) {
                            continue;
                        }
                    }
                }
                Iteration<Graph<VLabel, ELabel>.Vertex> vertices =
                    G.successors(vertex);
                while (vertices.hasNext()) {
                    Graph<VLabel, ELabel>.Vertex suc = vertices.next();
                    if (!suc.isMarked()) {
                        try {
                            Graph<VLabel, ELabel>.Edge g = getEdge(vertex,
                                suc, G);
                            preVisit(g, suc);
                        } catch (StopException e) {
                            Iteration<Graph<VLabel, ELabel>.Edge> edges =
                                G.outEdges(vertex);
                            while (edges.hasNext()) {
                                Graph<VLabel, ELabel>.Edge edg = edges.next();
                                if (edg.getV(vertex).equals(suc)) {
                                    setF(vertex, edg);
                                }
                            }
                        } catch (RejectException e) {
                            continue;
                        }
                        fringe.add(suc);
                    }
                }
            }
        }
    }
    /** Returns the edge between vertices V1 and V2 in graph G. */
    private Graph<VLabel, ELabel>.Edge getEdge(Graph<VLabel, ELabel>.Vertex v1,
                                             Graph<VLabel, ELabel>.Vertex v2,
                                             Graph<VLabel, ELabel> G) {
        if (G.isDirected()) {
            for (Graph<VLabel, ELabel>.Edge edge : G.edges()) {
                if (edge.getV0() == v1 && edge.getV1() == v2) {
                    return edge;
                }
            }
        } else {
            for (Graph<VLabel, ELabel>.Edge edge : G.edges()) {
                if ((edge.getV0() == v1 && edge.getV1() == v2)
                    || (edge.getV0() == v2 && edge.getV1() == v1)) {
                    return edge;
                }
            }
        }
        return null;
    }
    /** Continue the previous traversal starting from V.
     *  Continuing a traversal means that we do not traverse
     *  vertices that have been traversed previously. */
    public void continueTraversing(Graph<VLabel, ELabel>.Vertex v) {
        traverse(_graph, v, _comp);
    }

    /** If the traversal ends prematurely, returns the Vertex argument to
     *  preVisit, visit, or postVisit that caused a Visit routine to
     *  return false.  Otherwise, returns null. */
    public Graph<VLabel, ELabel>.Vertex finalVertex() {
        return _finalVertex;
    }

    /** If the traversal ends prematurely, returns the Edge argument to
     *  preVisit that caused a Visit routine to return false. If it was not
     *  an edge that caused termination, returns null. */
    public Graph<VLabel, ELabel>.Edge finalEdge() {
        return _finalEdge;
    }

    /** Returns the last graph argument to a traverse routine, or null if none
     *  of these methods have been called. */
    protected Graph<VLabel, ELabel> theGraph() {
        return _graph;
    }

    /** Method to be called when adding the node at the other end of E from V0
     *  to the fringe. If this routine throws a StopException,
     *  the traversal ends.  If it throws a RejectException, the edge
     *  E is not traversed. The default does nothing.
     */
    protected void preVisit(Graph<VLabel, ELabel>.Edge e,
                            Graph<VLabel, ELabel>.Vertex v0) {
    }

    /** Method to be called when visiting vertex V.  If this routine throws
     *  a StopException, the traversal ends.  If it throws a RejectException,
     *  successors of V do not get visited from V. The default does nothing. */
    protected void visit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** Method to be called immediately after finishing the traversal
     *  of successors of vertex V in pre- and post-order traversals.
     *  If this routine throws a StopException, the traversal ends.
     *  Throwing a RejectException has no effect. The default does nothing.
     */
    protected void postVisit(Graph<VLabel, ELabel>.Vertex v) {
    }

    /** The Vertex (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Vertex _finalVertex;
    /** The Edge (if any) that terminated the last traversal. */
    protected Graph<VLabel, ELabel>.Edge _finalEdge;
    /** The last graph traversed. */
    protected Graph<VLabel, ELabel> _graph;
    /** Comparator for traversal. */
    protected Comparator<VLabel> _comp;

}
