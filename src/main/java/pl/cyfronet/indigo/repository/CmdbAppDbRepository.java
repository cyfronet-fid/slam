package pl.cyfronet.indigo.repository;

import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import pl.cyfronet.indigo.engine.extension.metric.SiteSelectMetric;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@Log4j
public class CmdbAppDbRepository {
    @Value("${cmdb.appdb.url}")
    private String cmdbUrl;

    @Value("/rest")
    private String prefix;

    @Autowired
    private OAuth2RestOperations restTemplate;
//    private RestOperations restTemplate;

    public JSONObject get(String type, String fieldName, String fieldValue) {
//        String url = cmdbUrl + prefix + "/"+ type + "/filters/" + fieldName + "/" + fieldValue;
        String url = cmdbUrl + prefix + "/" + type + "/" + fieldName + "/" + fieldValue;
        log.debug("Calling " + url);
        return new JSONObject(restTemplate
                .getForObject(url, Map.class));
    }

    public JSONObject get(String type) {
        return get(type, null);
    }

    public JSONObject get(String type, String params) {
        String url = cmdbUrl + prefix + "/" + type;
        if (params != null) {
            url = url + "?" + params;
        }
        log.debug("Calling " + url);
        return new JSONObject(restTemplate.getForObject(url, Map.class));
    }

    public JSONObject post(String query) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("query", query);
        String url = cmdbUrl + "/graphql";
        return new JSONObject(restTemplate.postForObject(url, map, Map.class));
    }

    public JSONObject getById(String type, String id) {
        String url = cmdbUrl + prefix + "/" + type + "/id/" + id;
        log.debug("Calling " + url);
        return new JSONObject(restTemplate.getForObject(cmdbUrl + prefix + "/" + type + "/id/" + id, Map.class));
    }

    @PostConstruct
    private void injectIntoEngine() {
        SiteSelectMetric.cmdbRepository = this;
//        IsPublicServiceImpl.cmdbRepository = this;
    }
}
