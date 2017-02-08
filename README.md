#QueryIO

##A big data analytics solution
QueryIO is a Hadoop-based SQL and Big Data Analytics solution, used to store, structure, analyze and visualize vast amounts of structured and unstructured Big Data. It is especially well suited to enable users to process unstructured Big Data,	give it a structure and support querying and analysis of this Big Data using standard SQL syntax.
Check out the [QueryIO Website](http://queryio.com/) for more details and how-to videos.

## Features

* Easy to use.
* Scalable, supporting clusters of arbitrary sizes.
* Cluster management and monitoring services.
* Enhancements to Apache Hive to support additional features.
* A variety of inbuilt functions for data parsing, tagging and processing.
* Advanced reporting and querying features.

## Requirements

* Java 1.7 or newer.
* JAVA_HOME must be set on all machines in cluster to ensure intended java version is used while running services.
* All machines in the cluster to have same user accounts to ensure Hadoop services work seamlessly with system accounts.

## Installation

The best way to install QueryIO is to build from source:

`git clone https://github.com/ap-qio/queryio.git`

`cd queryio/queryio-parent`

`mvn clean install`

`cp buildRoot/build <installation-path>`

cd `<installation-path>/bin`

`sh install.sh`

You can also download a stable latest build from [Downloads Page](http://queryio.com/download/big-data-analytics-download.php).

## Issues and support

Issues during installation? Need an enhancement/new feature? Find all details to contact on [Contacts Page](http://queryio.com/company/contact-us.html)
