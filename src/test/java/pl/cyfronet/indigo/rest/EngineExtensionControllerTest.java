package pl.cyfronet.indigo.rest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import pl.cyfronet.indigo.bean.DocumentWeight;
import pl.cyfronet.indigo.bean.User;
import pl.cyfronet.indigo.controller.EngineExtensionController;
import pl.cyfronet.indigo.repository.DocumentWeightRepository;
import pl.cyfronet.indigo.repository.MockMvcSecurityTest;
import pl.cyfronet.indigo.repository.UserRepository;
import pl.cyfronet.indigo.security.PortalUser;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by marta on 7/19/17.
 */


public class EngineExtensionControllerTest  extends MockMvcSecurityTest {

    @InjectMocks
    private EngineExtensionController controller;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DocumentWeightRepository documentWeightRepository;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void getDocumentWeightsTest() throws Exception {

        User user = mock(User.class);
        PortalUser portalUser = mock(PortalUser.class);

        List<DocumentWeight> documentsWeights = getDocumentWeightList();

        when(portalUser.getUserBean()).thenReturn(user);
        when(user.getDocuments()).thenReturn(documentsWeights);

        List<DocumentWeight> result = controller.getDocumentWeights(portalUser);

        Assert.assertEquals(documentsWeights, result);
    }


    @Test
    public void setDocumentWeightsTest() throws Exception {


        PortalUser portalUserMock = mock(PortalUser.class);
        Long id = 11111L;
        User user = mock(User.class);

        List<DocumentWeight> documentWeights = getDocumentWeightList();
        when(user.getDocuments()).thenReturn(documentWeights);
        when(portalUserMock.getUserBean()).thenReturn(user);
        when(user.getId()).thenReturn(id);

        user.setDocuments(documentWeights);

        when(userRepository.findOne(user.getId())).thenReturn(user);
        controller.setDocumentWeights(portalUserMock, documentWeights);

    }


    private List<DocumentWeight> getDocumentWeightList(){

        DocumentWeight document = mock(DocumentWeight.class);
        List<DocumentWeight> docs = new ArrayList<>();
        docs.add(document);
        return docs;
    }




}
