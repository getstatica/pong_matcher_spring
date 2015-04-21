package org.pongmatcher.proxy;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

public class StaticaProxyAuthenticator extends Authenticator{
  private String user, password, host;  
  private int port;
  private ProxyAuthenticator auth;

  public StaticaProxyAuthenticator() {
    String proxyUrlEnv = Statica.getUri();
    if(proxyUrlEnv!=null){
      try {
        URL proxyUrl = new URL(proxyUrlEnv);
        String authString = proxyUrl.getUserInfo();
        user = authString.split(":")[0];
        password = authString.split(":")[1];
        host = proxyUrl.getHost();
        port = 1080;    
        auth = new ProxyAuthenticator(user,password);
    //    setProxy();
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }     
    }
    else{
      System.err.println("You need to set the environment variable STATICA_URL!");
    }
    
    }
   
    private void setProxy(){
      System.setProperty("socksProxyHost", host);
      System.setProperty("socksProxyPort", String.valueOf(port));
      System.setProperty("java.net.socks.username", user);
      System.setProperty("java.net.socks.password", password);
    }
   
    public String getEncodedAuth(){
      //If not using Java8 you will have to use another Base64 encoded, e.g. apache commons codec.
      String encoded = org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString((user + ":" + password).getBytes());      
      return encoded;
    }

    public ProxyAuthenticator getAuth(){
      return auth;
    }
    
    class ProxyAuthenticator extends Authenticator {

        private String user, password;

        public ProxyAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password.toCharArray());
        }
    }
    
}