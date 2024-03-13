package at.tugraz.oop2;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;
import at.tugraz.oop2.utils.*;

import java.util.ArrayList;
import java.util.List;

import at.tugraz.oop2.utils.Colors;

@Service
public class LandUsageService {

    public LandUsage[] getLandUsages() {
        List<LandUsage> landUsages = new ArrayList<>();
        landUsages.add(new LandUsage("forest", createGeometryForForest(), Colors.FOREST));
        landUsages.add(new LandUsage("residential", createGeometryForResidential(), Colors.RESIDENTIAL));
        landUsages.add(new LandUsage("vineyard", createGeometryForVineyard(), Colors.VINEYARD));
        landUsages.add(new LandUsage("grass", createGeometryForGrass(), Colors.GRASS));
        landUsages.add(new LandUsage("railway", createGeometryForRailway(), Colors.RAILWAY));

        return landUsages.toArray(new LandUsage[0]);
    }

    private Geometry createGeometryForForest() {
        GeometryFactory factory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(15.44, 47.07),
                new Coordinate(15.45, 47.07),
                new Coordinate(15.45, 47.06),
                new Coordinate(15.44, 47.06),
                new Coordinate(15.44, 47.07)  // Closed linear ring
        };
        return factory.createPolygon(coordinates);
    }

    private Geometry createGeometryForResidential() {
        GeometryFactory factory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(15.45, 47.07),
                new Coordinate(15.46, 47.07),
                new Coordinate(15.46, 47.06),
                new Coordinate(15.45, 47.06),
                new Coordinate(15.45, 47.07)
        };
        return factory.createPolygon(coordinates);
    }

    private Geometry createGeometryForVineyard() {
        GeometryFactory factory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(15.46, 47.07),
                new Coordinate(15.47, 47.07),
                new Coordinate(15.47, 47.06),
                new Coordinate(15.46, 47.06),
                new Coordinate(15.46, 47.07)
        };
        return factory.createPolygon(coordinates);
    }

    private Geometry createGeometryForGrass() {
        GeometryFactory factory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(15.44, 47.06),
                new Coordinate(15.45, 47.06),
                new Coordinate(15.45, 47.05),
                new Coordinate(15.44, 47.05),
                new Coordinate(15.44, 47.06)
        };
        return factory.createPolygon(coordinates);
    }

    private Geometry createGeometryForRailway() {
        GeometryFactory factory = new GeometryFactory();
        Coordinate[] coordinates = new Coordinate[] {
                new Coordinate(15.46, 47.06),
                new Coordinate(15.47, 47.06)
        };
        return factory.createLineString(coordinates);
    }
}

