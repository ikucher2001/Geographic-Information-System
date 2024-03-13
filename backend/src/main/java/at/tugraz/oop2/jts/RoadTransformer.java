package at.tugraz.oop2.jts;

import at.tugraz.oop2.parser.OsmData;
import at.tugraz.oop2.parser.OsmElement;
import at.tugraz.oop2.parser.OsmNode;
import org.locationtech.jts.geom.*;

import javax.management.relation.Relation;
import java.util.*;
import java.util.stream.Collectors;

public class RoadTransformer {
    private static final GeometryFactory factory = new GeometryFactory();

    public static Collection<Geometry> createRoads(OsmData data) {
        Map<String, OsmNode> nodesById = data.getOsmNodesCollection().stream().collect(Collectors.toMap(OsmElement::getId, node -> node));

        return data.getOsmWaysCollection().stream().map(way -> {
                    List<Coordinate> coordinateList = new ArrayList<>();
                    way.getReferences().forEach(referencedNodeId -> {
                        OsmNode osmNode = nodesById.getOrDefault(referencedNodeId, null);

                        if (osmNode != null) {
                            coordinateList.add(new Coordinate(Double.parseDouble(osmNode.getLon()), Double.parseDouble(osmNode.getLat())));
                        }

                    });

                    if (coordinateList.size() >= 2) {
                        return buildRoad(coordinateList);
                    }

                    return null;

                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private static Geometry buildRoad(List<Coordinate> coordinateList) {

        Coordinate[] array = coordinateList.toArray(Coordinate[]::new);

        LineString lineString = factory.createLineString(array);


        if (lineString.isClosed() && coordinateList.size() > 2) {

            //create Polygon
            Polygon polygon = factory.createPolygon(array);
            return factory.createGeometry(polygon);
        } else {
            return factory.createGeometry(lineString);
        }
    }

    private Relation buildRelation() {
//        if "multipolygon" in tags:
//        multi_polygons=[]
//        inners=[]
//        outer=None
//        for(i=0; i<members.lenght;):
//        closed_circle=getNextClosed(i, members)
//      # this iterates over the next memebers and returns a polygon
//      # if it is able to find a combination of coordinated in the next
//      # (one or more) *same* role-types,
//      # i.e. a closed ring of only outer or inner line segments
//        if closed_circle:
//        if closed_circle.last_role=="outer":
//        if outer:
//        multi_polygons+=[buildMultipolygon([outer, ...inners])] #multipolygons usually have the first ring as the outer one
//        outer=closed_circle.polygon
//
//        elif closed_circle.last_role=="inner":
//        inners+=[closed_circle.polygon]
//
//        i=closed_circle.last_member_idx+1
//      else
//        raise error
//    # if there is still a an outer ring pick it up and add it
//        if outer:
//        multi_polygons+=[buildMultipolygon([outer, ...inners])]
//        return buildGeometrycollection(multi_polygons)
//  else:
//        return buildGeometrycollection(members)
//    # just multiple geometries
        return null;
    }

}
