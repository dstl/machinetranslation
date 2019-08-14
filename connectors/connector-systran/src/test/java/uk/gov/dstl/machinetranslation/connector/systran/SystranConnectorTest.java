package uk.gov.dstl.machinetranslation.connector.systran;

/*-
 * #%L
 * MT API Connector for SYSTRAN
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.systran.platform.translation.client.ApiException;
import net.systran.platform.translation.client.api.TranslationApi;
import net.systran.platform.translation.client.model.ErrorResponse;
import net.systran.platform.translation.client.model.SupportedLanguageResponse;
import net.systran.platform.translation.client.model.TranslationOutput;
import net.systran.platform.translation.client.model.TranslationResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorTestMethods;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

@ExtendWith(MockitoExtension.class)
public class SystranConnectorTest {

  @Mock TranslationApi mockApi;

  // TODO: Test configure

  @Test
  public void testSupportedLanguages() throws Exception {
    when(mockApi.translationSupportedLanguagesGet(any(), any(), any()))
        .thenReturn(createSupportedLanguageResponse("en", "it", "fr"));

    SystranConnector sc = new SystranConnector(mockApi);
    Collection<LanguagePair> pairs = sc.supportedLanguages();

    assertEquals(6, pairs.size());
    assertTrue(pairs.contains(new LanguagePair("en", "it")));
    assertTrue(pairs.contains(new LanguagePair("it", "en")));
    assertTrue(pairs.contains(new LanguagePair("en", "fr")));
    assertTrue(pairs.contains(new LanguagePair("fr", "en")));
    assertTrue(pairs.contains(new LanguagePair("fr", "it")));
    assertTrue(pairs.contains(new LanguagePair("it", "fr")));

    verify(mockApi, times(1)).translationSupportedLanguagesGet(any(), any(), any());
    verifyNoMoreInteractions(mockApi);
  }

  @Test
  public void testSupportedLanguagesException() throws Exception {
    when(mockApi.translationSupportedLanguagesGet(any(), any(), any()))
        .thenThrow(ApiException.class);

    SystranConnector sc = new SystranConnector(mockApi);
    assertThrows(ConnectorException.class, () -> sc.supportedLanguages());
  }

  @Test
  public void testIdentifyLanguage() {
    SystranConnector sc = new SystranConnector(mockApi);
    assertThrows(
        UnsupportedOperationException.class, () -> sc.identifyLanguage("Bonjour le monde"));

    verifyZeroInteractions(mockApi);
  }

  @Test
  public void testTranslate() throws Exception {
    when(mockApi.translationTextTranslateGet(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any()))
        .thenReturn(createTranslationResponse());

    SystranConnector sc = new SystranConnector(mockApi);

    Translation t = sc.translate("fr", "en", "Bonjour le monde");

    assertEquals("fr", t.getSourceLanguage());
    assertEquals("Hello world", t.getContent());

    verify(mockApi, times(1))
        .translationTextTranslateGet(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any());
    verifyNoMoreInteractions(mockApi);
  }

  @Test
  public void testTranslateException() throws Exception {
    when(mockApi.translationTextTranslateGet(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any()))
        .thenThrow(ApiException.class);

    SystranConnector sc = new SystranConnector(mockApi);
    assertThrows(ConnectorException.class, () -> sc.translate("fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testTranslateError() throws Exception {
    when(mockApi.translationTextTranslateGet(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any()))
        .thenReturn(createTranslationResponseError());

    SystranConnector sc = new SystranConnector(mockApi);
    assertThrows(ConnectorException.class, () -> sc.translate("fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testTranslateZeroOutput() throws Exception {
    when(mockApi.translationTextTranslateGet(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(),
            any()))
        .thenReturn(new TranslationResponse());

    SystranConnector sc = new SystranConnector(mockApi);
    assertThrows(ConnectorException.class, () -> sc.translate("fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testQueryEngine() {
    SystranConnector sc = new SystranConnector(mockApi);

    EngineDetails ed = sc.queryEngine();
    assertEquals("SYSTRAN", ed.getName());
    assertEquals(ConnectorUtils.VERSION_UNKNOWN, ed.getVersion());
    assertTrue(ed.isSupportedLanguagesSupported());
    assertFalse(ed.isIdentifyLanguageSupported());
    assertTrue(ed.isTranslateSupported());
  }

  @Test
  public void testSupported() {
    SystranConnector rc = new SystranConnector(mockApi);
    ConnectorTestMethods.testSupportedOperations(rc);
  }

  private SupportedLanguageResponse createSupportedLanguageResponse(String... languages) {
    List<net.systran.platform.translation.client.model.LanguagePair> pairs = new ArrayList<>();

    for (String l1 : languages) {
      for (String l2 : languages) {
        if (l1.equals(l2)) continue;

        net.systran.platform.translation.client.model.LanguagePair lp =
            new net.systran.platform.translation.client.model.LanguagePair();
        lp.setSource(l1);
        lp.setTarget(l2);

        pairs.add(lp);
      }
    }

    SupportedLanguageResponse slr = new SupportedLanguageResponse();
    slr.setLanguagePairs(pairs);

    return slr;
  }

  private TranslationResponse createTranslationResponse() {
    TranslationResponse tr = new TranslationResponse();

    TranslationOutput to = new TranslationOutput();
    to.setDetectedLanguage("fr");
    to.setOutput("Hello world");

    tr.setOutputs(Collections.singletonList(to));

    return tr;
  }

  private TranslationResponse createTranslationResponseError() {
    TranslationResponse tr = new TranslationResponse();

    ErrorResponse er = new ErrorResponse();
    er.setMessage("Test exception");

    tr.setError(er);

    return tr;
  }
}
