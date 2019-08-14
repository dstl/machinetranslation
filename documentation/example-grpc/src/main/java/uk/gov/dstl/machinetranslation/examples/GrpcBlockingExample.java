package uk.gov.dstl.machinetranslation.examples;

/*-
 * #%L
 * Machine Translation gRPC Example
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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import uk.gov.dstl.machinetranslation.MachineTranslation;
import uk.gov.dstl.machinetranslation.MachineTranslationServiceGrpc;

/**
 * Simple example demonstrating how to perform a blocking request to the Machine Translation API Server over gRPC
 */
public class GrpcBlockingExample {
  private final ManagedChannel channel;
  private final MachineTranslationServiceGrpc.MachineTranslationServiceBlockingStub blockingStub;

  /**
   * Entry point for this example
   */
  public static void main(String[] args){
    //Check that we have some input
    if(args.length != 3){
      System.err.println("You must provide three arguments:\n  1) The host of the gRPC server\n  2) The port of the gRPC server\n  3) A string to translate into English\n\n" +
          "e.g. GrpcBlockingExample localhost 6856 \"Bonjour le monde\"");
      return;
    }

    //Create an instance of this class
    GrpcBlockingExample client = new GrpcBlockingExample(args[0], Integer.valueOf(args[1]));

    //Perform the translation
    System.out.println(client.translate(args[2]));

    //Terminate
    client.shutdown();
  }

  /**
   * Create a new client
   *
   * @param host
   *    Host of the gRPC server
   * @param port
   *    Port of the gRPC server
   */
  public GrpcBlockingExample(String host, int port){
    //Create a channel
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

    //Now create the blocking stub
    blockingStub = MachineTranslationServiceGrpc.newBlockingStub(channel);
  }

  /**
   * Perform the translation by submitting a translation request via gRPC to the server.
   * For simplicity, we assume the source language will be detected and that the target language is English.
   *
   * @param text
   *    Text to translate
   *
   * @return
   *    Translated text in English
   */
  public String translate(String text){
    //Build the translation request
    MachineTranslation.TranslationRequest request =
        MachineTranslation.TranslationRequest.newBuilder()
            .setSourceLanguage("auto")
            .setTargetLanguage("en")
            .setContent(text).build();

    //Perform the translation
    MachineTranslation.TranslationResponse response = blockingStub.translate(request);

    //Read the response
    return response.getContent();
  }

  /**
   * Shutdown the channel after we're done
   */
  public void shutdown(){
    channel.shutdown();
  }
}
