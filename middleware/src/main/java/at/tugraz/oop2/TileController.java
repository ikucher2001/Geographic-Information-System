package at.tugraz.oop2;

import at.tugraz.oop2.errors.GrpcResponseException;
import at.tugraz.oop2.utils.BoundingBox;
import at.tugraz.oop2.utils.Colors;
import io.grpc.StatusRuntimeException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static at.tugraz.oop2.MapApplication.blockingStub;

@RequestMapping("/tile")
@RestController
@CrossOrigin
public class TileController {

    @GetMapping(value = "/{z}/{x}/{y}", produces = "image/png")
    public BufferedImage createMap(
            @PathVariable int z,
            @PathVariable int x,
            @PathVariable int y,
            @RequestParam(name = "layers", required = false, defaultValue = "motorway") String layers
    ) {

        if (x <= 0 || y <= 0 || z <= 0) {
            throw new IllegalArgumentException("x, y and z must be greater than 0");
        }

        try {
            ReqMapTile req = ReqMapTile.newBuilder().setX(x).setY(y).setZ(z).build();
            MapTile response = blockingStub.getMapTile(req);
            byte[] bytes = response.getResponse().toByteArray();
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(bis);
            return image;

        } catch (StatusRuntimeException e) {
            new GrpcResponseException(e.getStatus().getCode(), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}