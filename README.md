TODO:
* more documentation
* Araresolver webservice aufrufe hart hinterlegen. nur hostname wird übergeben.

HINTS:
* export HTTPS_PROXY=...
* GIT_SSL_NO_VERIFY=true  git push

PACIFy (PAckage ConfIguration Framework)
=============
PACIFy is a tool, written in java, which can be used to get a step further in the devops movement. It solves the problem of *How do i have to configure your package?*.

What problem's does PACIFy solve?
------------
1. The dev team has written a nice install documentation and specified all configuration elements which the package contains. But the op's team didn't read it and *missed* "that new configuration".
1. The op's team *forgot* to do a configuration in a file.
1. The op's team *destroyed* the *encoding* of file x.y when they replaced the placeholder.
1. The op's team *changed* the *line endings* of file x.y when they replaced the placeholder.
1. The op's team reports *deployment successfully completed* but the app shows some really strange behavior. The dev team digged some hours and the result? One of the previous mentioned point.
1. Perhaps you are on the devops train already and are using ansible, chef and/or puppet than you have to change the workflow, if the dev team adds some new properties or removes them.

With PACIFy all of this problems are non-existent!

How does PACIFy work?
------------
Instead of writing a document where the dev team describes what placeholder have to be replaced, the dev team provides a PACIFy marker file (xml) - or many of them - which contains the information. The marker file looks like:

    ```xml
    <Pacify>
        <Property Name="log.level">
            <File RelativePath="config/log4j.xml"/>
        </Property>
        <Property Name="jdbc.url" >
            <File RelativePath="datasource/config.xml"/>
            <File RelativePath="web/infopage.html"/>
        </Property>
    </Pacify>
    ```

and is a part of your package. For the ops team it's now really easy to configure that package for a specific environment. They call PACIFy via:

    ```sh
    pacify replace --resolvers=FileResolver -RFileResolver.file=prod.properties --envName=production --packagePath=/share/app/v1.0
    ```

After that call, the package under `/share/app/v1.0` is now configured for production.

What is PACIFy doing for you?
------------
Really nice stuff ....
TODO: write down all the gimmiks!

Requirement's
------------
* Java >= 1.6

Commandline Examples
------------
###### Help
Show the usage of PACIFy.
```java -jar pacify.jar --help```

###### validate
Validate that the given marker files are correct, that all defined placeholder exists and that all needed properties are defined in the production.properties.
```java -jar pacify.jar validate             --packagePath=/share/app/v1.0 --resolvers=FileResolver -RFileResolver.file=production.properties```

###### validateMarkerFiles
Validate that the given marker files are correct and that all defined placeholder exists.
```java -jar pacify.jar validateMarkerFiles  --packagePath=/share/app/v1.0```

###### createPropertyFile
Use the given resolvers and create the final property file with all tokens replaced.
Write to stdout:
```java -jar pacify.jar createPropertyFile   --resolvers=FileResolver             -RFileResolver.file=production.properties```
```java -jar pacify.jar createPropertyFile   --resolvers=CmdResolver,FileResolver -RCmdResolver.log.level=DEBUG -RFileResolver.file=production.properties```

Write to a file:
```java -jar pacify.jar createPropertyFile   --resolvers=FileResolver -RFileResolver.file=production.properties --RestinationFile=productionAllPropertiesResolved.properties```

###### replace
Copy the original package to */share/app/v1.0_production*, replace all configuration elements using the given properties.
```java -jar pacify.jar replace  --resolvers=FileResolver -RFileResolver.file=production.properties --envName=production --packagePath=/share/app/v1.0```

Replace all configuration elements using the given properties and don't copy it.
```java -jar pacify.jar replace  --resolvers=FileResolver -RFileResolver.file=production.properties --envName=production --packagePath=/share/app/v1.0 --createCopy=false```

###### Commandline Parameter
You can specify the parameters for a command in a file via *@*, e.g.:
```java -jar pacify.jar replace @replace.properties```

