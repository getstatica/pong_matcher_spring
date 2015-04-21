package org.pongmatcher.proxy;

import com.jayway.jsonpath.JsonPath;

public class Statica {

    public static String getUri() {
        String connectionUrl = getUriFromVcapServices();
        if(connectionUrl == null){
            connectionUrl = getUriFromEnv();
        }
        return connectionUrl;
    }

    private static String getUriFromVcapServices(){
        String vcap_json = System.getenv("VCAP_SERVICES");        
        if(vcap_json !=null && vcap_json != "") {
          System.out.println("Attempting to get STATICA_URL from VCAP_SERVICES...");
          String connectionUrl = JsonPath.read(vcap_json, "$.statica[0].credentials.STATICA_URL");
          System.out.println(connectionUrl); 
          return connectionUrl;
        }
        return null;
    }

    private static String getUriFromEnv(){
        System.out.println("Attempting to get STATICA_URL from environment variable...");
        return System.getenv("STATICA_URL");
    }
}