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

import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Parameter;
import org.mockserver.model.Parameters;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorTestMethods;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

public class JoshuaConnectorTest {

  @Test
  public void test() throws Exception {
    ClientAndServer mockServer = startClientAndServer();

    mockServer
        .when(request().withMethod("GET"))
        .respond(
            response()
                .withStatusCode(200)
                .withBody(
                    "{\n"
                        + "  \"data\": {\n"
                        + "    \"translations\": [\n"
                        + "      {\n"
                        + "        \"translatedText\": \"Hello world\",\n"
                        + "        \"raw_nbest\": [\n"
                        + "          {\n"
                        + "            \"hyp\": \"hello world\",\n"
                        + "            \"totalScore\": -8.429729\n"
                        + "          }\n"
                        + "        ]\n"
                        + "      }\n"
                        + "    ]\n"
                        + "  }\n"
                        + "}"));

    JoshuaConnector c =
        new JoshuaConnector(new URI("http://localhost:" + mockServer.getLocalPort()));
    Translation t = c.translate("fr", "en", "Bonjour le monde");

    assertEquals("Hello world", t.getContent());
    assertEquals("fr", t.getSourceLanguage());

    mockServer.verify(
        request()
            .withPath("/")
            .withQueryStringParameters(new Parameters(new Parameter("q", "Bonjour le monde"))),
        exactly(1));

    mockServer.stop();
  }

  @Test
  public void testMultiLine() throws Exception {
    ClientAndServer mockServer = startClientAndServer();

    mockServer
      .when(request().withMethod("GET"))
      .respond(
        response()
          .withStatusCode(200)
          .withBody(
            "{\n"
              + "  \"data\": {\n"
              + "    \"translations\": [\n"
              + "      {\n"
              + "        \"translatedText\": \"Hello world\",\n"
              + "        \"raw_nbest\": [\n"
              + "          {\n"
              + "            \"hyp\": \"hello world\",\n"
              + "            \"totalScore\": -8.429729\n"
              + "          }\n"
              + "        ]\n"
              + "      },\n"
              + "      {\n"
              + "        \"translatedText\": \"One two three\",\n"
              + "        \"raw_nbest\": [\n"
              + "          {\n"
              + "            \"hyp\": \"one two three\",\n"
              + "            \"totalScore\": -5.34621\n"
              + "          }\n"
              + "        ]\n"
              + "      }\n"
              + "    ]\n"
              + "  }\n"
              + "}"));

    JoshuaConnector c =
      new JoshuaConnector(new URI("http://localhost:" + mockServer.getLocalPort()));
    Translation t = c.translate("fr", "en", "Bonjour le monde\nUn deux trois");

    assertEquals("Hello world\nOne two three", t.getContent());
    assertEquals("fr", t.getSourceLanguage());

    mockServer.verify(
      request()
        .withPath("/")
        .withQueryStringParameters(new Parameters(new Parameter("q", "Bonjour le monde\nUn deux trois"))),
      exactly(1));

    mockServer.stop();
  }

  @Test
  public void testBadErrorCode() throws Exception {
    ClientAndServer mockServer = startClientAndServer();

    mockServer.when(request().withMethod("GET")).respond(response().withStatusCode(500));

    JoshuaConnector c = new JoshuaConnector();

    Map<String, Object> conf = new HashMap<>();
    conf.put(JoshuaConnector.PROP_SERVER, "http://localhost:" + mockServer.getLocalPort());

    c.configure(conf);

    assertThrows(ConnectorException.class, () -> c.translate("fr", "en", "Bonjour le monde"));

    mockServer.stop();
  }

  @Test
  public void testBadUrl() throws Exception {
    ClientAndServer mockServer = startClientAndServer();

    mockServer.when(request().withMethod("GET")).respond(response().withStatusCode(500));

    JoshuaConnector c = new JoshuaConnector();

    Map<String, Object> conf = new HashMap<>();
    conf.put(JoshuaConnector.PROP_SERVER, "BAD URL");

    assertThrows(ConfigurationException.class, () -> c.configure(conf));

    mockServer.stop();
  }

  @Test
  public void testNoServer() throws Exception {
    JoshuaConnector c = new JoshuaConnector();

    Map<String, Object> conf = new HashMap<>();
    conf.put(JoshuaConnector.PROP_SERVER, "http://localhost:9999");

    c.configure(conf);

    assertThrows(ConnectorException.class, () -> c.translate("fr", "en", "Bonjour le monde"));
  }

  @Test
  public void testEngineDetails() {
    JoshuaConnector c = new JoshuaConnector();
    EngineDetails d = c.queryEngine();

    assertEquals("Apache Joshua", d.getName());
    assertEquals(ConnectorUtils.VERSION_UNKNOWN, d.getVersion());
    assertFalse(d.isSupportedLanguagesSupported());
    assertFalse(d.isIdentifyLanguageSupported());
    assertTrue(d.isTranslateSupported());
  }

  @Test
  public void testSupported() {
    JoshuaConnector rc = new JoshuaConnector();
    ConnectorTestMethods.testSupportedOperations(rc);
  }

  @Test
  public void testUnsupported() {
    JoshuaConnector c = new JoshuaConnector();

    assertThrows(UnsupportedOperationException.class, c::supportedLanguages);
    assertThrows(UnsupportedOperationException.class, () -> c.identifyLanguage("Ciao mondo"));
  }
}
