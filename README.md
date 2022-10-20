PACIFy
=============
PACIFy (**Pa**ckage **C**onf**i**guration **F**ramework) is a tool, written in java, which can be used to get a step further in the devops movement. It solves the problem of *How do i have to configure your package?*.

# What problem's does PACIFy solve?
1. The dev team has written a nice install documentation and specified all configuration elements which the package contains. But the op's team didn't read it and **missed** that new **configuration**.
1. The op's team **forgot** to do a configuration in a file.
1. The op's team **destroyed** the **Encoding** of file x.y when they replaced the placeholder.
1. The op's team **changed** the **line endings** of file x.y when they replaced the placeholder.
1. The op's team reports **deployment successfully completed** but the app shows some really strange behavior. The dev team digged some hours and the result? One of the previous mentioned point.
1. Perhaps you are on the devops train already and are using ansible, chef and/or puppet than you have to **change the workflow**, if the dev team adds some new properties or removes them.

With PACIFy all of this problems are non-existent!

# How does it work?

Instead of writing a word/pdf/... document where the dev team describes what placeholder have to be replaced, the dev team provides a PACIFy configuration marker file:  

    deployment-package-app-v1.0
       |- config
       |   |- log4j.xml
       |   |- ...
       |- datasource
       |   |- config.xml
       |   |- ...
       |- web
       |   |- infopage.html 
       |   |- ...
       |- server
       |   |- app.ear
       |   |- ... 
       |- app-CMFile.pacify        <-- Configuration Marker File

which contains the information and is part of your package. This marker file looks like:

    <Pacify>
        <File RelativePath="config/log4j.xml">
            <Property Name="log.level"/>
        </File>
        <File RelativePath="datasource/config.xml">
            <Property Name="jdbc.url"/>
        </File>            
        <File RelativePath="web/infopage.html">
            <Property Name="jdbc.url"/>
        </File>
        <Archive RelativePath="server/app.ear">
            <File RelativePath="META-INF/weblogic-application.xml">
                <Property Name="thread.count"/>
            </File>
        </Archive>
    </Pacify>

and is based on a [schema](https://github.com/cecom/PACIFy/blob/master/model/src/main/resources/pacify.xsd). You don't have to specify all configuration elements in one configuration marker file. You can create as many as you want on any location within the package. 
   
For the ops team it's now really easy to configure that package for a specific environment. They provide the values for the environment and call PACIFy:

    java -jar pacify.jar replace                                \
           --packagePath=/share/app/deployment-package-app-v1.0 \
           ...=prod.properties

If PACIFy successfully finished his work you can be sure that the package is now configured and nothing is missing! In this example, the op's team used a property file to resolve the properties, but PACIFy can handle more.

Have a look at the [wiki](https://github.com/cecom/PACIFy/wiki) for more information.
