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

4. Add Lombok

Lombok is nice for code generation. Lots of IDEs can hide/show generated code as you want.

```
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>

```

5. Configure GCP

For Spring Boot, we have spring-cloud-gcp (https://spring.io/projects/spring-cloud-gcp/) to help us.

Include the dependencyManagement configuration that will control all further GCP modules we use:
```
<dependencyManagement>
    <dependencies>
    <dependency>
        <groupId>com.google.cloud</groupId>
        <artifactId>spring-cloud-gcp-dependencies</artifactId>
        <version>${gcp.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
    </dependencies>
</dependencyManagement>
```
and first we'll start by configuring Firestore, following this Spring Cloud documentation: https://cloud.spring.io/spring-cloud-static/spring-cloud-gcp/current/reference/html/#cloud-firestore-spring-boot-starter
```
<dependency>
    <groupId>com.google.cloud</groupId>
    <artifactId>spring-cloud-gcp-starter-data-firestore</artifactId>
</dependency>
```

Create a new project in GCP or use an existing one. Make sure there's a service account created (IAM & Admin > Serivce Accounts > Create Service Account)

Generate a service account key, following https://developers.google.com/workspace/guides/create-credentials

Generate your credentials file. To do that, in GCP go to Apis & Services > Credentials to generate it. Store it in a credentials file, for me that's src/main/resources/key.json.

In Spring Cloud, we can set the location of the credentials file in application.properties:
```
spring.cloud.gcp.credentials.location=classpath:key.json
```

Example Curls (from the same maching running the project container) using the CrudController which pushes data to a "TestDTOs" table on the default Firestore instance for the project:

Create: `curl -vvv -X POST -H "Content-Type: application/json" -d '{"name":"myName", "value1":"myValue1", "value2":"myValue2"}' localhost:7777/create`

Read: `curl -vvv -X GET localhost:7777/read/myName | jq`

Update: `curl -vvv -X PUT -H "Content-Type: application/json" -d '{"name":"myName", "value1":"myUpdatedValue1", "value2":"myValue2"}' localhost:7777/update/myName`

Delete: `curl -vvv -X DELETE localhost:7777/delete/myName`

6. Configure Prometheus Metrics

I'm begrudgingly using Micrometer for my Prometheus metrics, since it seems popular

Following: https://www.tutorialworks.com/spring-boot-prometheus-micrometer/

Add the dependencies to pom (actuator done earlier):

```
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-aop</artifactId>
</dependency>
```
`spring-boot-starter-aop` is specifically for using Micrometer Timers.

Expose a prometheus endpoint via actuator. Set this property:
```
management.endpoints.web.exposure.include=health,info,prometheus
```

Confirm the new endpoint is available under actuator:
```
curl -vvv -X GET localhost:7777/actuator/prometheus
```

Write some custom metrics (Example is CRUDMetrics.java, which exposes metrics in the CrudController endpoints.) And make sure they show up in the prometheus output.


