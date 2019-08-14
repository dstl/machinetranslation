package uk.gov.dstl.machinetranslation.examples;

/*-
 * #%L
 * Machine Translation Connector API Example
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

import uk.gov.dstl.machinetranslation.connector.api.*;
import uk.gov.dstl.machinetranslation.connector.api.exceptions.ConnectorException;
import uk.gov.dstl.machinetranslation.connector.api.noop.NoOpConnector;
import uk.gov.dstl.machinetranslation.connector.api.utils.ConnectorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example of how to use the Connector API
 */
public class ConnectorExample {

  /**
   * Main entry point
   *
   * An optional single argument can be passed containing text to translate.
   * If not passed, a default is used.
   * Additional arguments are ignored.
   */
  public static void main(String[] args) throws ConnectorException {
    //Get text to translate, or use a default
    String textToTranslate = "bonjour le monde";
    if(args.length > 0){
      textToTranslate = args[0];
    }

    /*
    We use the NoOpConnector here for simplicity, but in reality we would use an actual implementation.

    For instance, we could choose the AwsConnector in which case we'd use:

    <code>
      MTConnectorApi api = new AwsConnector();
    </code>

    Or maybe we'd choose to find one using the ServiceLoader

    <code>
      ServiceLoader<MTConnectorApi> loader = ServiceLoader.load(MTConnectorApi.class);
      MTConnectorApi api = loader.iterator().next();
    </code>

    Try switching this out to other connectors to get better results from the examples below.
     */
    MTConnectorApi api = new NoOpConnector();

    //Does nothing for the NoOpConnector, but we should always call configure() before use anyway
    Map<String, Object> configuration = new HashMap<String, Object>();      //Usually, we would add configuration parameters to this map
    api.configure(configuration);

    //Let's get some details about the engine (i.e. MT tool) to start with
    EngineDetails details = api.queryEngine();

    System.out.println("Name: "+details.getName());
    System.out.println("Version: "+details.getVersion());
    System.out.println();

    //Let's see which language pairs are supported
    if(details.isSupportedLanguagesSupported()) {
      System.out.println("I can translate from...");
      for (LanguagePair lp : api.supportedLanguages()) {
        System.out.println("\t" + lp.getSourceLanguage() + " to " + lp.getTargetLanguage());
      }

      System.out.println();
    }

    //Let's try to identify a language
    if(details.isIdentifyLanguageSupported()){
      //These will be returned with the most likely language first
      List<LanguageDetection> detections = api.identifyLanguage("bonjour");

      System.out.println("I think \""+textToTranslate+"\" is...");
      for(LanguageDetection ld : detections){
        System.out.println("\t"+ld.getLanguage() + " (" + String.format("%.2f", ld.getProbability()*100.0) + "%)");
      }

      System.out.println();
    }

    //Let's try to translate
    if(details.isTranslateSupported()){
      //Translate from "auto" to English
      Translation t = api.translate(ConnectorUtils.LANGUAGE_AUTO, "en", textToTranslate);

      System.out.println("\""+textToTranslate+"\" in "+t.getSourceLanguage()+" translates to \""+t.getContent()+" in English");
    }
  }
}
