simlar-server
==============

[![Build Status](https://travis-ci.org/simlar/simlar-server.svg?branch=master)](https://travis-ci.org/simlar/simlar-server)


**This project is work in progress and not complete, yet.**
But after all this code runs in the simlar production environment.


[Simlar](https://www.simlar.org) is a cross platform VoIP App aiming to make ZRTP encrypted calls easy.

<!--suppress HtmlUnknownAttribute -->
<div id="screenshots" align="center">
<img src="https://www.simlar.org/press/screenshots/Android/en/talking-to-so.png" alt="Screenshot call" text-align="center" width="200" margin="15">
<img src="https://www.simlar.org/press/screenshots/iOS/ongoing_call.png" alt="Screenshot call" text-align="center" width="200">
</div>

You may start the simlar-server standalone e.g. for development.
But for a useful setup you will at least need the following servers.
Maybe some alternatives will work, too:
* [Apache Tomcat](https://tomcat.apache.org/)
* [Apache Web Server](https://httpd.apache.org/)
* [Kamailio SIP Server](https://www.kamailio.org/)


### Build dependencies ###
Java Development Kit 11

### Compile (Console) ###
```
./gradlew build
```

### Run ###
As the simlar-server is a spring-boot application you may start it with an embed tomcat server and an in-memory database.
```
./gradlew bootRun
```

### Build war ###
```
./gradlew bootWar
```

### Check dependencies ###
The simlar-server uses the [owasp-dependency-checker](https://www.owasp.org/index.php/OWASP_Dependency_Check). Execute it with:
```
./gradlew dependencyCheckAnalyze
```
The simlar-server uses the [gradle versions plugin](https://github.com/ben-manes/gradle-versions-plugin). Run it with:
```
./gradlew dependencyUpdates
```
Run both:
```
./gradlew dependencyChecks
```

## IntelliJ IDEA CE ##
We use the [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/) for development.
To generate some files for this ide run:
```
./gradlew idea
```
Then simply open (not import) the directory in IntelliJ.

### Lombok
Because the simlar-server uses the [Project Lombok](https://projectlombok.org/), IntelliJ requires the [Lombok Plugin](https://plugins.jetbrains.com/plugin/6317-lombok-plugin) to compile it.
After installing the plugin it is required to enable annotation processing in Settings/Build, Execution,Deployment/Compiler/Annotation Processors.

### Dictionary ###
In order to quiet IntelliJ's inspection warnings import the dictionary.
In Settings/Editor/Spelling choose the tab Dictionaries and add ```ides/intellij/dictionaries/simlar.dic``` to the list of Custom Dictionaries.

## Configuration
In a production environment a configuration is needed to configure e.g. the domain and the database: ```/etc/simlar-server/config.properties```
Have a look at the [example](examples/config.properties).

For development you may place your configurations in ```src/main/resources/application-default.properties```.
The [example](examples/application-default.properties) configures the database and sets a log pattern with filename and line number.
If you do not want to set up a database for development you may change the dependency type of the h2 database to ```providedRuntime```.
