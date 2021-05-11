package AVIB.SparkGitToNeo4j;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import database.Connector;

public class AvibSparkContext {
	private static AvibSparkContext instance;
	private JavaSparkContext context;
	
	private AvibSparkContext() {
		SparkConf conf = new SparkConf().setAppName("avib-spark").setMaster("local[*]").set("spark.executor.memory", "4g").
                set("spark.driver.host", "127.0.0.1").set("spark.driver.bindAddress", "127.0.0.1");
		setContext(new JavaSparkContext(conf));
		getContext().setLogLevel("WARN");

	}
	
	public static AvibSparkContext getInstance() {
		if(instance  == null) {
			synchronized (Connector.class) {
				if (instance==null) {
					instance = new AvibSparkContext();
				}
			}
		}
		return instance;
	}
	
	public void closeSpark() {
		this.context.close();
	}

	public JavaSparkContext getContext() {
		return context;
	}

	public void setContext(JavaSparkContext context) {
		this.context = context;
	}
	
	
	
}
