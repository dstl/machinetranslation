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

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import uk.gov.dstl.machinetranslation.connector.api.Translation;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;

public class JoshuaConnectorIT {
  @Test
  public void test() throws ConnectorException {
    JoshuaConnector jc = new JoshuaConnector();
    Translation t = jc.translate("fr", "en", "Bonjour le monde");

    assertNotNull(t);
  }
}
