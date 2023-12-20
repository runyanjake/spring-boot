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

`mvn package && rm target/ROOT.war && mv target/template-0.0.1-SNAPSHOT.war target/ROOT.war && docker stop spring-boot_tomcat_1 && docker system prune && docker build -t spring-boot-template . && docker-compose up -d`

2. Add custom hello world endpoint `/com/base/template/endpoint/HelloWorldEndpoint.java`

Rebuild and test by curling `curl -vvv -X GET localhost:7777/helloworld`

