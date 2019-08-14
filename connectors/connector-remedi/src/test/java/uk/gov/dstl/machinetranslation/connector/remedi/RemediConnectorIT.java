package uk.gov.dstl.machinetranslation.connector.remedi;

/*-
 * #%L
 * MT API Connector for REMEDI
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URI;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;

/**
 * Integration test for REMEDI, assumes that pre-processor and post-processor are available at
 * ws://localhost:9080, and that the translation server is available at ws://localhost:9090.
 *
 * <p>Checks only that content is returned, not that it is a correct translation.
 */
public class RemediConnectorIT {

  @Test
  public void test() throws Exception {
    RemediConnector rc =
        new RemediConnector(
            new URI("ws://localhost:9080"),
            new URI("ws://localhost:9090"),
            new URI("ws://localhost:9080"));
    integrationTest(rc);
  }

  @Test
  public void testNoProcessing() throws Exception {
    RemediConnector rc = new RemediConnector(new URI("ws://localhost:9090"));
    integrationTest(rc);
  }

  private void integrationTest(RemediConnector rc) throws ConnectorException {
    Collection<LanguagePair> languages = rc.supportedLanguages();
    assertFalse(languages.isEmpty());

    for (LanguagePair lp : languages) {
      Translation t = rc.translate(lp.getSourceLanguage(), lp.getTargetLanguage(), "Hello world");

      assertEquals(lp.getSourceLanguage(), t.getSourceLanguage());
      assertFalse(t.getContent().isEmpty());
    }
  }
}
