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

import static junit.framework.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;

/** Requires a running Moses server on http://localhost:8080 */
public class MosesConnectorIT {

  @Test
  public void testDefault() throws ConnectorException {
    MosesConnector mc = new MosesConnector();

    Translation t = mc.translate("fr", "en", "Bonjour le monde");
    assertNotNull(t);
    assertNotNull(t.getContent());
  }

  @Test
  public void test() throws ConnectorException {
    Map<String, Object> conf = new HashMap<>();
    conf.put(MosesConnector.PROP_RPC, "http://localhost:8080/RPC2");

    MosesConnector mc = new MosesConnector();
    mc.configure(conf);

    Translation t = mc.translate("fr", "en", "Bonjour le monde");
    assertNotNull(t);
    assertNotNull(t.getContent());
  }
}
