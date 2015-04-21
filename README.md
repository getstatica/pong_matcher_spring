# CF example app: ping-pong matching server

This is an app to match ping-pong players with each other. It's currently an
API only, so you have to use `curl` to interact with it.

It has an [acceptance test suite][acceptance-test] you might like to look at.

This app has been modified to route database traffic via a Static IP through the [Statica][statica] service using a SOCKS proxy. Try out Statica for free on Pivotal by creating a free account on the [Pivotal Marketplace Statica page][pivotal-statica].

## Statica Modifications
A **proxy** package has been added containing the necessary configuration classes to selectively route traffic via a Static IP.

#### Statica.java
A convenience class to pick up the STATICA_URL connection string from either the VCAP_SERVICES environment variable or the STATICA_URL environment variable

#### StaticaProxyAuthenticator.java
Responsible for pulling the authentication details out of the connection string and setting required System properties.

#### StaticaProxySelector.java
Selectively routes traffic via the proxy by matching the destination with the specified destinations in the STATICA_MASK environment variable.

e.g. If you only wanted to route your database traffic via a Static IP and the database was hosted on *us-cdbr-iron-east-02.cleardb.net* you would run this locally:

    export STATICA_MASK=us-cdbr-iron-east-02.cleardb.net

or on Pivotal:

    cf set-env STATICA_MASK us-cdbr-iron-east-02.cleardb.net

Multiple destinations can be specified in a comma separated list.

#### Application.java
To include the proxy in your application you need to set them up on Application initialization. In a Spring Boot application this can be done in your Application.java, e.g.

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

## Running on [Pivotal Web Services][pws]

Log in.

```bash
cf login -a https://api.run.pivotal.io
```

Target your org / space.

```bash
cf target -o myorg -s myspace
```

Sign up for a cleardb instance.

```bash
cf create-service cleardb spark mysql
```

Sign up for a statica instance.

```bash
cf create-service statica starter statica
```

Set the STATICA_MASK to match your cleardb hostname (get from command line ```cf env pong_matcher_spring | grep jdbcUrl```)

```bash
cf set-env STATICA_MASK us-cdbr-iron-east-02.cleardb.net
```

Build the app.

```bash
brew install maven
mvn package
```

Push the app. Its manifest assumes you called your ClearDB instance 'mysql' and Statica instance 'statica'.

```bash
cf push -n mysubdomain
```

Export the test host

```bash
export HOST=http://mysubdomain.cfapps.io
```

Now follow the [interaction instructions][interaction].

All database traffic should now be routed via your Static IP addresses.

## Running locally

The following assumes you have a working Java 1.8 SDK installed.

** NB You cannot use Statica to access a local db as your local database is not accessible from our proxy servers. To test locally modify your application.yml to point to your public ClearDB instance.**

Install and start mysql:

```bash
brew install mysql
mysql.server start
mysql -u root
```

Create a database user and table in the MySQL REPL you just opened:

```sql
CREATE USER 'springpong'@'localhost' IDENTIFIED BY 'springpong';
CREATE DATABASE pong_matcher_spring_development;
GRANT ALL ON pong_matcher_spring_development.* TO 'springpong'@'localhost';
exit
```

Start the application server from your IDE or the command line:

```bash
mvn spring-boot:run
```

Export the test host

```bash
export HOST=http://localhost:8080
```

Now follow the [interaction instructions][interaction].

[acceptance-test]:https://github.com/camelpunch/pong_matcher_acceptance
[pws]:https://run.pivotal.io
[interaction]:https://github.com/camelpunch/pong_matcher_grails#interaction-instructions
[statica]:https://www.statica.io
[pivotal-statica]:https://console.run.pivotal.io/marketplace/statica