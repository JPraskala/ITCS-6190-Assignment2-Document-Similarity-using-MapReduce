package com.example;
import org.apache.hadoop.mapreduce.Reducer;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.DoubleWritable;
import java.io.IOException;
import java.util.*;

public class DocumentSimilarityReducer extends Reducer<Text, Text, Text, DoubleWritable> {
    private Map<String, Set<String>> wordMap = new HashMap<>();

    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

        for (Text value : values) {
            String documentId = value.toString();
            wordMap.putIfAbsent(documentId, new HashSet<>());
            wordMap.get(documentId).add(key.toString());
        }

    }

    protected void cleanup(Context context) throws IOException, InterruptedException {
        List<String> documents = new ArrayList<>(wordMap.keySet());
        Collections.sort(documents);
        for (int i = 0; i < documents.size(); i++) 
            for (int j = i + 1; j < documents.size(); j++) {
                // Here, two documents (A and B) will be put into a two sets. The sets will be used to calculate the Jaccard Similarity.
                Set<String> aDocumentWords = wordMap.get(documents.get(i));
                Set<String> bDocumentWords = wordMap.get(documents.get(j));

                // The intersection is calculated here.
                Set<String> intersection = new HashSet<>(aDocumentWords);
                intersection.retainAll(bDocumentWords);

                // The Union is calculated here.
                Set<String> union = new HashSet<>(aDocumentWords);
                union.addAll(bDocumentWords);

                // Calculate the Jaccard Similarity and round to 2 decimal places.
                double jaccardSimilarity = (double) intersection.size() / union.size();
                jaccardSimilarity = Math.round(jaccardSimilarity * 100.0) / 100.0;

                String outputStr = documents.get(i) + ", " + documents.get(j) + " Similarity:";
                context.write(new Text(outputStr), new DoubleWritable(jaccardSimilarity));
            }
    }
}
