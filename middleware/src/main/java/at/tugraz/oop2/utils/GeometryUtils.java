package at.tugraz.oop2.utils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.locationtech.jts.operation.buffer.BufferOp;
import org.locationtech.jts.operation.buffer.BufferParameters;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;


public class GeometryUtils {
    public static Polygon buildPolygonObj(Coordinate[] coordinates) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPolygon(coordinates);
    }

    public static Polygon calculateAreaFromBbox(double bbox_tlx, double bbox_tly, double bbox_brx, double bbox_bry)
//            throws FactoryException, TransformException
    {

        Coordinate[] coordinates = new Coordinate[]{
                new Coordinate(bbox_tlx, bbox_bry), // bottom-left
                new Coordinate(bbox_tlx, bbox_tly), // top-left
                new Coordinate(bbox_brx, bbox_tly), // top-right
                new Coordinate(bbox_brx, bbox_bry), // bottom-right
                new Coordinate(bbox_tlx, bbox_bry)  // back to bottom-left to close the ring
        };

        return GeometryUtils.buildPolygonObj(coordinates);
    }

    public static Geometry ConvertEPGS(Geometry geometry) throws FactoryException, TransformException {
        CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:31256");

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

        return JTS.transform(geometry, transform);
    }

    public static Geometry intersectGeometries(Geometry geom1, Geometry geom2) {
        return geom1.intersection(geom2);
    }

}
