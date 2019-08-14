package uk.gov.dstl.machinetranslation.connector.api.utils;

/*-
 * #%L
 * Machine Translation Connector API
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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;

public class ConfigurationUtilsTest {
  @Test
  public void testGetURI() throws URISyntaxException, ConfigurationException {
    URI truth = new URI("http://localhost/hello_world");

    assertThrows(ConfigurationException.class, () -> ConfigurationUtils.getURI(null));

    assertEquals(truth, ConfigurationUtils.getURI(truth));
    assertEquals(truth, ConfigurationUtils.getURI("http://localhost/hello_world"));
    assertEquals(
        truth, ConfigurationUtils.getURI(new StringWrapper("http://localhost/hello_world")));

    assertThrows(ConfigurationException.class, () -> ConfigurationUtils.getURI("INVALID URI"));
    assertThrows(
        ConfigurationException.class,
        () -> ConfigurationUtils.getURI(new ComplexObject("HELLO WORLD", 17)));
  }

  private class StringWrapper {
    private final String s;

    private StringWrapper(String s) {
      this.s = s;
    }

    @Override
    public String toString() {
      return s;
    }
  }

  private class ComplexObject {
    private final String s;
    private final Integer i;

    private ComplexObject(String s, Integer i) {
      this.s = s;
      this.i = i;
    }

    @Override
    public String toString() {
      return "Complex Object [s = " + s + ", i = " + i + "]";
    }
  }
}
