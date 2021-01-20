Copyright 2015 Institute of Computer Science,
Foundation for Research and Technology - Hellas

Licensed under the EUPL, Version 1.1 or - as soon they will be approved
by the European Commission - subsequent versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl

Unless required by applicable law or agreed to in writing, software distributed
under the Licence is distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the Licence for the specific language governing permissions and limitations
under the Licence.

Contact:  POBox 1385, Heraklio Crete, GR-700 13 GREECE
Tel:+30-2810-391632
Fax: +30-2810-391638
E-mail: isl@ics.forth.gr
http://www.ics.forth.gr/isl

Authors :  Georgios Samaritakis.

This file is part of the x3mlMapper webapp.

x3mlMapper
==============

x3mlMapper is a webapp providing a single RESTful web services method to use [x3ml](https://github.com/delving/x3ml "x3ml") engine on xml input source files.

## Build - Deploy - Run
This project is a Maven project, providing all the libs in pom.xml.
Folder src contains all the files needed to build the web app and create a war file.
You may use any application server that supports war files. (Has been tested with Apache Tomcat versions 5,6,7,8).

To build with Maven on Docker, create and attach to a container:

```
docker run --rm -it --name maven -v (pwd):/x3ml maven:latest bash
```

then navigate to `/x3ml` and execute
```
maven install
```

## Configuration
Just deploy the x3mlMapper war and you are good to go.

## Usage
x3mlMapper is used by [3MEditor] (https://github.com/isl/3MEditor "3MEditor") as a plugin.
It may also be used on its own (with some tweaking).

Examples:

If you want to use x3ml Engine for a specific x3ml file stored in eXist (see [3M] (https://github.com/isl/Mapping-Memory-Manager "3M")), use:

**http://(server IP):(server port)/x3mlMapper/Index**

Parameters:
id= stored x3ml mapping file id 

uuidSize= generated uuid size, default value is 2

output= x3ml engine output type, default valus is "RDF/XML". Other values: "N-triples" or "Turtle"

sourceFile = xml input source file as a string

generator = generator policy file as a string

Read javadoc for more details.

The x3mlMapper webapp dependecies and licenses used are described in file x3mlMapper-Dependencies-LicensesUsed.txt 


