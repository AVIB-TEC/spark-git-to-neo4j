# spark-git-to-neo4j
This repository contains the extraction of the data from git to be inserted in Neo4j using Spark.

To run the cointiner you can use the following command ` docker run --rm -it  -p 4567:4567/tcp  -p 4040:4040/tcp  olbapd/spark-test:latest`

If you wish to test locally make sure to change the connection type. To do this go the the `Connector.java` class on the constructor. A comment indicating which line to uncomment will be found there. 