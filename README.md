# cics-java-osgi-mq-jms

Sample artifacts for "Using MQ JMS in an OSGi JVM server" article referenced below.

## Pre-reqs

    CICS TS V5.2 or later
    CICS Explorer
    MQ version 7.1 or 8
    Exact details provided in article referenced below

## Installation

Add the two java files in the sample/jms package and the MANIFEST.MF file in the META-INF directory into a project in your CICS Explorer. 
Place the .bindings file onto your HFS ensuring that when you upload it using FTP you use the binary
option to keep it in ASCII format.
Update QAdd.java so that this line "environment.put(Context.PROVIDER_URL, "file:///u/mleming/jndi/");" 
points to the .bindings file.

## Reference

Code originally developed for this [article](https://github.com/cicsdev/blog-cics-java-mq-jms-osgi/blob/main/blog.md) which describes the full set up required

## License
This project is licensed under [Apache License Version 2.0](LICENSE). 
