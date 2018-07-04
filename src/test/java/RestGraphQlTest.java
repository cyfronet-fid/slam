import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public class RestGraphQlTest {
    private static String query = "query{" +
            "  sites(filter: {services: {isInProduction: true}}) {" +
            "    totalCount" +
            "    items {" +
            "      name" +
            "      pkey" +
            "      services {" +
            "        totalCount" +
            "        items {" +
            "          endpointInterfaceName" +
            "          endpointURL" +
            "          endpointPKey" +
            "          isInProduction" +
            "        }" +
            "     }" +
            "   }" +
            " }" +
            "}";
    @Test
    public void test(){
        RestOperations rest = new RestTemplate();
        String url = "http://is.marie.hellasgrid.gr/graphql";
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("query", query);

        JSONArray result2 = new JSONObject(rest.postForObject(url, map, Map.class))
                .getJSONObject("data").getJSONObject("sites").getJSONArray("items");

        JSONArray result = result2;
    }

//    @Test
//    public void testOldDB(){
//        RestOperations rest = new RestTemplate();
//        String url = "http://indigo.cloud.plgrid.pl/cmdb";
//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("query", query);
//
//        JSONArray result2 = new JSONObject(rest.getForObject(url, map, Map.class))
//                .getJSONObject("data").getJSONObject("sites").getJSONArray("items");
//
//        JSONArray result = result2;
//    }
}