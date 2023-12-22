# spring-boot

Simple Spring Boot project with dockerization to run a simple Tomcat webapp with some endpoints and useful framework for logging, metrics etc.

### Build from Scratch

1. Use Spring Initializr (https://start.spring.io/) to generate a starter project. Make it a Maven project, get the Java version right, and select WAR packaging.

Add spring-boot-actuator:

```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Copy into the repo and test build with `mvn package`.

Rename the generated WAR file in target/ to ROOT.war so it can be picked up by the Dockerfile, which is looking for exactly that filename.

`docker build -t spring-boot-template .` to build container, `docker-compose up -d` to start it. 

Test the actuator endpoint at custom endpoint 7777 with `curl -vvv -X GET localhost:7777/actuator | jq` 

Large command to do everything after a change to the code:

`mvn clean package && docker stop spring-boot_tomcat_1 && docker system prune && docker build -t spring-boot-template . && docker-compose up -d`

2. Add custom hello world endpoint `/com/base/template/endpoint/HelloWorldEndpoint.java`

Rebuild and test by curling `curl -vvv -X GET localhost:7777/helloworld`

3. I'm using SLF4J as a logging abstraction so I can change the underlying logging framework if I want to without a whole lot of issues. Underneath SLF4J is Log4J. 

Add the SLF4J dependency
```
<properties>
    <slf4j.version>2.0.9</slf4j.version>
</properties>
...
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>${slf4j.version}</version>
</dependency>
```
the Log4J dependencies
```
<properties>
    <log4j.version>2.17.2</log4j.version>
</properties>
...
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-api</artifactId>
    <version>${log4j.version}</version>
</dependency>
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-core</artifactId>
    <version>${log4j.version}</version>
</dependency>
```
and the Log4J-SLF4J connector dependency
```
<dependency>
    <groupId>org.apache.logging.log4j</groupId>
    <artifactId>log4j-slf4j-impl</artifactId>
    <version>${log4j.version}</version>
</dependency>
```
to the pom's dependency management. Build again to make sure there aren't multiple bindings by mistake. 

Test by running the above hello world curl again. 


