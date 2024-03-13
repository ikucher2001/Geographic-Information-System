package at.tugraz.oop2.config;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Getter
public class RoadsMany {
    private final ArrayList<Road> entries;
    private final Map<String, Integer> paging;

    public RoadsMany(){
        this.entries = new ArrayList<>();
        this.paging = new HashMap<>();
    }

    public void addNewEntrie(Road road){
        this.entries.add(road);
    }

    public void setPaging(int skip, int take, int total){
        this.paging.put("skip", skip);
        this.paging.put("take", take);
        this.paging.put("total", total);
    }

}
