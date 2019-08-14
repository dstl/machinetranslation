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

/** Generic exception to indicate that there has been an issue with a connector */
public class ConnectorException extends Exception {
  public ConnectorException(String message) {
    super(message);
  }

  public ConnectorException(Throwable throwable) {
    super(throwable);
  }

  public ConnectorException(String message, Throwable throwable) {
    super(message, throwable);
  }
}
