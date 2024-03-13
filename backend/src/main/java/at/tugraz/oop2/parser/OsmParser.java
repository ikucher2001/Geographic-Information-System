package at.tugraz.oop2.parser;

import at.tugraz.oop2.MapLogger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


public class OsmParser {

    public static OsmData run(String osmFile) {

        OsmHandler osmHandler = new OsmHandler();

        File osm_file = new File(osmFile);

        if (!osm_file.exists() || !osm_file.canRead()) {
            throw new IllegalStateException("invalid osm file");
        }

        long startTime = System.currentTimeMillis();

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            saxParser.parse(osm_file, osmHandler);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }

        long endTime = System.currentTimeMillis();

//        osmHandler.getNodes().forEach(System.out::println);
//        osmHandler.getWays().forEach(System.out::println);
//        osmHandler.getRelations().forEach(System.out::println);

//        System.out.println("Processing took: " + (endTime - startTime) / 1000.0 + "sec.");


        Set<String> referencedNodes = osmHandler.getWays()
                .stream()
                .flatMap(way -> way.getReferences().stream())
                .collect(Collectors.toSet());

        Set<String> waysMembersOfRelations = osmHandler.getRelations()
                .stream()
                .flatMap(relation -> relation.getMembers()
                        .stream()
                        .filter(member -> member.getType().equals("way"))
                        .map(OsmMember::getRef)
                )
                .collect(Collectors.toSet());


        long notReferencedNodes = osmHandler.getNodes()
                .stream()
                .filter(node -> !referencedNodes.contains(node.getId()))
                .count();

        long unreferencedWays = osmHandler.getWays().stream()
                .filter(way -> !waysMembersOfRelations.contains(way.getId()))
                .count();

        MapLogger.backendLoadFinished(notReferencedNodes, unreferencedWays, osmHandler.getRelations().size());

        return new OsmData(osmHandler.getWays(), osmHandler.getNodes(), osmHandler.getRelations());
    }

}

