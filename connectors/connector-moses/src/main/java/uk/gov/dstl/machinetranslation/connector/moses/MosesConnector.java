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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import uk.gov.dstl.machinetranslation.connector.api.*;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConfigurationUtils;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

public class MosesConnector implements MTConnectorApi {

  private XmlRpcClient client;

  /** Create an instance of MosesConnector with default configuration */
  public MosesConnector() throws ConfigurationException {
    client = new XmlRpcClient();
    configure(Collections.emptyMap());
  }

  /** Create an instance of MosesConnector with a specific client, for testing purposes */
  protected MosesConnector(XmlRpcClient client) {
    this.client = client;
  }

  /** Property key used for specifying the MOSES server RPC endpoint */
  public static final String PROP_RPC = "rpc";

  /**
   * Configures this instance of MosesConnector. The following keys are accepted:
   *
   * <ul>
   *   <li><strong>{@value PROP_RPC}</strong> - The RPC endpoint of the Moses server (defaults to
   *       http://localhost:8080/RPC2)
   * </ul>
   *
   * @param configuration Key-value pairs
   */
  @Override
  public void configure(Map<String, Object> configuration) throws ConfigurationException {
    XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();

    if (configuration.containsKey(PROP_RPC)) {
      try {
        config.setServerURL(ConfigurationUtils.getURI(configuration.get(PROP_RPC)).toURL());
      } catch (MalformedURLException e) {
        throw new ConfigurationException("Unable to configure server URL", e);
      }
    } else {
      try {
        config.setServerURL(new URL("http://localhost:8080/RPC2"));
      } catch (MalformedURLException e) {
        throw new ConfigurationException("Unable to set default server URL", e);
      }
    }

    client.setConfig(config);
  }

  @Override
  public Collection<LanguagePair> supportedLanguages() {
    throw new UnsupportedOperationException(
        "Moses does not support retrieving configured languages");
  }

  @Override
  public List<LanguageDetection> identifyLanguage(String content) {
    throw new UnsupportedOperationException("Moses does not support language identification");
  }

  /**
   * Translate text using Moses
   *
   * <p>The source and target language are ignored, as Moses is configured to support a single
   * language pair only (which can't be queried) and as such these parameters are irrelevant.
   *
   * @param sourceLanguage Source language - ignored by MosesConnector
   * @param targetLanguage Target language - ignored by MosesConnector
   * @param content The content to translate
   */
  @Override
  public Translation translate(String sourceLanguage, String targetLanguage, String content)
      throws ConnectorException {

    Map<String, String> request = new HashMap<>();
    request.put("text", content);

    Map<String, String> response;
    try {
      response =
          (Map<String, String>) client.execute("translate", Collections.singletonList(request));
    } catch (Exception e) {
      throw new ConnectorException("Unable to translate text using Moses", e);
    }

    if (!response.containsKey("text"))
      throw new ConnectorException("Moses did not return results in expected format");

    return new Translation(sourceLanguage, response.get("text"));
  }

  @Override
  public EngineDetails queryEngine() {
    return new EngineDetails("Moses", ConnectorUtils.VERSION_UNKNOWN, false, false, true);
  }
}
