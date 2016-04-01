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

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Anthony Dahanne on 2016-03-31.
 */
public class IgnoreSslTmcWithAuthExamples extends TmcWithAuthExamples {



  private static final String BASE_URL  = "https://localhost:9443";
  private static final String AGENT_URL = BASE_URL + "/tmc/api/v2";
  private static final String LOGIN_URL = BASE_URL + "/tmc/login.jsp";

  // this code can be convenient for testing, but think twice before using it in production !
  static {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }
          public void checkClientTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {
          }
          public void checkServerTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {
          }
        }
    };

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
    }

  }

  public static void main(String[] args) {

    System.out.println("Logging in against : " + LOGIN_URL);
    login();


    String agentsUrl = AGENT_URL + "/agents";
    System.out.println("Requesting list of agents : " + agentsUrl);
    System.out.println(get(agentsUrl));


    String cacheManagersUrl = AGENT_URL + "/agents/cacheManagers";
    System.out.println("Requesting list of cache managers : " + cacheManagersUrl);
    System.out.println(get(cacheManagersUrl));

    String backupUrl = AGENT_URL + "/agents;ids=LocalCluster/backups;name=MyBackup";
    System.out.println("Triggering backup : " + backupUrl);
    System.out.println(post(backupUrl, "", "application/json"));

    System.out.println("Listing backup : " + backupUrl);
    System.out.println(get(backupUrl));


    String dgcUrl = AGENT_URL + "/agents;ids=LocalCluster/diagnostics/dgc";
    System.out.println("Triggering DGC : " + dgcUrl);
    System.out.println(post(dgcUrl, "", "application/json"));


  }

  static void login() {
    post(LOGIN_URL, "username=admin&password=admin", "application/x-www-form-urlencoded");
  }

}
