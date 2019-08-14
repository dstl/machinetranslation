package uk.gov.dstl.machinetranslation.connector.remedi;

/*-
 * #%L
 * MT API Connector for REMEDI
 * %%
 * Copyright (C) 2019 Dstl
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorTestMethods;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;
import uk.gov.nca.remedi4j.client.RemediClient;
import uk.gov.nca.remedi4j.data.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RemediConnectorTest {

  @Mock RemediClient mockClient;

  private URI wsProcessor = new URI("ws://localhost:9080");
  private URI wsTranslator = new URI("ws://localhost:9090");

  public RemediConnectorTest() throws URISyntaxException {
    // Default constructor with exception required to create URIs
  }

  @Test
  public void testConfigureEmpty() throws ConfigurationException {
    // Simple test just to check it doesn't error
    RemediConnector rc = new RemediConnector();
    rc.configure(Collections.emptyMap());
  }

  @Test
  public void testSupportedLanguages() throws ConnectorException {
    configureMock();

    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);
    Collection<LanguagePair> languagePairs = rc.supportedLanguages(mockClient);

    verify(mockClient, times(1)).getSupportedLanguages();
    verifyNoMoreInteractions(mockClient);

    assertEquals(3, languagePairs.size());
    assertTrue(languagePairs.contains(new LanguagePair("fr", "en")));
    assertTrue(languagePairs.contains(new LanguagePair("de", "en")));
    assertTrue(languagePairs.contains(new LanguagePair("de", "fr")));
  }

  @Test
  public void testIdentifyLanguage() throws ConfigurationException {
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);

    assertThrows(
        UnsupportedOperationException.class, () -> rc.identifyLanguage("Bonjour le monde"));
  }

  @Test
  public void testTranslateFr() throws ConnectorException {
    configureMock();
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);

    Translation t = rc.translate(mockClient, "fr", "en", "Bonjour le monde");

    verify(mockClient, times(1)).getSupportedLanguages();
    verify(mockClient, times(1)).preProcess("fr", "Bonjour le monde");
    verify(mockClient, times(1)).translate("fr", "en", "bonjour le monde");
    verify(mockClient, times(1)).postProcess("en", "hello world");
    verifyNoMoreInteractions(mockClient);

    assertEquals("Hello world", t.getContent());
    assertEquals("fr", t.getSourceLanguage());
  }

  @Test
  public void testTranslateAuto() throws ConnectorException {
    configureMock();
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);

    Translation t =
        rc.translate(mockClient, ConnectorUtils.LANGUAGE_AUTO, "en", "Bonjour le monde");

    // Get supported languages not called
    verify(mockClient, times(1)).preProcess(ConnectorUtils.LANGUAGE_AUTO, "Bonjour le monde");
    verify(mockClient, times(1)).translate("fr", "en", "bonjour le monde");
    verify(mockClient, times(1)).postProcess("en", "hello world");
    verifyNoMoreInteractions(mockClient);

    assertEquals("Hello world", t.getContent());
    assertEquals("fr", t.getSourceLanguage());
  }

  @Test
  public void testTranslateAutoNoPreProcess() throws ConfigurationException {
    configureMock();
    RemediConnector rc = new RemediConnector(null, wsTranslator, wsProcessor);

    assertThrows(
        UnsupportedOperationException.class,
        () -> rc.translate(mockClient, ConnectorUtils.LANGUAGE_AUTO, "en", "Bonjour le monde"));
  }

  @Test
  public void testTranslateDeNoPreProcess() throws ConnectorException {
    configureMock();
    RemediConnector rc = new RemediConnector(null, wsTranslator, wsProcessor);

    Translation t = rc.translate(mockClient, "de", "en", "hallo welt");

    verify(mockClient, times(1)).getSupportedLanguages();
    verify(mockClient, times(1)).translate("de", "en", "hallo welt");
    verify(mockClient, times(1)).postProcess("en", "hello world");
    verifyNoMoreInteractions(mockClient);

    assertEquals("Hello world", t.getContent());
    assertEquals("de", t.getSourceLanguage());
  }

  @Test
  public void testTranslateDeNoPostProcess() throws ConnectorException {
    configureMock();
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, null);

    Translation t = rc.translate(mockClient, "de", "en", "Hallo welt");

    verify(mockClient, times(1)).getSupportedLanguages();
    verify(mockClient, times(1)).preProcess("de", "Hallo welt");
    verify(mockClient, times(1)).translate("de", "en", "hallo welt");
    verifyNoMoreInteractions(mockClient);

    assertEquals("hello world", t.getContent());
    assertEquals("de", t.getSourceLanguage());
  }

  @Test
  public void testTranslateDeTranslateOnly() throws ConnectorException {
    configureMock();
    RemediConnector rc = new RemediConnector(wsTranslator);

    Translation t = rc.translate(mockClient, "de", "en", "hallo welt");

    verify(mockClient, times(1)).getSupportedLanguages();
    verify(mockClient, times(1)).translate("de", "en", "hallo welt");
    verifyNoMoreInteractions(mockClient);

    assertEquals("hello world", t.getContent());
    assertEquals("de", t.getSourceLanguage());
  }

  @Test
  public void testTranslateFrBadResultPre() throws ConfigurationException {
    configureMock(true, false, false);
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);

    assertThrows(
        ConnectorException.class, () -> rc.translate(mockClient, "fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testTranslateFrBadResultTranslate() throws ConfigurationException {
    configureMock(false, true, false);
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);

    assertThrows(
        ConnectorException.class, () -> rc.translate(mockClient, "fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testTranslateFrBadResultPost() throws ConfigurationException {
    configureMock(false, false, true);
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);

    assertThrows(
        ConnectorException.class, () -> rc.translate(mockClient, "fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testTranslateUnsupported() throws ConfigurationException {
    configureMock();
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);

    assertThrows(
        ConnectorException.class, () -> rc.translate(mockClient, "it", "en", "Ciao mondo"));
  }

  @Test
  public void testQueryEngine() throws ConfigurationException {
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);
    EngineDetails details = rc.queryEngine();

    assertNotNull(details);
    assertEquals("REMEDI", details.getName());
    assertEquals(ConnectorUtils.VERSION_UNKNOWN, details.getVersion());
    assertTrue(details.isSupportedLanguagesSupported());
    assertFalse(details.isIdentifyLanguageSupported());
    assertTrue(details.isTranslateSupported());
  }

  @Test
  public void testSupported() throws ConfigurationException {
    RemediConnector rc = new RemediConnector(wsProcessor, wsTranslator, wsProcessor);
    ConnectorTestMethods.testSupportedOperations(rc);
  }

  private void configureMock() {
    configureMock(false, false, false);
  }

  private void configureMock(boolean badStatusPre, boolean badStatusTrans, boolean badStatusPost) {
    // Supported Languages
    Map<String, Set<String>> languages = new HashMap<>();
    languages.put("fr", new HashSet<>(Collections.singletonList("en")));
    languages.put("de", new HashSet<>(Arrays.asList("en", "fr")));

    when(mockClient.getSupportedLanguages())
        .thenReturn(CompletableFuture.completedFuture(languages));

    // Pre Processor
    PreProcessorResponse preResponseFrench = new PreProcessorResponse();
    preResponseFrench.setLanguage("fr");
    preResponseFrench.setText("bonjour le monde");
    if (badStatusPre) {
      preResponseFrench.setStatusCode(StatusCode.RESULT_ERROR);
    } else {
      preResponseFrench.setStatusCode(StatusCode.RESULT_OK);
    }

    when(mockClient.preProcess(eq(ConnectorUtils.LANGUAGE_AUTO), anyString()))
        .thenReturn(CompletableFuture.completedFuture(preResponseFrench));
    when(mockClient.preProcess(eq("fr"), anyString()))
        .thenReturn(CompletableFuture.completedFuture(preResponseFrench));

    PreProcessorResponse preResponseGerman = new PreProcessorResponse();
    preResponseGerman.setLanguage("de");
    preResponseGerman.setText("hallo welt");
    if (badStatusPre) {
      preResponseGerman.setStatusCode(StatusCode.RESULT_ERROR);
    } else {
      preResponseGerman.setStatusCode(StatusCode.RESULT_OK);
    }

    when(mockClient.preProcess(eq("de"), anyString()))
        .thenReturn(CompletableFuture.completedFuture(preResponseGerman));

    // Translate
    TargetData td = new TargetData();
    td.setStatusCode(StatusCode.RESULT_OK);
    td.setTranslatedText("hello world");

    TranslationResponse tr = new TranslationResponse();
    tr.setTargetData(Collections.singletonList(td));
    if (badStatusTrans) {
      tr.setStatusCode(StatusCode.RESULT_ERROR);
    } else {
      tr.setStatusCode(StatusCode.RESULT_OK);
    }

    when(mockClient.translate(anyString(), anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(tr));

    // Post Processing
    PostProcessorResponse postResponse = new PostProcessorResponse();
    postResponse.setText("Hello world");
    postResponse.setLanguage("en");
    if (badStatusPost) {
      postResponse.setStatusCode(StatusCode.RESULT_ERROR);
    } else {
      postResponse.setStatusCode(StatusCode.RESULT_OK);
    }

    when(mockClient.postProcess(anyString(), anyString()))
        .thenReturn(CompletableFuture.completedFuture(postResponse));
  }
}
