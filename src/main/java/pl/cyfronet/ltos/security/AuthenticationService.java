package pl.cyfronet.ltos.security;

import com.agreemount.bean.identity.Identity;
import com.agreemount.bean.identity.provider.IdentityProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Service;
import pl.cyfronet.ltos.bean.User;
import pl.cyfronet.ltos.repository.UserRepository;
import pl.cyfronet.ltos.security.AuthenticationProviderDev.UserOperations;

import javax.net.ssl.HttpsURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthenticationService {
    private static String AUTHORITY_ROLE_PREFIX = "ROLE_";

    @Value("${unity.server.base}")
    private String authorizeUrl;

    @Value("${unity.server.userInfoAction}")
    private String userInfoAction;

    @Value("${hostname.verification}")
    private Boolean hostnameVerification;

    @Autowired
    private OAuth2RestOperations restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserOperations userOperations;

    @Autowired
    private IdentityProvider identityProvider;

    @Autowired
    private PortalUserFactory portalUserFactory;

    public void engineLogin(PortalUser user) {
        Identity identity = new Identity();
        identity.setLogin(user.getUserBean().getEmail());
        identity.setRoles(new ArrayList<>(user.getUserBean().getRoles().stream()
                .map(entry -> entry.getName())
                .collect(Collectors.toList())));
        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PROVIDER"))) {
            identity.getRoles().add("provider");
        }

        identityProvider.setIdentity(identity);
    }

    public PortalUser getPortalUser() {
        return getPortalUser(getUserInfo());
    }

    private PortalUser getPortalUser(UserInfo userInfo) {
        User user = getUser(userInfo);
        userInfo.setId(user.getId());

        PortalUserImpl.Data.DataBuilder builder = PortalUserImpl.Data.builder();
        builder.authenticated(true);
        builder.user(user);
        builder.authorities(user.getRoles().stream()
                .map(role -> AUTHORITY_ROLE_PREFIX + role.getName().toUpperCase(Locale.US))
                .map(name -> new SimpleGrantedAuthority(name))
                .collect(Collectors.toList()));
        builder.principal(userInfo);

        return portalUserFactory.createPortalUser(builder.build());
    }

    private UserInfo getUserInfo() {
        if (hostnameVerification.equals(false)) {
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> {
                log.warn("Hostname verification is disabled!!!");
                return true;
            });
        }

        return restTemplate.getForObject(authorizeUrl + userInfoAction, UserInfo.class);
    }

    private User getUser(UserInfo userInfo) {
        User user = userRepository.findByEmail(userInfo.getEmail());
        if (user == null) {
            user = User.builder().name(userInfo.getName()).email(userInfo.getEmail())
                    .organisationName(userInfo.getOrganisation_name())
                    .roles(Arrays.asList(userOperations.loadOrCreateRoleByName("manager"))).build();

            userRepository.save(user);
        }
        return user;
    }
}
