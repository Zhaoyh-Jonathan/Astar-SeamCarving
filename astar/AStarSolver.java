package astar;

import edu.princeton.cs.algs4.Stopwatch;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @see ShortestPathsSolver for more method documentation
 */
public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {
    private SolverOutcome outcome;
    private double solutionWeight;
    private List<Vertex> solution;
    private double timeSpent;
    private int numStates;

    private TreeMapMinPQ<Vertex> PQ;  // priority queue, the fringe
    private HashMap<Vertex, Double> distTo;
    private HashMap<Vertex, Vertex> edgeTo;

    /**
     * Immediately solves and stores the result of running memory optimized A*
     * search, computing everything necessary for all other methods to return
     * their results in constant time. The timeout is given in seconds.
     */
    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        numStates = 0;

        Stopwatch sw = new Stopwatch();
        PQ = new TreeMapMinPQ<>();
        PQ.add(start, 0);
        distTo.put(start, 0.0);

        if (start.equals(end)) {
            outcome = SolverOutcome.SOLVED;
            solution = new LinkedList<>();
            solution.add(end);
            solutionWeight = 0.0;
            timeSpent = sw.elapsedTime();
            return;
        }

        while (!(PQ.isEmpty())) {
            timeSpent = sw.elapsedTime();
            if (timeSpent > timeout) {
                outcome = SolverOutcome.TIMEOUT;
                return;
            }
            Vertex currVertex = PQ.removeSmallest();  // get the vertex with the highest priority

            if (currVertex.equals(end)) {  // Got it!
                outcome = SolverOutcome.SOLVED;
                solution = new LinkedList<>();
                solution.add(end);
                Vertex revElement = end;
                while (!(revElement.equals(start))) {
                    solution.add(edgeTo.get(revElement));
                    revElement = edgeTo.get(revElement);
                }
                Collections.reverse(solution);
                solutionWeight = distTo.get(currVertex);
                timeSpent = sw.elapsedTime();
                return;
            }

            numStates++;

            List<WeightedEdge<Vertex>> neighborEdges = input.neighbors(currVertex);

            for (WeightedEdge<Vertex> e : neighborEdges) {
                //                if ((!(e.from().equals(currVertex))) || (e.to().equals(edgeTo.get(currVertex)))) {
                //                    continue;
                //                }

                if (!(edgeTo.containsKey(e.to()))) {
                    edgeTo.put(e.to(), e.from());
                }
                if (!(distTo.containsKey(e.to()))) {
                    distTo.put(e.to(), Double.POSITIVE_INFINITY);
                }

                relax(input, e, end);
            }
        }

        //        if (edgeTo.containsKey(end)) {  // Got it!
        //            outcome = SolverOutcome.SOLVED;
        //            solution = new LinkedList<>();
        //            solution.add(end);
        //            Vertex revElement = end;
        //            while (!(revElement.equals(start))) {
        //                solution.add(edgeTo.get(revElement));
        //                revElement = edgeTo.get(revElement);
        //            }
        //            Collections.reverse(solution);
        //            solutionWeight = distTo.get(end);
        //            timeSpent = sw.elapsedTime();
        //            return;
        //        }

        outcome = SolverOutcome.UNSOLVABLE;
        solution = List.of();
        timeSpent = sw.elapsedTime();
    }


    // relax a single direction
    private void relax(AStarGraph<Vertex> input, WeightedEdge<Vertex> edge, Vertex goal) {

        double realDistance = distTo.get(edge.from()) + edge.weight();

        if (distTo.get(edge.to()) > realDistance) {
            distTo.replace(edge.to(), realDistance);
            edgeTo.replace(edge.to(), edge.from());

            // dist + heuristic
            double evalCost = realDistance + input.estimatedDistanceToGoal(edge.to(), goal);
            if (!(PQ.contains(edge.to()))) {
                PQ.add(edge.to(), evalCost);
            } else {
                PQ.changePriority(edge.to(), evalCost);
            }
        }
    }

    @Override
    public SolverOutcome outcome() {
        return outcome;
    }

    @Override
    public List<Vertex> solution() {
        return solution;
    }

    @Override
    public double solutionWeight() {
        return solutionWeight;
    }

    /** The total number of priority queue removeSmallest operations. */
    @Override
    public int numStatesExplored() {
        return numStates;
    }

    @Override
    public double explorationTime() {
        return timeSpent;
    }
}
