package at.tugraz.oop2;

import at.tugraz.oop2.utils.Graph;
import at.tugraz.oop2.utils.DijkstraAlgorithm;
import at.tugraz.oop2.utils.Graph.GraphNode;
import at.tugraz.oop2.utils.Graph.GraphEdge;
import at.tugraz.oop2.model.Route;
import at.tugraz.oop2.model.RoadSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.oop2.utils.*;

@Service
public class RouteService {
    private final Graph graph;

    @Autowired
    public RouteService(Graph graph) {
        this.graph = graph;
    }

    public Route calculateRoute(long fromNodeId, long toNodeId, String weighting) {
        GraphNode startNode = graph.getNode(fromNodeId);
        GraphNode endNode = graph.getNode(toNodeId);

        if (startNode == null || endNode == null) {
            // Handle the case where nodes are not found
            throw new IllegalArgumentException("Start or end node not found in the graph.");
        }

        List<GraphEdge> path = DijkstraAlgorithm.findShortestPath(startNode, endNode);
        return constructRouteFromPath(path, weighting);
    }

    private Route constructRouteFromPath(List<GraphEdge> path, String weighting) {
        List<RoadSegment> roadSegments = new ArrayList<>();
        double totalLength = 0.0;
        double totalTime = 0.0;

        for (GraphEdge edge : path) {
            RoadSegment segment = new RoadSegment();
            segment.setId(edge.getFromNode().getId());
            segment.setName(edge.getFromNode().getId() + "-" + edge.getToNode().getId());

//            LineString geometry = getGeometryForEdge(edge);
//            Map<String, String> tags = getTagsForEdge(edge);
//
//            segment.setGeometry(geometry);
//            segment.setTags(tags);

            roadSegments.add(segment);
            totalLength += edge.getLength();

            double time = calculateTime(edge, weighting);
            totalTime += time;
        }

        Route route = new Route();
        route.setLength(totalLength);
        route.setTime(totalTime);
        route.setRoads(roadSegments);
        return route;
    }

    private double calculateTime(GraphEdge edge, String weighting) {
        // Logic to calculate time based on edge length and weighting
        // Example: if weighting is 'time', use edge length and a default speed to calculate time
        if (weighting.equals("time")) {
            double defaultSpeed = 30.0;
            return (edge.getLength() / 1000.0) / defaultSpeed * 60.0; // Convert to minutes
        } else {
            return 0.0;
        }
    }

}
