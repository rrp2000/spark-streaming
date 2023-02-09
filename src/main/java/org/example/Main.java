package org.example;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        try{
            SparkConf conf = new SparkConf().setMaster("local[*]").setAppName("WordCount");
            JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

            JavaDStream<String> lines = jssc.socketTextStream("localhost", 9999);
            JavaDStream<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
//            words.print();
//            JavaDStream<Long> wordCount = words.count();
            JavaPairDStream<String, Integer> pairs = words.mapToPair(s -> new Tuple2<>(s, 1));
            JavaPairDStream<String, Integer> wordCount = pairs.reduceByKey((i1, i2) -> i1 + i2);
            wordCount.print();
//            pairs.print();

            jssc.start();
            jssc.awaitTermination();

        }catch (Exception e){
            System.out.println( e.getMessage());
        }
    }
}