package at.tugraz.oop2.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class OsmData {

    private Map<String, OsmWay> osmWays;
    private Map<String, OsmNode> osmNodes;
    private Map<String, OsmRelation> osmRelations;

    public OsmData(Collection<OsmWay> osmWays, Collection<OsmNode> osmNodes, Collection<OsmRelation> osmRelations) {
        this.osmWays = osmWays.stream().collect(Collectors.toMap(OsmWay::getId, Function.identity()));
        this.osmNodes = osmNodes.stream().collect(Collectors.toMap(OsmNode::getId, Function.identity()));
        this.osmRelations = osmRelations.stream().collect(Collectors.toMap(OsmRelation::getId, Function.identity()));
    }

    public Collection<OsmWay> getOsmWaysCollection() {
        return osmWays.values().stream()
                .sorted(Comparator.comparing(OsmWay::getId))
                .collect(Collectors.toList());
    }

    public Collection<OsmNode> getOsmNodesCollection() {
        return osmNodes.values()
                .stream()
                .sorted(Comparator.comparing(OsmNode::getId))
                .collect(Collectors.toList());
    }

    public Collection<OsmRelation> getOsmRelationsCollection() {
        return osmRelations.values()
                .stream()
                .sorted(Comparator.comparing(OsmRelation::getId))
                .collect(Collectors.toList());
    }


    public OsmWay getWay(String id) {
        return osmWays.getOrDefault(id, null);
    }

    public OsmNode getNode(String id) {
        return osmNodes.getOrDefault(id, null);
    }

    public OsmRelation getRelation(String id) {
        return osmRelations.getOrDefault(id, null);
    }


}
