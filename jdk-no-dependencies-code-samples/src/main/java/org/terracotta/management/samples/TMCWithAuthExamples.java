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
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony Dahanne on 2016-03-30.
 */
public class TmcWithAuthExamples {


  enum RequestType {
    GET, POST
  }

  public static final String OWASP_CSRFTOKEN = "OWASP_CSRFTOKEN";
  public static final String X_REQUESTED_WITH = "X-Requested-With";
  public static final String OWASP_CSRFGUARD_PROJECT = "OWASP CSRFGuard Project";

  private static final List<Cookie> cookies = new ArrayList<Cookie>();
  private static String csrfToken = null;

  // replace this value with the name of the connection you added to the TMC
  public static final String CONNECTION_NAME = "LocalCluster";

  // the default secured URL is : https://localhost:9443
  private static final String BASE_URL  = "http://localhost:9889";
  // the /v2 URLs adds exceptionEntities, where exceptions are stored
  private static final String AGENT_URL = BASE_URL + "/tmc/api/v2";
  private static final String LOGIN_URL = BASE_URL + "/tmc/login.jsp";


  public static void main(String[] args) {

    System.out.println("Logging in against : " + LOGIN_URL);
    login();


    String agentsUrl = AGENT_URL + "/agents";
    System.out.println("Requesting list of agents : " + agentsUrl);
    System.out.println(get(agentsUrl));


    String cacheManagersUrl = AGENT_URL + "/agents/cacheManagers";
    System.out.println("Requesting list of cache managers : " + cacheManagersUrl);
    System.out.println(get(cacheManagersUrl));

    String backupUrl = AGENT_URL + "/agents;ids=" + CONNECTION_NAME + "/backups;name=MyBackup";
    System.out.println("Triggering backup : " + backupUrl);
    System.out.println(post(backupUrl, "", "application/json"));

    System.out.println("Listing backup : " + backupUrl);
    System.out.println(get(backupUrl));


    String dgcUrl = AGENT_URL + "/agents;ids=" + CONNECTION_NAME + "/diagnostics/dgc";
    System.out.println("Triggering DGC : " + dgcUrl);
    System.out.println(post(dgcUrl, "", "application/json"));


  }

  private static void login() {
    post(LOGIN_URL, "username=admin&password=admin", "application/x-www-form-urlencoded");
  }

  static Result post(String targetURL, String requestBody, String contentType) {
    return request(RequestType.POST, targetURL, requestBody, contentType);
  }

  static Result get(String targetURL) {
    return request(RequestType.GET, targetURL, null, null);
  }

  private static Result request(RequestType requestType, String targetURL, String requestBody, String contentType) {
    String body = "";
    int responseCode = 0;
    String resultContentType = "";

    HttpURLConnection connection = null;
    try {
      URL url = new URL(targetURL);
      connection = (HttpURLConnection) url.openConnection();
      connection.setInstanceFollowRedirects(false);
      connection.setRequestProperty("Cookie", generateCookieString(cookies));
      injectCsrfTokens(connection);

      if (requestType == RequestType.POST) {
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Content-Length", Integer.toString(requestBody.getBytes().length));
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(requestBody);
        wr.close();
      }

      connection.connect();

      resfreshCsrfToken(connection);
      refreshCookies(connection);

      resultContentType = connection.getContentType();
      try {
        responseCode = connection.getResponseCode();
      } catch (FileNotFoundException e) {
        // 404
      }
      InputStream is;
      try {
        is = connection.getInputStream();
      } catch (IOException e) {
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
    return new Result(responseCode, body, resultContentType);
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

  static class Cookie {

    private final String name;
    private final String value;

    public Cookie(String name, String value) {
      this.name = name;
      this.value = value;
    }

    public String getName() {
      return name;
    }

    public String getValue() {
      return value;
    }
  }

  static String generateCookieString(List<Cookie> cookies) {
    StringBuilder result = new StringBuilder();
    for (Cookie cookie : cookies) {
      if (result.length() != 0) {
        result.append("; ");
      }
      result.append(cookie.getName() + "=" + cookie.getValue());
    }
    return result.toString();
  }

  private static void resfreshCsrfToken(HttpURLConnection connection) {
    csrfToken = connection.getHeaderField(OWASP_CSRFTOKEN);
  }

  private static void injectCsrfTokens(HttpURLConnection connection) {
    connection.setRequestProperty(X_REQUESTED_WITH, OWASP_CSRFGUARD_PROJECT);
    connection.setRequestProperty(OWASP_CSRFTOKEN, csrfToken);
  }

  private static void refreshCookies(HttpURLConnection connection) {
    String headerName;
    for (int i = 1; (headerName = connection.getHeaderFieldKey(i)) != null; i++) {
      if (headerName.equals("Set-Cookie")) {
        String cookieString = connection.getHeaderField(i);
        cookieString = cookieString.substring(0, cookieString.indexOf(";"));
        Cookie cookie = new Cookie(cookieString.substring(0, cookieString.indexOf("=")), cookieString.substring(cookieString.indexOf("=") + 1, cookieString.length()));
        cookies.add(cookie);
      }
    }
  }

}
