package at.tugraz.oop2.config;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class AmenitiesMany {
    private final ArrayList<Amenity> entries;
    private final Map<String, Integer> paging;

    public AmenitiesMany(){
        this.entries = new ArrayList<>();
        this.paging = new HashMap<>();
    }

    public void addNewEntrie(Amenity amenity){
        this.entries.add(amenity);
    }

    public void setPaging(int skip, int take, int total){
        this.paging.put("skip", skip);
        this.paging.put("take", take);
        this.paging.put("total", total);
    }
}
