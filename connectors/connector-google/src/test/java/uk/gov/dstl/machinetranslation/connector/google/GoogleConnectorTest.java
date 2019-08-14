package uk.gov.dstl.machinetranslation.connector.google;

/*-
 * #%L
 * MT API Connector for Google Cloud Translate
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.google.cloud.translate.Detection;
import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.Translation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.LanguageDetection;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorTestMethods;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

@ExtendWith(MockitoExtension.class)
public class GoogleConnectorTest {

  @Mock Translate mockClient;

  @Test
  public void testSupportedLanguages() throws Exception {
    when(mockClient.listSupportedLanguages(any()))
        .thenReturn(
            Arrays.asList(
                createLanguage("en", "English"),
                createLanguage("fr", "French"),
                createLanguage("it", "Italian")));

    GoogleConnector gc = new GoogleConnector(mockClient);

    Collection<LanguagePair> pairs = gc.supportedLanguages();
    assertNotNull(pairs);
    assertEquals(6, pairs.size());
    assertTrue(pairs.contains(new LanguagePair("en", "fr")));
    assertTrue(pairs.contains(new LanguagePair("fr", "en")));
    assertTrue(pairs.contains(new LanguagePair("en", "it")));
    assertTrue(pairs.contains(new LanguagePair("it", "en")));
    assertTrue(pairs.contains(new LanguagePair("fr", "it")));
    assertTrue(pairs.contains(new LanguagePair("it", "fr")));
  }

  @Test
  public void testIdentifyLanguage() throws Exception {
    when(mockClient.detect(anyString())).thenReturn(createDetection("fr", 0.8f));

    GoogleConnector gc = new GoogleConnector(mockClient);

    List<LanguageDetection> ld = gc.identifyLanguage("Bonjour le monde");
    assertEquals(1, ld.size());

    LanguageDetection d = ld.get(0);
    assertEquals("fr", d.getLanguage());
    assertEquals(0.8, d.getProbability(), 0.0001);
  }

  @Test
  public void testTranslate() throws Exception {
    when(mockClient.translate(anyString(), any()))
        .thenReturn(createTranslation("Hello world", "fr", "nmt"));

    GoogleConnector gc = new GoogleConnector(mockClient);

    uk.gov.dstl.machinetranslation.connector.api.Translation t =
        gc.translate("fr", "en", "Bonjour le monde");
    assertEquals("fr", t.getSourceLanguage());
    assertEquals("Hello world", t.getContent());
  }

  @Test
  public void testTranslateAuto() throws Exception {
    when(mockClient.translate(anyString(), any()))
        .thenReturn(createTranslation("Hello world", "fr", "nmt"));

    GoogleConnector gc = new GoogleConnector(mockClient);

    uk.gov.dstl.machinetranslation.connector.api.Translation t =
        gc.translate(ConnectorUtils.LANGUAGE_AUTO, "en", "Bonjour le monde");
    assertEquals("fr", t.getSourceLanguage());
    assertEquals("Hello world", t.getContent());
  }

  @Test
  public void testQueryEngine() {
    EngineDetails ed = (new GoogleConnector(mockClient)).queryEngine();

    assertNotNull(ed);
    assertNotNull(ed.getName());
    assertNotNull(ed.getVersion());
    assertTrue(ed.isSupportedLanguagesSupported());
    assertTrue(ed.isIdentifyLanguageSupported());
    assertTrue(ed.isTranslateSupported());
  }

  @Test
  public void testSupported() {
    GoogleConnector rc = new GoogleConnector(mockClient);
    ConnectorTestMethods.testSupportedOperations(rc);
  }

  /**
   * Google Client API doesn't expose a way of creating Language classes, so we have to get access
   * to the underlying constructor via reflection.
   */
  private static Language createLanguage(String code, String name) throws Exception {
    Constructor<Language> c = Language.class.getDeclaredConstructor(String.class, String.class);
    c.setAccessible(true);

    return c.newInstance(code, name);
  }

  /**
   * Google Client API doesn't expose a way of creating Detection classes, so we have to get access
   * to the underlying constructor via reflection.
   */
  private static Detection createDetection(String code, float confidence) throws Exception {
    Constructor<Detection> c = Detection.class.getDeclaredConstructor(String.class, Float.class);
    c.setAccessible(true);

    return c.newInstance(code, confidence);
  }

  /**
   * Google Client API doesn't expose a way of creating Translation classes, so we have to get
   * access to the underlying constructor via reflection.
   */
  private static Translation createTranslation(
      String translatedText, String sourceLanguage, String model) throws Exception {
    Constructor<Translation> c =
        Translation.class.getDeclaredConstructor(String.class, String.class, String.class);
    c.setAccessible(true);

    return c.newInstance(translatedText, sourceLanguage, model);
  }
}
