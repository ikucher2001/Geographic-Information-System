package at.tugraz.oop2;

import at.tugraz.oop2.errors.InternalServerErrorException;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import at.tugraz.oop2.model.Route;

@RequestMapping("/route")
@RestController
@CrossOrigin
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("")
    public ResponseEntity<Route> getRoute(
            @RequestParam(value = "from", required = true) long from,
            @RequestParam(value = "to", required = true) long to,
            @RequestParam(value = "weighting", required = false, defaultValue = "length") String weighting
    ) {
        if (from == 0 || to == 0) {
            throw new IllegalArgumentException("Invalid or missing parameters");
        }

        if (weighting != "time" || weighting != "length") {
            weighting = "length";
        }
        try {
            Route route = routeService.calculateRoute(from, to, weighting);
            return ResponseEntity.ok(route);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Log the exception for debugging
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}