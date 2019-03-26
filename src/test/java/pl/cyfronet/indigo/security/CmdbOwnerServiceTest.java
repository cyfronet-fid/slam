package pl.cyfronet.indigo.security;

import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import pl.cyfronet.indigo.repository.CmdbAppDbRepository;
import pl.cyfronet.indigo.repository.CmdbRepository;

import java.util.Set;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class CmdbOwnerServiceTest {

    @Test
    @Ignore
    public void shouldReturnProviderSetForEmail() {
        CmdbAppDbRepository cmdbRepository = mockCmdbRepository();
        CmdbOwnerService service = new CmdbOwnerService(
                cmdbRepository,
                1000); // something large, so that the cache won't expire

        Set<String> providers1 = service.getOwnedProviders("1@owner");
        assertThat(providers1, Matchers.containsInAnyOrder("provider1", "provider2", "provider3"));

        Set<String> providers2 = service.getOwnedProviders("2@owner");
        assertThat(providers2, Matchers.containsInAnyOrder("provider1", "provider3"));

        Set<String> providers3 = service.getOwnedProviders("3@owner");
        assertThat(providers3, Matchers.containsInAnyOrder("provider2", "provider3"));
    }

    @Test
    @Ignore
    public void shouldCacheResponses() {
        CmdbAppDbRepository cmdbRepository = mockCmdbRepository();
        CmdbOwnerService service = new CmdbOwnerService(
                cmdbRepository,
                1000); // something large, so that the cache won't expire

        service.getOwnedProviders("1@owner");
        service.getOwnedProviders("3@owner");
        service.getOwnedProviders("2@owner");
        service.getOwnedProviders("1@owner");

        verify(cmdbRepository, times(1)).get("sites", "limit=10000");
    }

    @Test
    @Ignore
    public void shouldReloadWhenNeeded() {
        CmdbAppDbRepository cmdbRepository = mockCmdbRepository();
        CmdbOwnerService service = new CmdbOwnerService(
                cmdbRepository,
                1000); // something large, so that the cache won't expire

        service.getOwnedProviders("1@owner");
        service.invalidateCache();

        Set<String> providers1 = service.getOwnedProviders("1@owner");
        assertThat(providers1, Matchers.containsInAnyOrder("provider1", "provider3"));

        Set<String> providers2 = service.getOwnedProviders("2@owner");
        assertThat(providers2, Matchers.containsInAnyOrder("provider1", "provider2", "provider3"));

        verify(cmdbRepository, times(2)).get("provider", "include_docs=true");
    }

    private CmdbAppDbRepository mockCmdbRepository() {
        CmdbAppDbRepository cmdbRepository = mock(CmdbAppDbRepository.class);
        String provider1 = "{\"data\":{\"owners\":[\"1@owner\",\"2@owner\"]}}";
        String provider2inv1 = "{\"data\":{\"owners\":[\"1@owner\",\"3@owner\"]}}";
        String provider2inv2 = "{\"data\":{\"owners\":[\"2@owner\",\"3@owner\"]}}";
        String provider3 = "{\"data\":{\"owners\":[\"1@owner\",\"2@owner\",\"3@owner\"]}}";

        String repoProvidersReturn1 = "{\"rows\":[" +
                "{\"id\":\"provider1\",\"doc\":"+provider1+"}," +
                "{\"id\":\"provider2\",\"doc\":"+provider2inv1+"}," +
                "{\"id\":\"provider3\",\"doc\":"+provider3+"}" +
            "]}";
        String repoProvidersReturn2 = "{\"rows\":[" +
                "{\"id\":\"provider1\",\"doc\":"+provider1+"}," +
                "{\"id\":\"provider2\",\"doc\":"+provider2inv2+"}," +
                "{\"id\":\"provider3\",\"doc\":"+provider3+"}" +
            "]}";

        when(cmdbRepository.get("provider")).thenReturn(
                new JSONObject(repoProvidersReturn1),
                new JSONObject(repoProvidersReturn2)
        );
        return cmdbRepository;
    }
}
