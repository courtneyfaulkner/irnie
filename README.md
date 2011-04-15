Irnie is an effort to update the [birt-jpa-connector project](code.google.com/p/birt-jpa-connector/) project. The [birt-jpa-connector project](code.google.com/p/birt-jpa-connector/) project is an Open Data Access (ODA) driver and UI plugin for the [Eclipse](www.eclipse.org) [BIRT](www.eclipse.org/birt) system. This project was started by V. Alfonso Phocco Diaz as a Google Summer of Code project in 2008. The last commit for the Google Code project is from 2008, and the code is targeted for Eclipse 3.4. Since then, Eclipse has added additional required methods to their ODA API, meaning the last version of birt-jpa-connector does not work in Eclipse 3.6.

The goal for Irnie is to update the birt-jpa-connector to support JPA 2.0 in Eclipse Helios (3.6). Branching methodology will follow Vincent Driessen's branching model (nicely implemented at https://github.com/nvie/gitflow).


Information taken from [birt-jpa-connector project](code.google.com/p/birt-jpa-connector/):

> The Java Persistence API (JPA)is the Java API for the management of persistence and object/relational mapping for Java EE and Java SE environments. The persistence consists of three areas:

> * the API, defined in the javax.persistence package
> * the Java Persistence Query Language
> * object/relational metadata

> The purpose of this project proposal is to implement a Connector JPA for BIRT framework, and so able to handle data source from the persistence API. Is so great the importance of the project because JPA is a specification that is being used widely in the development of applications, precisely from the advantages it.

> The implementation is to make the JPA ODA driver, extending each one of the interface required in the Data Tools Plataform given in the package org.eclipse.datatools.connectivity.oda.

> Finalizing it first part will be an improvement approaching also advanced options in regard to the parameters and also will be implemented JPA ODA IU extensions.

> More information too, here: http://wiki.eclipse.org/About_:_BIRT_JPA_or_JDO_Connector

> Author: V. Alfonso Phocco Diaz vphocco@gmail.com , alfonso7@ime.usp.br National University of San Agustin - Engineer Systems( Computer Science)