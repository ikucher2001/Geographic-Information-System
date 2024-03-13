package at.tugraz.oop2.tile;

import at.tugraz.oop2.jts.GeomBuilder;
import at.tugraz.oop2.parser.OsmData;
import at.tugraz.oop2.parser.OsmNode;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static at.tugraz.oop2.tile.Colors.getColor;

public class MapTileResolver {

    public byte[] getMapTile(int x, int y, int z, List<String> layers, OsmData osmData) {

        BufferedImage image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHints(Map.of(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
        graphics.setColor(Colors.BACKGROUND);
        graphics.fillRect(0, 0, 512, 512);
        BoundingBox bbox = new BoundingBox(x, y, z);

        layers.forEach(layer -> {
            roads(graphics, bbox, osmData, layer);
            amenities(graphics, bbox, osmData, layer);
        });

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return stream.toByteArray();
    }

    private void amenities(Graphics2D graphics, BoundingBox bbox, OsmData osmData, String layer) {

    }

    private void roads(Graphics2D graphics, BoundingBox bbox, OsmData osmData, String roadTag) {
        final Geometry area;
        try {
            area = GeomBuilder.calculateZoneFromBbox
                    (bbox.getWest(), bbox.getNorth(), bbox.getEast(), bbox.getSouth());
        } catch (FactoryException | TransformException e) {
            return;
        }

        List<LineString> found_ways = createRoads(osmData, roadTag, area);

        found_ways.forEach(way -> {
            graphics.setColor(getColor(roadTag));
            graphics.setStroke(new BasicStroke(roadTag.equals("motorway") ? 3 : 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));


        });

    }


    private static List<LineString> createRoads(OsmData osmData, String roadTag, Geometry area) {
        return osmData.getOsmWaysCollection()
                .stream()
                .filter(way -> way.getTags() != null && way.getTags().containsKey("highway") && way.getTags().get("highway").equals(roadTag))
                .map(way -> {
                    ArrayList<OsmNode> way_nodes = new ArrayList<>();

                    for (String id : way.getReferences()) {
                        OsmNode node = osmData.getNode(id);
                        if (node != null) {
                            way_nodes.add(node);
                        }
                    }
                    if (way_nodes.size() < 2) {
                        return null;
                    }

                    LineString way_transformed = GeomBuilder.buildLineStringObj(way_nodes);
                    if (area.contains(way_transformed) || area.intersects(way_transformed)) {
                        return GeomBuilder.buildLineStringObj(way_nodes);
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

}
