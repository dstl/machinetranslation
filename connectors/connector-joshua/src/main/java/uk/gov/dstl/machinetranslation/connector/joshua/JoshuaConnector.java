package uk.gov.dstl.machinetranslation.connector.joshua;

/*-
 * #%L
 * MT API Connector for Apache Joshua
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

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.LanguageDetection;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.MTConnectorApi;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConfigurationUtils;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Connector for Apache Joshua (https://joshua.apache.org) */
public class JoshuaConnector implements MTConnectorApi {
  private URI server;

  /** Property key for specifying the Joshua server */
  public static final String PROP_SERVER = "server";

  /** Create new Joshua Connector with default configuration */
  public JoshuaConnector() {
    try {
      configure(Collections.emptyMap());
    } catch (ConfigurationException ce) {
      // This shouldn't happen...
      throw new RuntimeException("Unable to create default configuration", ce);
    }
  }

  /**
   * Create new Joshua Connector with specified configuration
   *
   * @param server URI of Joshua server
   */
  public JoshuaConnector(URI server) throws ConfigurationException {
    Map<String, Object> conf = new HashMap<>();
    conf.put(PROP_SERVER, server);

    configure(conf);
  }

  /**
   * Configures this instance of JoshuaConnector. The following keys are accepted:
   *
   * <ul>
   *   <li><strong>{@value PROP_SERVER}</strong> - The URI of the Joshua server (defaults to
   *       http://localhost:5674)
   * </ul>
   *
   * @param configuration Key-value pairs
   */
  @Override
  public void configure(Map<String, Object> configuration) throws ConfigurationException {
    // If server isn't specified, default to 5674
    if (configuration.containsKey(PROP_SERVER)) {
      this.server = ConfigurationUtils.getURI(configuration.get(PROP_SERVER));
    } else {
      try {
        this.server = new URI("http://localhost:5674");
      } catch (URISyntaxException e) {
        throw new ConfigurationException("Couldn't set default URI for Joshua server", e);
      }
    }
  }

  @Override
  public Collection<LanguagePair> supportedLanguages() {
    throw new UnsupportedOperationException(
        "Joshua does not support retrieving configured languages");
  }

  @Override
  public List<LanguageDetection> identifyLanguage(String content) {
    throw new UnsupportedOperationException("Joshua does not support language identification");
  }

  /**
   * Translate text using Joshua
   *
   * <p>The source and target language are ignored, as Joshua is configured to support a single
   * language pair only (which can't be queried) and as such these parameters are irrelevant.
   *
   * @param sourceLanguage Source language - ignored by JoshuaConnector
   * @param targetLanguage Target language - ignored by JoshuaConnector
   * @param content The content to translate
   */
  @Override
  public Translation translate(String sourceLanguage, String targetLanguage, String content)
      throws ConnectorException {

    // Build request
    URIBuilder builder = new URIBuilder(server);
    builder.setParameter("q", content);
    // TODO: Add metadata - is any of it useful?

    CloseableHttpClient httpClient = HttpClients.createDefault();
    CloseableHttpResponse response = null;

    String translated;
    try {
      // Send request
      HttpGet getRequest = new HttpGet(builder.build());
      response = httpClient.execute(getRequest);

      if (response.getStatusLine().getStatusCode() != 200) {
        throw new ConnectorException(
            "Joshua returned a " + response.getStatusLine().getStatusCode() + " status code");
      }

      // Extract content from response
      HttpEntity entity = response.getEntity();
      translated =
        ((JSONArray) JsonPath.read(entity.getContent(), "$.data.translations[*].translatedText"))
          .stream().map(Object::toString).collect(Collectors.joining("\n"));

    } catch (IOException e) {
      throw new ConnectorException("Unable to communicate with server", e);
    } catch (URISyntaxException e) {
      throw new ConnectorException("Unable to build query URL", e);
    } finally {
      ConnectorUtils.silentlyClose(response, httpClient);
    }

    // Return translation
    return new Translation(sourceLanguage, translated);
  }

  @Override
  public EngineDetails queryEngine() {
    return new EngineDetails("Apache Joshua", ConnectorUtils.VERSION_UNKNOWN, false, false, true);
  }
}
