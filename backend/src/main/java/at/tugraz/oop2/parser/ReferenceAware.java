package at.tugraz.oop2.parser;

import java.util.List;

public interface ReferenceAware {
    void addNodeReference(String nodeId);

    List<String> getReferences();

}
