package com.example;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, DoubleWritable> {
    Set<String> aDocumentWords = new HashSet<>();
    Set<String> bDocumentWords = new Hashset<>();

    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        // Calculate Jaccard Similarity (A intersection B) / (A Union B)

        

    }
}
