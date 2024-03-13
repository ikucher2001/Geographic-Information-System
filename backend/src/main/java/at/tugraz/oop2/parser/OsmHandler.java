package at.tugraz.oop2.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class OsmHandler extends DefaultHandler {

    private static final String NODE = "node";
    private static final String WAY = "way";
    private static final String RELATION = "relation";
    private static final String TAG = "tag";
    private static final String NODE_REFERENCE = "nd";
    private static final String MEMBER = "member";

    private final List<OsmNode> nodes = new ArrayList<>();
    private final List<OsmWay> ways = new ArrayList<>();
    private final List<OsmRelation> relations = new ArrayList<>();

    OsmElement currentElement;

    @Override
    public void startElement(String uri, String lName, String qName, Attributes attr) throws SAXException {
        switch (qName) {
            case NODE:
                currentElement = OsmNode.builder()
                        .id(attr.getValue("id"))
                        .lon(attr.getValue("lon"))
                        .lat(attr.getValue("lat"))
                        .build();
                break;
            case WAY:
                currentElement = OsmWay.builder()
                        .id(attr.getValue("id"))
                        .build();
                break;
            case RELATION:
                currentElement = OsmRelation.builder()
                        .id(attr.getValue("id"))
                        .build();
                break;
            case TAG:
                currentElement.addTag(
                        attr.getValue("k"),
                        attr.getValue("v")
                );
                break;
            case NODE_REFERENCE:
                ((ReferenceAware) currentElement).addNodeReference(attr.getValue("ref"));
                break;
            case MEMBER:
                ((OsmRelation) currentElement).addMember(
                        attr.getValue("type"),
                        attr.getValue("ref"),
                        attr.getValue("role")
                );
                break;
            default:

        }
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case NODE:
                nodes.add((OsmNode) currentElement);
                break;
            case WAY:
                ways.add((OsmWay) currentElement);
                break;
            case RELATION:
                relations.add((OsmRelation) currentElement);
                break;
            default:

        }
    }

    public Collection<OsmNode> getNodes() {
        return nodes;
    }

    public Collection<OsmWay> getWays() {
        return ways;
    }

    public Collection<OsmRelation> getRelations() {
        return relations;
    }
}
