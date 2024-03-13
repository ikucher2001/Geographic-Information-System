package at.tugraz.oop2;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import at.tugraz.oop2.config.Road;
import at.tugraz.oop2.jts.GeomBuilder;
import at.tugraz.oop2.parser.*;
import at.tugraz.oop2.tile.MapTileResolver;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import at.tugraz.oop2.jts.RoadTransformer;
import org.locationtech.jts.geom.*;
import io.grpc.StatusRuntimeException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

public class MapServiceServer {
    private Server grpcServer;
    private static final String JMAP_BACKEND_PORT = "JMAP_BACKEND_PORT";
    private static final int JMAP_BACKEND_PORT_DEFAULT = 8020;
    private static final String JMAP_BACKEND_OSMFILE = "JMAP_BACKEND_OSMFILE";
    private static final String JMAP_BACKEND_OSMFILE_DEFAULT = "data/styria_reduced.osm";
//    private static final String JMAP_BACKEND_OSMFILE_DEFAULT = "data/graz_tiny_reduced.osm";

    private void startGRPCServer(int port, OsmData data) throws IOException {
        GeographyImpl geographyImpl = new GeographyImpl();
        geographyImpl.storeData(data);

        grpcServer = ServerBuilder.forPort(port)
                .addService(geographyImpl) // here add grpc services
                .build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            MapServiceServer.this.stopGRPCServer();
        }));
    }

    private void stopGRPCServer() {
        if (grpcServer != null) {
            grpcServer.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final MapServiceServer grpcServer = new MapServiceServer();

        String osmFile = System.getenv().getOrDefault(JMAP_BACKEND_OSMFILE, JMAP_BACKEND_OSMFILE_DEFAULT);

        String backendPortEnv = System.getenv().getOrDefault(JMAP_BACKEND_PORT, "" + JMAP_BACKEND_PORT_DEFAULT);

        int backendPort = JMAP_BACKEND_PORT_DEFAULT;
        try {
            backendPort = Integer.parseUnsignedInt(backendPortEnv);

        } catch (NumberFormatException e) {
            //do nothing
        }

        OsmData data = OsmParser.run(osmFile);

        Collection<Geometry> ways = RoadTransformer.createRoads(data);

        // start grpc server
        grpcServer.startGRPCServer(backendPort, data);
        // Log backend startup
        MapLogger.backendStartup(backendPort, osmFile);
        grpcServer.blockUntilShutdown();
    }

    // !!! works
    static class GeographyImpl extends GeographyGrpc.GeographyImplBase {
        private OsmData osmData;

        public void storeData(OsmData data) {
            osmData = data;
        }

        public static Boolean logicalCheckNodes(OsmNode node, ReqAmenityMany req){
            return node.getTags() != null && node.getTags().containsKey("amenity") &&
                    ((req.getAmenity().length() != 0 && node.getTags().get("amenity").equals(req.getAmenity())) ||
                            req.getAmenity().length() == 0);
        }

        public static Boolean logicalCheckRelations(OsmRelation relation, ReqAmenityMany req){
            return relation.getTags() != null && relation.getTags().containsKey("amenity") &&
                    ((req.getAmenity().length() != 0 && relation.getTags().get("amenity").equals(req.getAmenity())) ||
                            req.getAmenity().length() == 0);
        }

        @Override
        public void testReqCl(EmptyValue req, StreamObserver<TestRes> res) {
            TestRes reply = TestRes.newBuilder().setMessage("Incoming grpc request").build();
            res.onNext(reply);
            res.onCompleted();
        }

        @Override
        public void getAmenityById(ReqById req, StreamObserver<AmenityByID> res) {
            MapLogger.backendLogAmenityRequest((int) req.getId());
            String requestedId = Long.toString(req.getId());

            OsmNode osmNode = osmData.getNode(requestedId);
            OsmRelation osmRelation = osmData.getRelation(requestedId);

            if (osmNode == null && osmRelation == null) {
                res.onError(new StatusRuntimeException(Status.NOT_FOUND));
                return;
            }

            String jsonString = null;
            if(osmRelation != null){
                jsonString = GeomBuilder.buildRelationByID(osmRelation, osmData, req.getId());
            }
            else {
                jsonString = GeomBuilder.buildAmenityByID(osmNode, req.getId());
            }

            if (jsonString == null) {
                res.onError(new StatusRuntimeException(Status.INTERNAL));
                return;
            }

            AmenityByID response = AmenityByID.newBuilder()
                    .setResponse(jsonString)
                    .build();

            res.onNext(response);
            res.onCompleted();
        }

        @Override
        public void getRoadById(ReqById req, StreamObserver<RoadByID> res) {
            MapLogger.backendLogRoadRequest((int) req.getId());
            String requestedId = Long.toString(req.getId());

            OsmWay osmWay = osmData.getWay(requestedId);

            if (osmWay == null) {
                res.onError(new StatusRuntimeException(Status.NOT_FOUND));
                return;
            }

            ArrayList<OsmNode> nodes = new ArrayList<>();
            for (String id : osmWay.getReferences()) {

                OsmNode referencedNode = osmData.getNode(id);
                if (referencedNode != null) {
                    nodes.add(referencedNode);
                }
            }

            String jsonString = GeomBuilder.buildRoadByID(osmWay, nodes, req.getId());

            if (jsonString == null) {
                res.onError(new StatusRuntimeException(Status.INTERNAL));
                return;
            }

            RoadByID response = RoadByID.newBuilder()
                    .setResponse(jsonString)
                    .build();

            res.onNext(response);
            res.onCompleted();

        }

        @Override
        public void getAmenities(ReqAmenityMany req, StreamObserver<Amenities> res) {
            MapLogger.backendLogAmenitiesRequest();
            ArrayList<OsmNode> nodes = new ArrayList<>();
            ArrayList<OsmRelation> relations = new ArrayList<>();

            Polygon searchField = null;
            Point centerPoint = null;

            if (req.getLocationCase() == ReqAmenityMany.LocationCase.BOXES) {
                Boxes boxes = req.getBoxes();
                try {
                    searchField = (Polygon) GeomBuilder.ConvertEPGS(null, (Polygon) GeomBuilder.calculateZoneFromBbox
                            (boxes.getTlX(), boxes.getTlY(), boxes.getBrX(), boxes.getBrY()), null).get(0);

                    for (OsmNode node : osmData.getOsmNodesCollection()) {
                        double lon = Double.parseDouble(node.getLon()); // Longitude -> x
                        double lat = Double.parseDouble(node.getLat()); // Latitude -> y
                        Point point = (Point) GeomBuilder.ConvertEPGS(GeomBuilder.buildPointObj(lon, lat), null, null).get(0);

                        if (searchField.contains(point) && GeographyImpl.logicalCheckNodes(node, req)) {
                            nodes.add(node);
                        }
                    }

//                    for(OsmRelation relation : osmData.getOsmRelationsCollection()){
//                        MultiPolygon multiPolygon = (MultiPolygon) GeomBuilder.ConvertEPGS
//                                (null, null, GeomBuilder.buildRelationObj(relation, osmData)).get(0);
//
//
//                    }
                } catch (FactoryException | TransformException e) {
                    res.onError(new StatusRuntimeException(Status.INTERNAL));
                    return;
                }
            } else {
                Points points = req.getPoints();
                try {
                    centerPoint = (Point) GeomBuilder.ConvertEPGS
                            (GeomBuilder.buildPointObj(points.getPointX(), points.getPointY()), null, null).get(0);

                    for (OsmNode node : osmData.getOsmNodesCollection()) {
                        double lon = Double.parseDouble(node.getLon()); // Longitude -> x
                        double lat = Double.parseDouble(node.getLat()); // Latitude -> y
                        Point point = (Point) GeomBuilder.ConvertEPGS(GeomBuilder.buildPointObj(lon, lat), null, null).get(0);

                        if (centerPoint.distance(point) <= points.getPointD() && GeographyImpl.logicalCheckNodes(node, req)) {
                            nodes.add(node);
                        }
                    }

//                    for(OsmRelation relation : osmData.getOsmRelationsCollection()){
//                        MultiPolygon multiPolygon = (MultiPolygon) GeomBuilder.ConvertEPGS
//                                (null, null, GeomBuilder.buildRelationObj(relation, osmData)).get(0);
//
//                        if (centerPoint.distance(multiPolygon) <= points.getPointD() && GeographyImpl.logicalCheckRelations(relation, req)) {
//                            relations.add(relation);
//                        }
//                    }
                } catch (FactoryException | TransformException e) {
                    res.onError(new StatusRuntimeException(Status.INTERNAL));
                    return;
                }
            }

            String jsonString = GeomBuilder.buildAmenities(nodes, req.getTake(), req.getSkip());

            if (jsonString == null) {
                res.onError(new StatusRuntimeException(Status.INTERNAL));
                return;
            }

            Amenities response = Amenities.newBuilder()
                    .setResponse(jsonString)
                    .build();

            res.onNext(response);
            res.onCompleted();
        }

        @Override
        public void getRoads(ReqRoadMany req, StreamObserver<Roads> res) {
            MapLogger.backendLogRoadsRequest();
            Boxes boxes = req.getBoxes();
            Geometry area = null;
            try {
                area = GeomBuilder.calculateZoneFromBbox
                        (boxes.getTlX(), boxes.getTlY(), boxes.getBrX(), boxes.getBrY());
            } catch (FactoryException | TransformException e) {
                res.onError(new StatusRuntimeException(Status.INTERNAL));
                return;
            }

            ArrayList<Road> found_ways = new ArrayList<>();

            for (OsmWay way : osmData.getOsmWaysCollection()) {
                ArrayList<OsmNode> way_nodes = new ArrayList<>();
                if(!(way.getTags() != null && way.getTags().containsKey("highway"))){
                    continue;
                }
                if(req.getRoad().length() > 0 && !way.getTags().get("highway").equals(req.getRoad())){
                    continue;
                }
                for (String id : way.getReferences()) {
                    OsmNode node = osmData.getNode(id);
                    if (node != null) {
                        way_nodes.add(node);
                    }
                }
                if(way_nodes.size() < 2) {
                    continue;
                }
                LineString way_transformed = GeomBuilder.buildLineStringObj(way_nodes);
                if (area.contains(way_transformed) ||
                        (!area.contains(way_transformed) && area.intersects(way_transformed))) {
                    found_ways.add(GeomBuilder.buildRoadByIDJSON
                            (way, way_nodes, Long.parseLong(way.getId())));
                }
            }

            String jsonString = GeomBuilder.buildRoads(found_ways, req.getTake(), req.getSkip());

            if (jsonString == null) {
                res.onError(new StatusRuntimeException(Status.INTERNAL));
                return;
            }

            Roads response = Roads.newBuilder()
                    .setResponse(jsonString)
                    .build();

            res.onNext(response);
            res.onCompleted();
        }

        @Override
        public void getMapTile(ReqMapTile request, StreamObserver<MapTile> responseObserver) {
            List<String> layers = Arrays.asList(request.getLayers().split(","));
            MapLogger.backendLogMapRequest(request.getX(), request.getY(), request.getZ(), layers);

            MapTileResolver resolver = new MapTileResolver();
            byte[] mapTile = resolver.getMapTile(request.getX(), request.getY(), request.getZ(), layers, osmData);

            MapTile result = MapTile.newBuilder()
                    .setResponse(ByteString.copyFrom(mapTile))
                    .build();

            responseObserver.onNext(result);
            responseObserver.onCompleted();
        }

        //
//        @Override
//        public void GetRouting(ReqRouting req, StreamObserver<Routing> res){
//
//            res.onNext(reply);
//            res.onCompleted();
//        }
//
//        @Override
//        public void GetLanduse(Boxes req, StreamObserver<Landuse> res){
//
//            res.onNext(reply);
//            res.onCompleted();
//        }
    }
}
