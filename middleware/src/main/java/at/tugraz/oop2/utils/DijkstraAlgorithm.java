package at.tugraz.oop2.utils;
import java.util.*;
import at.tugraz.oop2.utils.Graph.*;

public class DijkstraAlgorithm {
    public static List<GraphEdge> findShortestPath(GraphNode startNode, GraphNode endNode) {
        Map<GraphNode, GraphNode> predecessors = new HashMap<>();
        Map<GraphNode, Double> distances = new HashMap<>();
        PriorityQueue<GraphNode> nodesQueue = new PriorityQueue<>(Comparator.comparing(distances::get));

        for (GraphNode node : getAllNodes(startNode)) {
            distances.put(node, Double.MAX_VALUE);
        }

        distances.put(startNode, 0.0);
        nodesQueue.add(startNode);

        while (!nodesQueue.isEmpty()) {
            GraphNode currentNode = nodesQueue.poll();

            if (currentNode.equals(endNode)) {
                return constructPath(predecessors, endNode);
            }

            for (GraphEdge edge : currentNode.getEdges()) {
                GraphNode adjacentNode = edge.getToNode();
                double weight = edge.getLength();
                double distanceThroughCurrent = distances.get(currentNode) + weight;

                if (distanceThroughCurrent < distances.get(adjacentNode)) {
                    distances.put(adjacentNode, distanceThroughCurrent);
                    predecessors.put(adjacentNode, currentNode);

                    nodesQueue.add(adjacentNode);
                }
            }
        }

        return Collections.emptyList(); // No path found
    }

    private static List<GraphEdge> constructPath(Map<GraphNode, GraphNode> predecessors, GraphNode endNode) {
        LinkedList<GraphEdge> path = new LinkedList<>();
        GraphNode step = endNode;

        if (predecessors.get(step) == null) {
            return path; // Start node is isolated
        }

        while (predecessors.get(step) != null) {
            GraphNode prevStep = predecessors.get(step);
            GraphEdge connectingEdge = findEdgeConnecting(prevStep, step);
            path.addFirst(connectingEdge);
            step = prevStep;
        }

        return path;
    }

    private static GraphEdge findEdgeConnecting(GraphNode fromNode, GraphNode toNode) {
        for (GraphEdge edge : fromNode.getEdges()) {
            if (edge.getToNode().equals(toNode)) {
                return edge;
            }
        }
        return null; // Edge not found
    }

    private static Set<GraphNode> getAllNodes(GraphNode startNode) {
        Set<GraphNode> visited = new HashSet<>();
        Stack<GraphNode> stack = new Stack<>();

        stack.push(startNode);

        while (!stack.isEmpty()) {
            GraphNode node = stack.pop();

            if (!visited.contains(node)) {
                visited.add(node);
                for (GraphEdge edge : node.getEdges()) {
                    stack.push(edge.getToNode());
                }
            }
        }

        return visited;
    }

}
