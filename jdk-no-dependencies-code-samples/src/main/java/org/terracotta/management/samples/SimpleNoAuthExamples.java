/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terracotta.management.samples;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Anthony Dahanne on 2016-03-30.
 */
public class SimpleNoAuthExamples {

  // you can also use http://localhost:9889/tmc/api/v2 if you want to reach the TMC (non secured) Rest Api instead of an agent Rest Api
  // the /v2 URLs adds exceptionEntities, where exceptions are stored
  public static final String AGENT_URL = "http://localhost:9540/tc-management-api/v2";

  public static void main(String[] args) {

    String agentsUrl = AGENT_URL + "/agents";
    System.out.println("Requesting list of agents : " + agentsUrl);
    System.out.println(get(agentsUrl));


    String cacheManagersUrl = AGENT_URL + "/agents/cacheManagers";
    System.out.println("Requesting list of cache managers : " + cacheManagersUrl);
    System.out.println(get(cacheManagersUrl));

    String backupUrl = AGENT_URL + "/agents;ids=embedded/backups;name=MyBackup";
    System.out.println("Triggering backup : " + backupUrl);
    System.out.println(post(backupUrl));

    System.out.println("Listing backup : " + backupUrl);
    System.out.println(get(backupUrl));


    String dgcUrl = AGENT_URL + "/agents;ids=embedded/diagnostics/dgc";
    System.out.println("Triggering DGC : " + dgcUrl);
    System.out.println(post(dgcUrl));


  }


  private static Result post(String targetURL) {
    String body = "";
    int responseCode = 0;
    String contentType = "";

    HttpURLConnection connection = null;
    try {
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();

      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", "application/json");

      contentType = connection.getContentType();
      try {
        responseCode = connection.getResponseCode();
      } catch (FileNotFoundException e) {
        // 404
      }
      InputStream is;
      try {
        is = connection.getInputStream();
      } catch (FileNotFoundException e) {
        // 404
        is = connection.getErrorStream();
      }
      body = consumeStreamIntoString(is);
    } catch (Exception e) {
      throw new RuntimeException("Could not perform the HTTP request", e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return new Result(responseCode, body, contentType);
  }

  private static Result get(String targetURL) {
    String body = "";
    int responseCode = 0;
    String contentType = "";

    HttpURLConnection connection = null;
    try {
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
      contentType = connection.getContentType();
      try {
        responseCode = connection.getResponseCode();
      } catch (FileNotFoundException e) {
        // 404
      }
      InputStream is;
      try {
        is = connection.getInputStream();
      } catch (FileNotFoundException e) {
        // 404
        is = connection.getErrorStream();
      }
      body = consumeStreamIntoString(is);
    } catch (Exception e) {
      throw new RuntimeException("Could not perform the HTTP request", e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
    return new Result(responseCode, body, contentType);
  }

  private static String consumeStreamIntoString(InputStream is) throws IOException {
    String body;
    try {
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
      StringBuilder response = new StringBuilder();
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      body = response.toString();
    } finally {
      if (is != null) {
        is.close();
      }
    }
    return body;
  }

  static class Result {

    private final int statusCode;
    private final String body;
    private final String contentType;

    public Result(int statusCode, String body, String contentType) {
      this.statusCode = statusCode;
      this.body = body;
      this.contentType = contentType;
    }

    public int getStatusCode() {
      return statusCode;
    }

    public String getBody() {
      return body;
    }

    public String getContentType() {
      return contentType;
    }

    @Override
    public String toString() {
      return "Status : " + statusCode + "\n" + "Content-type : " + contentType + "\n" + "Body : " + body;
    }
  }


}
