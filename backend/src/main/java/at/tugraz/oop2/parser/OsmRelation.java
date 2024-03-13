package at.tugraz.oop2.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;


@ToString(callSuper = true)
@Getter
public class OsmRelation extends OsmElement {

    private final List<OsmMember> members;

    @Builder
    public OsmRelation(String id) {
        super(id);
        members = new ArrayList<>();
    }

    public void addMember(String type, String ref, String role) {
        members.add(OsmMember.builder()
                .type(type)
                .ref(ref)
                .role(role)
                .build());
    }


}

