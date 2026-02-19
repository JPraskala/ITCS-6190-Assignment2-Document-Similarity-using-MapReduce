package com.example;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import java.util.Map;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, DoubleWritable>  {
    private Text content = new Text();
    private DoubleWritable similarityValue = new DoubleWritable();
    // This will hold the vectors we need to calculate the simarilities 
    Map<Integer, Map<String, Integer>> vectors = new HashMap<>();
    
    public void Map(Object key, Text value, Context context) throws IOException, InterruptedException {

    }

    
}
