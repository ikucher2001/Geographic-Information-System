package at.tugraz.oop2;

import at.tugraz.oop2.errors.InternalServerErrorException;
import at.tugraz.oop2.utils.GeometryUtils;
import at.tugraz.oop2.utils.LandUsage;
import org.json.simple.JSONObject;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/usage")
public class UsageController {

    private final LandUsageService landUsageService;

    @Autowired
    public UsageController(LandUsageService landUsageService) {
        this.landUsageService = landUsageService;
    }

    @GetMapping("")
    public ResponseEntity<JSONObject> calculateLandUsage(
            @RequestParam(name = "bbox.tl.x") Double bboxTlX,
            @RequestParam(name = "bbox.tl.y") Double bboxTlY,
            @RequestParam(name = "bbox.br.x") Double bboxBrX,
            @RequestParam(name = "bbox.br.y") Double bboxBrY) {
        try {
            if (bboxBrX == null || bboxBrY == null || bboxTlX == null || bboxTlY == null) {
                throw new IllegalArgumentException("Invalid or missing parameters");
            }

            if (bboxTlX > 180 || bboxTlX < -180 || bboxTlY > 90 || bboxTlY < -90 || bboxBrX > 180 || bboxBrX < -180 || bboxBrY > 90 || bboxBrY < -90) {
                throw new IllegalArgumentException("Out of bounds");
            }

            Polygon bboxGeometry4326 = GeometryUtils.calculateAreaFromBbox(bboxTlX, bboxTlY, bboxBrX, bboxBrY);
            // Transform bounding box to EPSG:31256
            Geometry transformedBbox = GeometryUtils.ConvertEPGS(bboxGeometry4326);

            double totalArea = transformedBbox.getArea();

            LandUsage[] landUsages = landUsageService.getLandUsages();
            Map<String, Double> usageAreas = new HashMap<>();

            for (LandUsage usage : landUsages) {
                // Transform land usage geometry coordinates to EPSG:31256
                Geometry transformedGeom = GeometryUtils.ConvertEPGS(usage.getGeometry());
                Geometry intersection = GeometryUtils.intersectGeometries(transformedGeom, transformedBbox);
                if (!intersection.isEmpty()) {
                    double intersectedArea = intersection.getArea();
                    usageAreas.put(usage.getType(), intersectedArea);
                }
            }

            List<Map<String, Object>> usageStats = new ArrayList<>();
              for (Map.Entry<String, Double> entry : usageAreas.entrySet()) {
                double share = (totalArea > 0) ? entry.getValue() / totalArea : 0;
                Map<String, Object> stat = new LinkedHashMap<>();
                stat.put("type", entry.getKey());
                stat.put("share", share);
                stat.put("area", entry.getValue());
                usageStats.add(stat);
             }

            usageStats.sort((a, b) -> Double.compare((double) b.get("share"), (double) a.get("share")));
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("area", totalArea);
            response.put("usages", usageStats);

            return ResponseEntity.ok(new JSONObject(response));

        } catch (Exception e) {
            e.printStackTrace(); //for debugging
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}


