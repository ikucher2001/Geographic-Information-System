package at.tugraz.oop2.utilities;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
public class QueryParameterParser {
    public static MultiValueMap<String, String> parseParameters(String queryString) {
        // Create an empty MultiValueMap to store the parameters
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        // Use UriComponentsBuilder to parse the query string
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(queryString).build();

        // Extract query parameters and add them to the MultiValueMap
        uriComponents.getQueryParams().forEach((key, values) -> parameters.put(key, values));

        return parameters;
    }

}
