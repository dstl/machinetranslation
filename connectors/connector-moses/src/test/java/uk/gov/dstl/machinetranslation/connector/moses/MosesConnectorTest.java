package uk.gov.dstl.machinetranslation.connector.moses;

/*-
 * #%L
 * MT API Connector for Moses
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.junit.jupiter.api.Test;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorTestMethods;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

public class MosesConnectorTest {

  @Test
  public void testTranslate() throws Exception {
    XmlRpcClient mockClient = mock(XmlRpcClient.class);

    Map<String, String> response = new HashMap<>();
    response.put("text", "Hello world");

    when(mockClient.execute(anyString(), anyList())).thenReturn(response);

    MosesConnector c = new MosesConnector(mockClient);
    Translation t = c.translate("fr", "en", "Bonjour le monde");

    assertEquals("Hello world", t.getContent());
    assertEquals("fr", t.getSourceLanguage());
  }

  @Test
  public void testEngineDetails() throws ConfigurationException {
    MosesConnector c = new MosesConnector();
    EngineDetails d = c.queryEngine();

    assertEquals("Moses", d.getName());
    assertEquals(ConnectorUtils.VERSION_UNKNOWN, d.getVersion());
    assertFalse(d.isSupportedLanguagesSupported());
    assertFalse(d.isIdentifyLanguageSupported());
    assertTrue(d.isTranslateSupported());
  }

  @Test
  public void testSupported() throws ConfigurationException {
    MosesConnector c = new MosesConnector();
    ConnectorTestMethods.testSupportedOperations(c);
  }

  @Test
  public void testUnsupported() throws ConfigurationException {
    MosesConnector c = new MosesConnector();

    assertThrows(UnsupportedOperationException.class, c::supportedLanguages);
    assertThrows(UnsupportedOperationException.class, () -> c.identifyLanguage("Ciao mondo"));
  }
}
