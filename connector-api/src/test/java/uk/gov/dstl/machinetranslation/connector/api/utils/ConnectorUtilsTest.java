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

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import uk.gov.dstl.machinetranslation.connector.api.LanguageDetection;

public class ConnectorUtilsTest {

  @Test
  public void testSortDetections() {
    LanguageDetection ld1 = new LanguageDetection(0.5, "en");
    LanguageDetection ld2 = new LanguageDetection(0.2, "de");
    LanguageDetection ld3 = new LanguageDetection(0.7, "es");
    LanguageDetection ld4 = new LanguageDetection(0.1, "fr");

    List<LanguageDetection> detections = Arrays.asList(ld1, ld2, ld3, ld4);

    ConnectorUtils.sortDetections(detections);

    assertEquals(ld3, detections.get(0));
    assertEquals(ld1, detections.get(1));
    assertEquals(ld2, detections.get(2));
    assertEquals(ld4, detections.get(3));
  }
}
