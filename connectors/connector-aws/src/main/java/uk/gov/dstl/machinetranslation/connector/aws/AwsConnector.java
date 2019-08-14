package uk.gov.dstl.machinetranslation.connector.aws;

/*-
 * #%L
 * MT API Connector for AWS Translate
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

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.comprehend.AmazonComprehend;
import com.amazonaws.services.comprehend.AmazonComprehendClientBuilder;
import com.amazonaws.services.comprehend.model.DetectDominantLanguageRequest;
import com.amazonaws.services.comprehend.model.DetectDominantLanguageResult;
import com.amazonaws.services.translate.AmazonTranslate;
import com.amazonaws.services.translate.AmazonTranslateClient;
import com.amazonaws.services.translate.model.TranslateTextRequest;
import com.amazonaws.services.translate.model.TranslateTextResult;
import java.util.*;
import java.util.stream.Collectors;
import uk.gov.dstl.machinetranslation.connector.api.*;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConfigurationException;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

/**
 * Connector to connect to AWS, using Amazon Translate for translation, and Amazon Comprehend for
 * language identification.
 *
 * <p>An AWS account is required, and your credentials will be read using the standard AWS approach
 * which is documented at
 * https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html. Credentials
 * will be read at creation, and every time {@link #configure(Map)} is called.
 */
public class AwsConnector implements MTConnectorApi {
  private Regions region;

  private AWSCredentialsProvider awsCreds;

  private AmazonTranslate translate;
  private AmazonComprehend comprehend;

  /** Property key for configuring the AWS region */
  public static final String PROP_REGION = "region";

  /** Create a new instance with the default configuration */
  public AwsConnector() throws ConfigurationException {
    configure(Collections.emptyMap());
  }

  /**
   * Create a new instance with the specified region
   *
   * @param region Region to access AWS services in
   */
  public AwsConnector(Regions region) throws ConfigurationException {
    Map<String, Object> conf = new HashMap<>();
    conf.put(PROP_REGION, region);

    configure(conf);
  }

  /** Create a new instance with explicit clients, used for testing */
  protected AwsConnector(AmazonTranslate translateClient, AmazonComprehend comprehendClient) {
    this.translate = translateClient;
    this.comprehend = comprehendClient;
  }

  /**
   * Configure the connector, and create new clients using the updated configuration. The following
   * keys are accepted:
   *
   * <ul>
   *   <li><strong>{@value PROP_REGION}</strong> - The AWS region to use (defaults to your current
   *       region, or the default AWS region if the current region isn't set)
   * </ul>
   *
   * This method also re-reads your AWS Credentials.
   *
   * @param configuration Map of key-value pairs
   */
  @Override
  public void configure(Map<String, Object> configuration) throws ConfigurationException {
    awsCreds = DefaultAWSCredentialsProviderChain.getInstance();

    region = getRegion(configuration);

    translate =
        AmazonTranslateClient.builder().withCredentials(awsCreds).withRegion(region).build();

    comprehend =
        AmazonComprehendClientBuilder.standard()
            .withCredentials(awsCreds)
            .withRegion(region)
            .build();
  }

  @Override
  public Collection<LanguagePair> supportedLanguages() {
    throw new UnsupportedOperationException(
        "AWS does not support retrieving supported languages via API. Refer to http://docs.aws.amazon.com/translate/latest/dg/pairs.html for a list of currently supported languages.");
  }

  @Override
  public List<LanguageDetection> identifyLanguage(String content) throws ConnectorException {
    try {
      DetectDominantLanguageRequest request = new DetectDominantLanguageRequest().withText(content);
      DetectDominantLanguageResult result = comprehend.detectDominantLanguage(request);

      List<LanguageDetection> detections =
          result.getLanguages().stream()
              .map(dl -> new LanguageDetection(dl.getScore(), dl.getLanguageCode()))
              .collect(Collectors.toList());

      ConnectorUtils.sortDetections(detections);

      return detections;
    } catch (Exception e) {
      throw new ConnectorException("Unable to detect language", e);
    }
  }

  @Override
  public Translation translate(String sourceLanguage, String targetLanguage, String content)
      throws ConnectorException {
    try {
      TranslateTextRequest request =
          new TranslateTextRequest()
              .withText(content)
              .withSourceLanguageCode(sourceLanguage)
              .withTargetLanguageCode(targetLanguage);

      TranslateTextResult result = translate.translateText(request);

      return new Translation(result.getSourceLanguageCode(), result.getTranslatedText());
    } catch (Exception e) {
      throw new ConnectorException("Unable to translate", e);
    }
  }

  @Override
  public EngineDetails queryEngine() {
    return new EngineDetails("AWS Translate", ConnectorUtils.VERSION_UNKNOWN, false, true, true);
  }

  protected static Regions getRegion(Map<String, Object> configuration)
      throws ConfigurationException {
    if (configuration.containsKey(PROP_REGION)) {
      Object oRegion = configuration.get(PROP_REGION);

      if (oRegion instanceof Regions) {
        return (Regions) oRegion;
      } else if (oRegion instanceof String) {
        try {
          return Regions.fromName((String) oRegion);
        } catch (IllegalArgumentException e) {
          throw new ConfigurationException("Invalid region", e);
        }
      } else {
        throw new ConfigurationException(
            "Property " + PROP_REGION + " is not of correct type (Regions or String)");
      }
    } else {
      Region r = Regions.getCurrentRegion();
      if (r == null) {
        return Regions.fromName(Regions.DEFAULT_REGION.getName());
      }

      return Regions.fromName(r.getName());
    }
  }
}
