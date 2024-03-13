package at.tugraz.oop2.parser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class OsmMember {

    private String type;
    private String ref;
    private String role;

}
