package uk.gov.dstl.machinetranslation.connector.aws;

/*-
 * #%L
 * MT API Connector for AWS Translate
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

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.model.DetectDominantLanguageResult;
import com.amazonaws.services.comprehend.model.DominantLanguage;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.model.TranslateTextResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.LanguageDetection;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorTestMethods;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

@ExtendWith(MockitoExtension.class)
public class AwsConnectorTest {

  @Mock AmazonTranslate mockTranslate;

  @Mock AmazonComprehend mockComprehend;

  @Test
  public void testSupportedLanguages() {
    AwsConnector ac = new AwsConnector(mockTranslate, mockComprehend);

    assertThrows(UnsupportedOperationException.class, ac::supportedLanguages);
  }

  @Test
  public void testIdentifyLanguage() throws ConnectorException {
    when(mockComprehend.detectDominantLanguage(any()))
        .thenReturn(
            new DetectDominantLanguageResult()
                .withLanguages(
                    new DominantLanguage().withLanguageCode("fr").withScore(0.8f),
                    new DominantLanguage().withLanguageCode("de").withScore(0.2f),
                    new DominantLanguage().withLanguageCode("ch").withScore(0.5f)));

    AwsConnector ac = new AwsConnector(mockTranslate, mockComprehend);
    List<LanguageDetection> ld = ac.identifyLanguage("Bonjour le monde");

    assertEquals(3, ld.size());
    assertEquals("fr", ld.get(0).getLanguage());
    assertEquals(0.8, ld.get(0).getProbability(), 0.00001);
    assertEquals("ch", ld.get(1).getLanguage());
    assertEquals(0.5, ld.get(1).getProbability(), 0.00001);
    assertEquals("de", ld.get(2).getLanguage());
    assertEquals(0.2, ld.get(2).getProbability(), 0.00001);

    verify(mockComprehend, times(1)).detectDominantLanguage(any());
    verifyNoMoreInteractions(mockComprehend);
    verifyNoInteractions(mockTranslate);
  }

  @Test
  public void testIdentifyLanguageException() {
    when(mockComprehend.detectDominantLanguage(any())).thenThrow(AmazonServiceException.class);

    AwsConnector ac = new AwsConnector(mockTranslate, mockComprehend);
    assertThrows(ConnectorException.class, () -> ac.identifyLanguage("Bonjour le monde"));
  }

  @Test
  public void testTranslate() throws ConnectorException {
    when(mockTranslate.translateText(any()))
        .thenReturn(
            new TranslateTextResult()
                .withSourceLanguageCode("fr")
                .withTargetLanguageCode("en")
                .withTranslatedText("Hello world"));

    AwsConnector ac = new AwsConnector(mockTranslate, mockComprehend);
    Translation t = ac.translate("fr", "en", "Bonjour le monde");

    assertEquals("fr", t.getSourceLanguage());
    assertEquals("Hello world", t.getContent());

    verify(mockTranslate, times(1)).translateText(any());
    verifyNoMoreInteractions(mockTranslate);
    verifyNoInteractions(mockComprehend);
  }

  @Test
  public void testTranslateAuto() throws ConnectorException {
    when(mockTranslate.translateText(any()))
        .thenReturn(
            new TranslateTextResult()
                .withSourceLanguageCode("fr")
                .withTargetLanguageCode("en")
                .withTranslatedText("Hello world"));

    AwsConnector ac = new AwsConnector(mockTranslate, mockComprehend);
    Translation t = ac.translate(ConnectorUtils.LANGUAGE_AUTO, "en", "Bonjour le monde");

    assertEquals("fr", t.getSourceLanguage());
    assertEquals("Hello world", t.getContent());

    verify(mockTranslate, times(1)).translateText(any());
    verifyNoMoreInteractions(mockTranslate);
    verifyNoInteractions(mockComprehend);
  }

  @Test
  public void testTranslateException() {
    when(mockTranslate.translateText(any())).thenThrow(AmazonServiceException.class);

    AwsConnector ac = new AwsConnector(mockTranslate, mockComprehend);
    assertThrows(ConnectorException.class, () -> ac.translate("fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testQueryEngine() {
    EngineDetails ed = (new AwsConnector(mockTranslate, mockComprehend)).queryEngine();

    assertNotNull(ed);
    assertNotNull(ed.getName());
    assertNotNull(ed.getVersion());
    assertFalse(ed.isSupportedLanguagesSupported());
    assertTrue(ed.isIdentifyLanguageSupported());
    assertTrue(ed.isTranslateSupported());
  }

  @Test
  public void testSupported() {
    AwsConnector rc = new AwsConnector(mockTranslate, mockComprehend);
    ConnectorTestMethods.testSupportedOperations(rc);
  }

  @Test
  public void testGetRegion() throws ConfigurationException {
    Map<String, Object> m = new HashMap<>();

    Region curr = Regions.getCurrentRegion();

    if (curr == null) {
      assertEquals(Regions.DEFAULT_REGION, AwsConnector.getRegion(m));
    } else {
      assertEquals(Regions.fromName(curr.getName()), AwsConnector.getRegion(m));
    }

    m.put(AwsConnector.PROP_REGION, Regions.CA_CENTRAL_1);
    assertEquals(Regions.CA_CENTRAL_1, AwsConnector.getRegion(m));

    m.put(AwsConnector.PROP_REGION, Regions.AP_NORTHEAST_1.getName());
    assertEquals(Regions.AP_NORTHEAST_1, AwsConnector.getRegion(m));

    m.put(AwsConnector.PROP_REGION, "Hello world");
    assertThrows(ConfigurationException.class, () -> AwsConnector.getRegion(m));

    m.put(AwsConnector.PROP_REGION, 123);
    assertThrows(ConfigurationException.class, () -> AwsConnector.getRegion(m));
  }
}
