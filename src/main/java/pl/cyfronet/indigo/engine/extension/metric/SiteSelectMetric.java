package pl.cyfronet.indigo.engine.extension.metric;

import com.agreemount.bean.metric.Metric;
import com.agreemount.bean.metric.MetricOption;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.PropertySource;
import pl.cyfronet.indigo.repository.CmdbAppDbRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@lombok.Getter
@lombok.Setter
@lombok.ToString
@Slf4j
@lombok.EqualsAndHashCode(callSuper = false)
@PropertySource("classpath:application.properties")
public class SiteSelectMetric extends Metric<String> {
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

    public SiteSelectMetric() {
        super(InputType.SELECT);
    }

    public enum SiteType {compute, storage}

    private SiteType siteType;
    public String cmdbUrl;

    public static CmdbAppDbRepository cmdbRepository;

    public List<MetricOption> getOptions() {
        return getOptionsByGraphQl();
//        return getOptionsByRest();
    }

//    public List<MetricOption> getOptionsByRest() {
//        JSONArray sites = null;
//        JSONArray services = null;
//        ArrayList<MetricOption> ret = new ArrayList<>();
//        try {
//            sites = cmdbRepository.get("service", "type", siteType.toString()).getJSONArray("rows");
//            sites = cmdbRepository.get("sites", null).getJSONObject("data").getJSONArray("items");
//            services = cmdbRepository.get("sites", null).getJSONObject("data").getJSONArray("items");
//        } catch (Exception e) {
//            //@TODO@ - provide message to frontend
//            e.printStackTrace();
//            return ret;
//        }
//
//        for(int i=0; i < sites.length(); ++i) {
//            MetricOption opt = new MetricOption();
//            JSONObject site = sites.getJSONObject(i);
//
//            Map<String, Object> attrs = new HashMap<>();
//
//            String sitename = site.has("name") ? site.getString("name") : null;
////            String sitename = site.getJSONObject("value").has("sitename") ? site.getJSONObject("value").getString("sitename") : null;
//            String hostname = null;
//
//            attrs.put("sitename",  sitename);
////            attrs.put("hostname",  hostname);
//            attrs.put("label",  (sitename != null ? sitename : "?") + " : " + (hostname != null ? hostname : "?"));
//            opt.setAttributes(attrs);
//            opt.setValue(site.getString("id"));
//            ret.add(opt);
//        }
//
//        return ret;
//    }

    public List<MetricOption> getOptionsByGraphQl() {
        JSONArray sites = null;
        JSONArray services = null;
        ArrayList<MetricOption> ret = new ArrayList<>();
        try {
            sites = cmdbRepository.post(query)
                    .getJSONObject("data").getJSONObject("sites").getJSONArray("items");
        } catch (Exception e) {
            //@TODO@ - provide message to frontend
            e.printStackTrace();
            log.error("WAZNE"+ e.getLocalizedMessage() + " | "+ e.getCause());
            return ret;
        }

        for(int i=0; i < sites.length(); ++i) {
            MetricOption opt = new MetricOption();
            JSONObject site = sites.getJSONObject(i);

            Map<String, Object> attrs = new HashMap<>();

            String sitename = site.has("name") ? site.getString("name") : null;

            for(int j=0; j < site.getJSONObject("services").getJSONArray("items").length(); ++j ){
                JSONObject service = site.getJSONObject("services").getJSONArray("items").getJSONObject(j);
                attrs.put("sitename",  sitename);
                String hostname = service.has("endpointURL") ? service.getString("endpointURL") : null;
                attrs.put("hostname",  hostname);
                attrs.put("label",  (sitename != null ? sitename : "?") + " : " + (hostname != null ? hostname : "?"));
                opt.setAttributes(attrs);
                // this was id from couchdb
//                opt.setValue(site.getString("id"));
                //this is service primarykey
                opt.setValue(site.getString("pkey"));
                ret.add(opt);
            }
        }

        return ret;
    }
}