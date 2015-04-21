package org.pongmatcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import java.net.Authenticator;
import java.net.*;
import org.pongmatcher.proxy.*;

@ComponentScan
@Configuration
@EnableAutoConfiguration
public class Application {
    public static void main(String[] args) {
    	String staticaUrl = Statica.getUri();
    	if(staticaUrl != null){
    		System.out.println("Initializing SOCKS Proxy");
        	StaticaProxyAuthenticator proxy = new StaticaProxyAuthenticator();
	        Authenticator.setDefault(proxy.getAuth());
	        StaticaProxySelector ps = new StaticaProxySelector(ProxySelector.getDefault());
	        ProxySelector.setDefault(ps);
	    }
        SpringApplication.run(Application.class, args);
    }
}
