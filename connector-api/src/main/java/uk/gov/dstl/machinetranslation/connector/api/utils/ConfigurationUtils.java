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

import java.net.URI;
import java.net.URISyntaxException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;

/** Utility methods for working with Connector configuration */
public class ConfigurationUtils {

  private ConfigurationUtils() {
    // Utility class
  }

  /** Safely cast an object to a URI, throwing a ConfigurationException if this can't be done */
  public static URI getURI(Object o) throws ConfigurationException {
    if (o == null) throw new ConfigurationException("Configuration value can't be null");

    if (o instanceof URI) {
      return (URI) o;
    } else if (o instanceof String) {
      try {
        return new URI((String) o);
      } catch (URISyntaxException e) {
        throw new ConfigurationException("Couldn't create URI from String [" + o + "]", e);
      }
    } else {
      try {
        return new URI(o.toString());
      } catch (URISyntaxException e) {
        throw new ConfigurationException("Couldn't create URI from Object [" + o + "]", e);
      }
    }
  }

  /**
   * Safely cast an object to an Integer, throwing a ConfigurationException if this can't be done
   */
  public static Integer getInteger(Object o) throws ConfigurationException {
    if (o == null) throw new ConfigurationException("Configuration value can't be null");

    if (o instanceof Integer) {
      return (Integer) o;
    } else if (o instanceof String) {
      try {
        return Integer.parseInt((String) o);
      } catch (NumberFormatException e) {
        throw new ConfigurationException("Couldn't create Integer from String [" + o + "]", e);
      }
    } else {
      throw new ConfigurationException("Couldn't create Integer from Object [" + o + "]");
    }
  }
}
