package pl.cyfronet.indigo.engine.extension.component;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import pl.cyfronet.indigo.engine.extension.bean.Site;
import pl.cyfronet.indigo.repository.CmdbAppDbRepository;
import pl.cyfronet.indigo.repository.CmdbRepository;

import java.util.HashMap;

/**
 * Created by mszostak on 06.04.17.
 */

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SitesService {

    @Autowired
    private CmdbAppDbRepository cmdbRepository;

    private HashMap<String, Site> sites = null;

    public HashMap<String, Site> getSites() {
        if(sites != null)
            return sites;

        sites = new HashMap<>();

        try {
            JSONArray sites = cmdbRepository.get("sites").getJSONObject("data").getJSONArray("items");
            for(int i = 0; i < sites.length(); ++i ) {
                JSONObject site = sites.getJSONObject(i);
                this.sites.put(site.getString("pkey"),
                               Site.builder().id(site.getString("pkey"))
                               .name(site.has("name") ? site.getString("name") : "")
                               .build());
            }
            return this.sites;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public String getSiteName(String siteId) {
        if(siteId == null)
            return null;
        return getSites().get(siteId).getName();
    }
}
