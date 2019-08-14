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

import java.io.Closeable;
import java.util.Comparator;
import java.util.List;
import uk.gov.dstl.machinetranslation.connector.api.LanguageDetection;
import uk.gov.dstl.machinetranslation.connector.api.MTConnectorApi;

/** Utility methods and constants for working with the {@link MTConnectorApi} */
public class ConnectorUtils {
  /**
   * Constant to be used for the source language during translation when the language is unknown and
   * detection should be performed as part of the translation process.
   */
  public static final String LANGUAGE_AUTO = "auto";

  /** Constant to be used if the version of the engine is unknown */
  public static final String VERSION_UNKNOWN = "unknown";

  private ConnectorUtils() {
    // Utility class
  }

  /**
   * Sorts a list of {@link LanguageDetection}s, with the highest probability first
   *
   * @param detections The list to sort
   */
  public static void sortDetections(List<LanguageDetection> detections) {
    detections.sort(Comparator.comparingDouble(LanguageDetection::getProbability).reversed());
  }

  /** Close a closeable object, ignoring any exceptions */
  public static void silentlyClose(Closeable c) {
    if (c == null) return;

    try {
      c.close();
    } catch (Exception e) {
      // Do nothing
    }
  }

  /** Close multiple closeable objects, ignoring any exceptions */
  public static void silentlyClose(Closeable... c) {
    for (Closeable cl : c) silentlyClose(cl);
  }
}
