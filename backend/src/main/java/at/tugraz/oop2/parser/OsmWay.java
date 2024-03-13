package at.tugraz.oop2.parser;

import lombok.*;

import java.util.ArrayList;
import java.util.List;


@ToString(callSuper = true)
public class OsmWay extends OsmElement implements ReferenceAware {
    private final List<String> nodes;

    @Builder
    public OsmWay(String id) {
        super(id);
        nodes = new ArrayList<>();
    }

    public void addNodeReference(String nodeId) {
        this.nodes.add(nodeId);
    }

    @Override
    public List<String> getReferences() {
        return nodes;
    }

}

