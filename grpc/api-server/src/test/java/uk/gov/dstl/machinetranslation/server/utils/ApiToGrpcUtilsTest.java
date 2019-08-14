package uk.gov.dstl.machinetranslation.server.utils;

/*-
 * #%L
 * MT Server
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

import static org.junit.Assert.*;
import static uk.gov.dstl.machinetranslation.server.utils.ApiToGrpcUtils.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.junit.Test;
import uk.gov.dstl.machinetranslation.MachineTranslation;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.LanguageDetection;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.Translation;

public class ApiToGrpcUtilsTest {

  @Test
  public void testBuildLanguagePairCollection() {
    Collection<MachineTranslation.LanguagePair> result =
        buildLanguagePairCollection(Collections.singletonList(new LanguagePair("en-gb", "en-gb")));

    Iterator<MachineTranslation.LanguagePair> iter = result.iterator();
    assertTrue(iter.hasNext());

    MachineTranslation.LanguagePair languagePair = iter.next();
    assertEquals("en-gb", languagePair.getSourceLanguage());
    assertEquals("en-gb", languagePair.getTargetLanguage());

    assertFalse(iter.hasNext());
  }

  @Test
  public void testBuildLanguageDetectionCollection() {
    Collection<MachineTranslation.LanguageDetection> result =
        buildLanguageDetectionCollection(
            Collections.singletonList(new LanguageDetection(0.95f, "en-gb")));

    Iterator<MachineTranslation.LanguageDetection> iter = result.iterator();
    assertTrue(iter.hasNext());

    MachineTranslation.LanguageDetection languageDetection = iter.next();
    assertEquals("en-gb", languageDetection.getLanguage());
    assertEquals(0.95f, languageDetection.getProbability(), 0.0001);

    assertFalse(iter.hasNext());
  }

  @Test
  public void testBuildTranslationResponse() {
    Translation input = new Translation("en-gb", "test");

    MachineTranslation.TranslationResponse result = buildTranslationResponse(input);

    assertEquals("en-gb", result.getLanguage());
    assertEquals("test", result.getContent());
  }

  @Test
  public void testBuildQueryEngineResponse() {
    EngineDetails input = new EngineDetails("TestEngine", "0.0.1");

    MachineTranslation.QueryEngineResponse result = buildQueryEngineResponse(input);

    assertEquals("TestEngine", result.getName());
    assertEquals("0.0.1", result.getVersion());
  }
}
