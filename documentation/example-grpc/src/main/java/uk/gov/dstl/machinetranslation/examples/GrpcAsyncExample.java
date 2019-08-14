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
import io.grpc.stub.StreamObserver;
import uk.gov.dstl.machinetranslation.MachineTranslation;
import uk.gov.dstl.machinetranslation.MachineTranslationServiceGrpc;

/**
 * Simple example demonstrating how to perform an asynchronous request to the Machine Translation API Server over gRPC
 */
public class GrpcAsyncExample {
  private final ManagedChannel channel;
  private final MachineTranslationServiceGrpc.MachineTranslationServiceStub asyncStub;

  private String result = null;

  /**
   * Entry point for this example
   */
  public static void main(String[] args){
    //Check that we have some input
    if(args.length != 3){
      System.err.println("You must provide three arguments:\n  1) The host of the gRPC server\n  2) The port of the gRPC server\n  3) A string to translate into English\n\n" +
          "e.g. GrpcAsyncExample localhost 6856 \"Bonjour le monde\"");
      return;
    }

    //Create an instance of this class
    GrpcAsyncExample client = new GrpcAsyncExample(args[0], Integer.valueOf(args[1]));

    //Perform the translation
    client.translate(args[2]);

    //Wait for the work to be finished
    while(client.getResult() == null){
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        //Do nothing
      }
    }

    //Print the result
    System.out.println(client.getResult());

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
  public GrpcAsyncExample(String host, int port){
    //Create a channel
    channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

    //Now create the asynchronous stub
    asyncStub = MachineTranslationServiceGrpc.newStub(channel);
  }

  /**
   * Perform the translation by submitting a translation request via gRPC to the server.
   * For simplicity, we assume the source language will be detected and that the target language is English.
   *
   * This method will return once the request has been sent, and the result variable will be populated
   * once the server has returned a response
   *
   * @param text
   *    Text to translate
   */
  public void translate(String text){
    //Build the translation request
    MachineTranslation.TranslationRequest request =
        MachineTranslation.TranslationRequest.newBuilder()
            .setSourceLanguage("auto")
            .setTargetLanguage("en")
            .setContent(text).build();

    //Perform the translation with a StreamObserver to collate the responses as they come in (there will only be one in our case)
    asyncStub.translate(request, new StreamObserver<MachineTranslation.TranslationResponse>() {
      private StringBuilder sb = new StringBuilder();

      /**
       * When we receive a response, add it to our result String
       */
      public void onNext(MachineTranslation.TranslationResponse translationResponse) {
        sb.append(translationResponse.getContent());
        sb.append("\n");
      }

      /**
       * If we encounter an error here, just rethrow it.
       * Obviously we'd want to handle it properly in real life
       */
      public void onError(Throwable throwable) {
        throw new RuntimeException(throwable);
      }

      /**
       * Once we're done, set the result
       */
      public void onCompleted() {
        result = sb.toString();
      }
    });
  }

  /**
   * Tell the main thread when we're done so that we can exit
   */
  public String getResult(){
    return result;
  }

  /**
   * Shutdown the channel after we're done
   */
  public void shutdown(){
    channel.shutdown();
  }
}
