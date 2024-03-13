package at.tugraz.oop2;

import at.tugraz.oop2.errors.GrpcResponseException;
import io.grpc.StatusRuntimeException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static at.tugraz.oop2.MapApplication.blockingStub;

@RequestMapping("/roads")
@RestController
@CrossOrigin
public class RoadsController {

    @GetMapping("")
    public ResponseEntity<JSONObject> find(
            @RequestParam(name = "road", required = false) String road,
            @RequestParam(name = "bbox.tl.x", required = false) Double bboxTlX,
            @RequestParam(name = "bbox.tl.y", required = false) Double bboxTlY,
            @RequestParam(name = "bbox.br.x", required = false) Double bboxBrX,
            @RequestParam(name = "bbox.br.y", required = false) Double bboxBrY,
            @RequestParam(name = "take", required = false) Integer take,
            @RequestParam(name = "skip", required = false) Integer skip
    ) throws ParseException {
        Roads response = null;

        if (take == null) {
            take = MapApplication.TAKE_DEFAULT;
        }
        if (skip == null) {
            skip = MapApplication.SKIP_DEFAULT;
        }
        if (take < 0 || skip < 0) {
            throw new IllegalArgumentException("Invalid or missing parameters");
        }
        if (road == null) {
            road = "";
        }

        if (bboxTlX == null || bboxTlY == null || bboxBrX == null || bboxBrY == null) {
            throw new IllegalArgumentException("Invalid or missing parameters");
        }

        if (bboxTlX > 180 || bboxTlX < -180 || bboxTlY > 90 || bboxTlY < -90 || bboxBrX > 180 || bboxBrX < -180 || bboxBrY > 90 || bboxBrY < -90) {
            throw new IllegalArgumentException("Out of bounds");
        }

        try {
            ReqRoadMany req = ReqRoadMany.newBuilder()
                    .setRoad(road)
                    .setBoxes(Boxes.newBuilder()
                            .setTlX(bboxTlX)
                            .setTlY(bboxTlY)
                            .setBrX(bboxBrX)
                            .setBrY(bboxBrY)
                    )
                    .setTake(take)
                    .setSkip(skip)
                    .build();
            response = blockingStub.getRoads(req);
        } catch (StatusRuntimeException e) {
            new GrpcResponseException(e.getStatus().getCode(), 0);
        }

        JSONParser parser = new JSONParser();
        return ResponseEntity.ok((JSONObject) parser.parse(response.getResponse()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getRoadById(@PathVariable long id) throws ParseException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid ID provided");
        }

        RoadByID response = null;
        try {
            ReqById req = ReqById.newBuilder().setId(id).build();
            response = blockingStub.getRoadById(req);
        } catch (StatusRuntimeException e) {
            new GrpcResponseException(e.getStatus().getCode(), id);
        }

        JSONParser parser = new JSONParser();
        return ResponseEntity.ok((JSONObject) parser.parse(response.getResponse()));
    }


}
