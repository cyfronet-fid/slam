package pl.cyfronet.indigo.engine;

import com.agreemount.bean.document.Document;
import com.agreemount.slaneg.action.ActionContext;
import com.agreemount.slaneg.constraint.action.impl.QualifierImpl;
import org.junit.Test;
import org.mockito.Mockito;
import pl.cyfronet.indigo.engine.extension.constraint.action.definition.HasAnyOfMetrics;
import pl.cyfronet.indigo.engine.extension.constraint.action.impl.HasAnyOfMetricsImpl;

import java.util.Arrays;
import java.util.HashMap;


public class HasAnyOfMetricsImplTest {

  @Test
  public void testWithoutMetrics() throws Exception {
      HasAnyOfMetricsImpl spy = Mockito.spy(new HasAnyOfMetricsImpl());
      Mockito.mock(QualifierImpl.class);
      HasAnyOfMetrics metrics = new HasAnyOfMetrics();        
      Document document = new Document();
      metrics.setMetrics(Arrays.asList("sampleMetric"));
      ActionContext<Document> actionContext = new ActionContext<Document>(document);
      Mockito.doReturn(metrics).when((QualifierImpl)spy).getConstraintDefinition();
      Mockito.doReturn(actionContext).when((QualifierImpl)spy).getActionContext();
      spy.isAvailable();
  }
  
  @Test
  public void testWithMetrics() throws Exception {
      HasAnyOfMetricsImpl spy = Mockito.spy(new HasAnyOfMetricsImpl());
      Mockito.mock(QualifierImpl.class);
      HasAnyOfMetrics metrics = new HasAnyOfMetrics();        
      Document document = new Document();
      metrics.setMetrics(Arrays.asList("sampleMetric"));
      HashMap<String, Object> documentMetrics = new HashMap<String, Object>();
      documentMetrics.put("sampleMetric", "sampleMetricValue");
      document.setMetrics(documentMetrics);
      ActionContext<Document> actionContext = new ActionContext<Document>(document);
      Mockito.doReturn(metrics).when((QualifierImpl)spy).getConstraintDefinition();
      Mockito.doReturn(actionContext).when((QualifierImpl)spy).getActionContext();
      spy.isAvailable();
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testWithoutAlias() throws Exception {
      HasAnyOfMetricsImpl spy = Mockito.spy(new HasAnyOfMetricsImpl());
      Mockito.mock(QualifierImpl.class);
      HasAnyOfMetrics metrics = new HasAnyOfMetrics();        
      Document document = new Document();
      metrics.setDocumentAlias("");
      HashMap<String, Object> documentMetrics = new HashMap<String, Object>();
      documentMetrics.put("sampleMetric", "sampleMetricValue");
      document.setMetrics(documentMetrics);
      ActionContext<Document> actionContext = new ActionContext<Document>(document);
      Mockito.doReturn(metrics).when((QualifierImpl)spy).getConstraintDefinition();
      spy.isAvailable();
  }
  
  @Test(expected = NullPointerException.class)
  public void testWithNullDocument() throws Exception {
      HasAnyOfMetricsImpl spy = Mockito.spy(new HasAnyOfMetricsImpl());
      Mockito.mock(QualifierImpl.class);
      HasAnyOfMetrics metrics = new HasAnyOfMetrics();        
      Document document = new Document();
      HashMap<String, Object> documentMetrics = new HashMap<String, Object>();
      documentMetrics.put("sampleMetric", "sampleMetricValue");
      document.setMetrics(documentMetrics);
      ActionContext actionContextMock = Mockito.mock(ActionContext.class);
      Mockito.doReturn(metrics).when((QualifierImpl)spy).getConstraintDefinition();
      Mockito.doReturn(actionContextMock).when((QualifierImpl)spy).getActionContext();
      spy.isAvailable();
  }

}
