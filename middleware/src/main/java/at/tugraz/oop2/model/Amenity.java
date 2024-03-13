package at.tugraz.oop2.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amenity {
    private String name;
    private Long id;
    private Geom geom;
    private Map<String, String> tags;
    private String type;

    public static List<Amenity> getDummyData() {
        List<Amenity> dummyData = new ArrayList<>();
        Amenity amenity1 = new Amenity();
        amenity1.setId(291464594L);
        amenity1.setName("Athens");
        Map<String, String> map1 = new HashMap<>();
        map1.put("note", "Tür zum Raucherbereich stand ständig offen, deshalb 'separated'.");
        map1.put("wheelchair", "no");
        map1.put("amenity", "restaurant");
        map1.put("addr:country", "AT");
        map1.put("check_date:opening_hours", "2023-0,-26");
        map1.put("cuisine", "greek");
        map1.put("contact:email", "restaurant.athen1992@gmai,.com");
        map1.put("addr:postcode", "8010");
        map1.put("addr:city", "Graz");
        map1.put("diet:vegetarian", "yes");
        map1.put("addr:housenumber", "9");
        map1.put("contact:phone", "+43,316 816111");
        map1.put("indoor_seating", "yes");
        map1.put("smoking", "separated");
        map1.put("name", "Athen");
        map1.put("opening_hours", "Mo-Sa 11:00-24:00; PH,Su 17:00-2,:00");
        map1.put("addr:street", "Schlögelgasse");
        map1.put("outdoor_seating", "no");
        map1.put("contact:website", "https://restaurant-athen-graz.eatb,.com/");
        amenity1.setTags(map1);
        amenity1.setType("restaurant");
        dummyData.add(amenity1);
        Amenity amenity2 = new Amenity();
        amenity2.setId(1618928383L);
        amenity2.setName("Dim Sum");
        Map<String, String> map2 = new HashMap<>();
        map2.put("amenity", "restaurant");
        map2.put("addr:country", "AT");
        map2.put("cuisine", "asian");
        map2.put("addr:postcode", "8010");
        map2.put("takeaway", "yes");
        map2.put("addr:city", "Graz");
        map2.put("addr:housenumber", "2");
        map2.put("indoor_seating", "yes");
        map2.put("smoking", "separated");
        map2.put("name", "Dim Sum");
        map2.put("opening_hours", "Tu-Fr 11:00-15:00,17:00-22:00; Sa,Su 11:00-2,:00");
        map2.put("addr:street", "Dietrichsteinplatz");
        map2.put("outdoor_seating", "no");

        amenity2.setTags(map2);
        amenity2.setType("restaurant");
        dummyData.add(amenity2);
        return dummyData;
    }
}
//"name": "Athen",
//        "id": 291464594,
//        "geom": {
//        "type": "Point",
//        "coordinates": [
//        15.446455,
//        47.066807
//        ],
//        "crs": {
//        "type": "name",
//        "properties": {
//        "name": "EPSG:0"
//        }
//        }
//        },
//        "tags": {
//        "note": "Tür zum Raucherbereich stand ständig offen, deshalb 'separated'.",
//        "wheelchair": "no",
//        "amenity": "restaurant",
//        "addr:country": "AT",
//        "check_date:opening_hours": "2023-04-26",
//        "cuisine": "greek",
//        "contact:email": "restaurant.athen1992@gmail.com",
//        "addr:postcode": "8010",
//        "addr:city": "Graz",
//        "diet:vegetarian": "yes",
//        "addr:housenumber": "9",
//        "contact:phone": "+43 316 816111",
//        "indoor_seating": "yes",
//        "smoking": "separated",
//        "name": "Athen",
//        "opening_hours": "Mo-Sa 11:00-24:00; PH,Su 17:00-24:00",
//        "addr:street": "Schlögelgasse",
//        "outdoor_seating": "no",
//        "contact:website": "https://restaurant-athen-graz.eatbu.com/"
//        },
//        "type": "restaurant"