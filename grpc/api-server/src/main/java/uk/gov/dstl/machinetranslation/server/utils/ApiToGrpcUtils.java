package uk.gov.dstl.machinetranslation.server.utils;

/*-
 * #%L
 * MT Server
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

import java.util.Collection;
import java.util.stream.Collectors;
import uk.gov.dstl.machinetranslation.MachineTranslation;
import uk.gov.dstl.machinetranslation.connector.api.EngineDetails;
import uk.gov.dstl.machinetranslation.connector.api.LanguageDetection;
import uk.gov.dstl.machinetranslation.connector.api.LanguagePair;
import uk.gov.dstl.machinetranslation.connector.api.Translation;

public class ApiToGrpcUtils {

  private ApiToGrpcUtils() {
    // Utility class
  }

  public static Collection<MachineTranslation.LanguagePair> buildLanguagePairCollection(
      Collection<LanguagePair> languagePairCollection) {

    return languagePairCollection.stream()
        .map(
            lp ->
                MachineTranslation.LanguagePair.newBuilder()
                    .setSourceLanguage(lp.getSourceLanguage())
                    .setTargetLanguage(lp.getTargetLanguage())
                    .build())
        .collect(Collectors.toList());
  }

  public static Collection<MachineTranslation.LanguageDetection> buildLanguageDetectionCollection(
      Collection<LanguageDetection> languageDetectionCollection) {

    return languageDetectionCollection.stream()
        .map(
            ld ->
                MachineTranslation.LanguageDetection.newBuilder()
                    .setLanguage(ld.getLanguage())
                    .setProbability((float) ld.getProbability())
                    .build())
        .collect(Collectors.toList());
  }

  public static MachineTranslation.TranslationResponse buildTranslationResponse(
      Translation translation) {

    return MachineTranslation.TranslationResponse.newBuilder()
        .setLanguage(translation.getSourceLanguage())
        .setContent(translation.getContent())
        .build();
  }

  public static MachineTranslation.QueryEngineResponse buildQueryEngineResponse(
      EngineDetails details) {

    return MachineTranslation.QueryEngineResponse.newBuilder()
        .setName(details.getName())
        .setVersion(details.getVersion())
        .build();
  }
}
