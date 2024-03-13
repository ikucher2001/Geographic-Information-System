package at.tugraz.oop2;

import at.tugraz.oop2.errors.GrpcResponseException;
import io.grpc.StatusRuntimeException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static at.tugraz.oop2.MapApplication.blockingStub;

@RequestMapping("/amenities")
@RestController
@CrossOrigin
public class AmenitiesController {


    @GetMapping("")
    public ResponseEntity<JSONObject> find(
            @RequestParam(value = "point.x", required = false) Double pointX,
            @RequestParam(value = "point.y", required = false) Double pointY,
            @RequestParam(value = "point.d", required = false) Double pointD,
            @RequestParam(value = "bbox.tl.x", required = false) Double bboxTlX,
            @RequestParam(value = "bbox.tl.y", required = false) Double bboxTlY,
            @RequestParam(value = "bbox.br.x", required = false) Double bboxBrX,
            @RequestParam(value = "bbox.br.y", required = false) Double bboxBrY,
            @RequestParam(value = "take", required = false) Integer take,
            @RequestParam(value = "skip", required = false) Integer skip,
            @RequestParam(value = "amenity", required = false) String amenity
    ) throws ParseException {
        Amenities response = null;

        if (take == null) {
            take = MapApplication.TAKE_DEFAULT;
        }
        if (skip == null) {
            skip = MapApplication.SKIP_DEFAULT;
        }
        if(take < 0 || skip < 0){
            throw new IllegalArgumentException("Invalid or missing parameters");
        }
        if(amenity == null){
            amenity = "";
        }


        boolean point_set = pointX != null && pointY != null && pointD != null;
        boolean bbox_set = bboxTlX != null && bboxTlY != null && bboxBrX != null && bboxBrY != null;

        if (point_set) {
            if (pointX > 180 || pointX < -180 || pointY > 90 || pointY < -90 || pointD < 0) {
                throw new IllegalArgumentException("Out of bounds");
            }
        } else if (bbox_set) {
            if (bboxTlX > 180 || bboxTlX < -180 || bboxTlY > 90 || bboxTlY < -90 || bboxBrX > 180 || bboxBrX < -180 || bboxBrY > 90 || bboxBrY < -90) {
                throw new IllegalArgumentException("Out of bounds");
            }
        } else {
            throw new IllegalArgumentException("Invalid or missing parameters");
        }

        if (pointX != null && pointY != null && pointD != null) {
            try {
                ReqAmenityMany req = ReqAmenityMany.newBuilder()
                        .setAmenity(amenity)
                        .setPoints(Points.newBuilder()
                                .setPointX(pointX)
                                .setPointY(pointY)
                                .setPointD(pointD)
                        )
                        .setTake(take)
                        .setSkip(skip)
                        .build();
                response = blockingStub.getAmenities(req);
            } catch (StatusRuntimeException e) {
                new GrpcResponseException(e.getStatus().getCode(), 0);
            }
        } else if (bboxTlX != null && bboxTlY != null && bboxBrX != null && bboxBrY != null) {
            try {
                ReqAmenityMany req = ReqAmenityMany.newBuilder()
                        .setAmenity(amenity)
                        .setBoxes(Boxes.newBuilder()
                                .setTlX(bboxTlX)
                                .setTlY(bboxTlY)
                                .setBrX(bboxBrX)
                                .setBrY(bboxBrY)
                        )
                        .setTake(take)
                        .setSkip(skip)
                        .build();
                response = blockingStub.getAmenities(req);
            } catch (StatusRuntimeException e) {
                new GrpcResponseException(e.getStatus().getCode(), 0);
            }
        } else {
            throw new IllegalArgumentException("Invalid or missing parameters");
        }

        JSONParser parser = new JSONParser();

        return ResponseEntity.ok((JSONObject) parser.parse(response.getResponse()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<JSONObject> getAmenityById(@PathVariable long id) throws ParseException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid or missing parameters");
        }

        AmenityByID response = null;
        try {
            ReqById req = ReqById.newBuilder().setId(id).build();
            response = blockingStub.getAmenityById(req);
        } catch (StatusRuntimeException e) {
            new GrpcResponseException(e.getStatus().getCode(), id);
        }

        JSONParser parser = new JSONParser();

        return ResponseEntity.ok((JSONObject) parser.parse(response.getResponse()));
    }




}
