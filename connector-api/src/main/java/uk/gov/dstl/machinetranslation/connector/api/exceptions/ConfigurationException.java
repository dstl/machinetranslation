package uk.gov.dstl.machinetranslation.connector.api.exceptions;

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

/** Exception to indicate there is an issue with the configuration of a connector */
public class ConfigurationException extends ConnectorException {
  public ConfigurationException(String message) {
    super(message);
  }

  public ConfigurationException(Throwable throwable) {
    super(throwable);
  }

  public ConfigurationException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
