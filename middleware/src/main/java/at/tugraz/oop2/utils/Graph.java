package at.tugraz.oop2.utils;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.locationtech.jts.geom.LineString;

@Service
public class Graph {

    private Map<Long, GraphNode> nodes = new HashMap<>();

    public void addNode(GraphNode node) {
        nodes.put(node.getId(), node);
    }

    public GraphNode getNode(long id) {
        return nodes.get(id);
    }

    public class GraphNode {
        private long id;
        private List<GraphEdge> edges = new ArrayList<>();

        public GraphNode(long id) {
            this.id = id;
        }

        public void addEdge(GraphEdge edge) {
            if (edge.getFromNode().equals(this) || edge.getToNode().equals(this)) {
                edges.add(edge);
            } else {
                throw new IllegalArgumentException("Edge does not connect to this node");
            }
        }

        public List<GraphEdge> getEdges() {
            return edges;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GraphNode node = (GraphNode) o;
            return id == node.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    public class GraphEdge {
        private GraphNode fromNode;
        private GraphNode toNode;
        private double length;
        private LineString geometry; // JTS LineString
        private Map<String, String> tags;


        public GraphEdge(GraphNode fromNode, GraphNode toNode, double length) {
            this.fromNode = fromNode;
            this.toNode = toNode;
            this.length = length;
            this.geometry = geometry;
            this.tags = tags;
        }

        public GraphNode getFromNode() {
            return fromNode;
        }

        public GraphNode getToNode() {
            return toNode;
        }

        public double getLength() {
            return length;
        }

        public LineString getGeometry() {
            return geometry;
        }

        public Map<String, String> getTags() {
            return tags;
        }

        public void setGeometry(LineString geometry) {
            this.geometry = geometry;
        }

        public void setTags(Map<String, String> tags) {
            this.tags = tags;
        }

    }
}
