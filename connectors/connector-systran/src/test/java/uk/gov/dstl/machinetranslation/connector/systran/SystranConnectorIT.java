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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

public class SystranConnectorIT {

  @Test
  public void test() throws ConnectorException {
    String apiKey = System.getProperty("systranApiKey");
    if (apiKey == null || apiKey.isEmpty()) {
      fail("API Key (-DsystranApiKey) required for integration test");
    }

    Map<String, Object> conf = new HashMap<>();
    conf.put(SystranConnector.PROP_API_KEY, apiKey);

    SystranConnector sc = new SystranConnector();
    sc.configure(conf);

    Collection<LanguagePair> lp = sc.supportedLanguages();

    assertFalse(lp.isEmpty());
    assertTrue(lp.contains(new LanguagePair("fr", "en")));

    Translation t = sc.translate("fr", "en", "Bonjour le monde");
    assertEquals("fr", t.getSourceLanguage());
    assertNotNull(t.getContent());
  }

  @Test
  public void testAuto() throws ConnectorException {
    String apiKey = System.getProperty("systranApiKey");
    if (apiKey == null) {
      fail("API Key (-DsystranApiKey) required for integration test");
    }

    Map<String, Object> conf = new HashMap<>();
    conf.put(SystranConnector.PROP_API_KEY, apiKey);

    SystranConnector sc = new SystranConnector();
    sc.configure(conf);

    Translation t = sc.translate(ConnectorUtils.LANGUAGE_AUTO, "en", "Bonjour le monde");
    assertEquals("fr", t.getSourceLanguage());
    assertNotNull(t.getContent());
  }
}
