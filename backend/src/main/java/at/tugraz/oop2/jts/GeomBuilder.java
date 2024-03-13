package at.tugraz.oop2.jts;

// grpc particles

// grpc messages

import at.tugraz.oop2.AmenityByID;
import at.tugraz.oop2.RoadByID;
import at.tugraz.oop2.Amenities;
import at.tugraz.oop2.Roads;
import at.tugraz.oop2.Routing;
import at.tugraz.oop2.Landuse;

import at.tugraz.oop2.config.AmenitiesMany;
import at.tugraz.oop2.config.Amenity;
import at.tugraz.oop2.config.Road;
import at.tugraz.oop2.config.RoadsMany;
import at.tugraz.oop2.parser.*;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeomBuilder {
    public static Point buildPointObj(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    public static LineString buildLineStringObj(ArrayList<OsmNode> nodes) {
        GeometryFactory geometryFactory = new GeometryFactory();

        int len = nodes.size();

        Coordinate[] coordinates = new Coordinate[len];

        for (int i = 0; i < len; i++) {
            coordinates[i] = new Coordinate
                    (Double.parseDouble(nodes.get(i).getLon()), Double.parseDouble(nodes.get(i).getLat()));
        }

        return geometryFactory.createLineString(coordinates);
    }

    public static Polygon buildPolygonObj(Coordinate[] coordinates) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPolygon(coordinates);
    }

    public static MultiPolygon buildRelationObj(OsmRelation relation, OsmData osmData){
        Polygon[] polygons = new Polygon[relation.getMembers().size()];
        int polygon_iterator = 0;

        for(OsmMember member : relation.getMembers())
        {
            OsmWay way = osmData.getWay(member.getRef());
            if(way == null){
                continue;
            }
            int ref_size = way.getReferences().size();
            Coordinate[] area_coords = new Coordinate[ref_size];

            for(int i = 0; i < ref_size; i++){
                OsmNode node = osmData.getOsmNodes().get(way.getReferences().get(i));
                area_coords[i] = new Coordinate(Double.parseDouble(node.getLon()), Double.parseDouble(node.getLat()));
            }
            polygons[polygon_iterator] = GeomBuilder.buildPolygonObj(area_coords);
            polygon_iterator++;
        }

        GeometryFactory factory = new GeometryFactory();
        return factory.createMultiPolygon(polygons);
    }

    public static String buildPoint(double x, double y) {
        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(GeomBuilder.buildPointObj(x, y));
    }

    public static String buildLineString(ArrayList<OsmNode> nodes) {
        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(GeomBuilder.buildLineStringObj(nodes));
    }

    public static String buildMultyPoligon(MultiPolygon multiPolygon){
        GeoJsonWriter writer = new GeoJsonWriter();
        return writer.write(multiPolygon);
    }

    public static Geometry calculateZoneFromBbox(double bbox_tlx, double bbox_tly, double bbox_brx, double bbox_bry)
            throws FactoryException, TransformException {

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(bbox_tlx, bbox_bry), // bottom-left
                new Coordinate(bbox_tlx, bbox_tly), // top-left
                new Coordinate(bbox_brx, bbox_tly), // top-right
                new Coordinate(bbox_brx, bbox_bry), // bottom-right
                new Coordinate(bbox_tlx, bbox_bry)  // back to bottom-left to close the ring
        };

        return GeomBuilder.buildPolygonObj(coordinates);
    }

    public static List<Object> ConvertEPGS(Point point, Polygon polygon, MultiPolygon multiPolygon) throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:31256");

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

        List<Object> return_list = new ArrayList<>();
        if(point != null){
            return_list.add((Point) JTS.transform(point, transform));
            return return_list;
        }
        else if(polygon != null){
            return_list.add((Polygon) JTS.transform(polygon, transform));
            return return_list;
        }
        else{
            return_list.add((MultiPolygon) JTS.transform(multiPolygon, transform));
            return return_list;
        }
    }

    public static Amenity buildAmenityByIDJSON(OsmNode node, long id) {
        Map<String, String> tags = node.getTags();

        String name = tags.getOrDefault("name", "");

        String geom = GeomBuilder.buildPoint
                (Double.parseDouble(node.getLon()), Double.parseDouble(node.getLat()));
        String type = tags.getOrDefault("amenity", "");

        return new Amenity(id, name, geom, tags, type);
    }

    public static Amenity buildRelationByIDJSON(OsmRelation relation, MultiPolygon multiPolygon, long id) {
        Map<String, String> tags = relation.getTags();

        String name = tags.getOrDefault("name", "");

        String geom = GeomBuilder.buildMultyPoligon(multiPolygon);
        String type = tags.getOrDefault("amenity", "");

        return new Amenity(id, name, geom, tags, type);
    }

    public static Road buildRoadByIDJSON(OsmWay way, ArrayList<OsmNode> nodes, long id) {
        Map<String, String> tags = way.getTags();

        String name = tags.getOrDefault("name", "");

        String geom = GeomBuilder.buildLineString(nodes);

        String type = tags.getOrDefault("highway", "");

        return new Road(id, name, geom, tags, type, nodes);
    }

    public static String buildAmenityByID(OsmNode node, long id) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(GeomBuilder.buildAmenityByIDJSON(node, id));

            return jsonString;
        } catch (Exception e) {
            return null;
        }
    }

    // under rebuilding
    public static String buildRelationByID(OsmRelation relation, OsmData osmData, long id) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString;

        MultiPolygon multiPolygon = GeomBuilder.buildRelationObj(relation, osmData);

        try {
            jsonString = mapper.writeValueAsString(GeomBuilder.buildRelationByIDJSON(relation, multiPolygon, id));

            return jsonString;
        } catch (Exception e) {
            return null;
        }
    }

    public static String buildAmenities(ArrayList<OsmNode> nodes, int take, int skip) {
        AmenitiesMany amenities = new AmenitiesMany();

        int toTake = take;
        if(skip != 0){
            toTake += skip;
        }
        if(toTake > nodes.size()){
            toTake = nodes.size();
        }

        for (int i = skip; i < toTake; i++) {
            amenities.addNewEntrie(GeomBuilder.buildAmenityByIDJSON(nodes.get(i), Long.parseLong(nodes.get(i).getId())));
        }



        amenities.setPaging(skip, take, nodes.size());

        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(amenities);

            return jsonString;
        } catch (Exception e) {
            return null;
        }
    }

    public static String buildRoadByID(OsmWay way, ArrayList<OsmNode> nodes, long id) {

        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(GeomBuilder.buildRoadByIDJSON(way, nodes, id));

            return jsonString;
        } catch (Exception e) {
            return null;
        }
    }

    public static String buildRoads(ArrayList<Road> ways, int take, int skip) {
        RoadsMany roads = new RoadsMany();

        int toTake = take;
        if(skip != 0){
            toTake += skip;
        }
        if(toTake > ways.size()){
            toTake = ways.size();
        }

        for (int i = skip; i < toTake; i++) {
            roads.addNewEntrie(ways.get(i));
        }

        roads.setPaging(skip, take, ways.size());

        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        try {
            jsonString = mapper.writeValueAsString(roads);

            return jsonString;
        } catch (Exception e) {
            return null;
        }
    }
}
