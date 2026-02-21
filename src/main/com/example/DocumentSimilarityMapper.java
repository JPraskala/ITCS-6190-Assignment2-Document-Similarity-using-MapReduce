package com.example;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;

import java.io.IOException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.HashSet;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, IntWritable>  {
    private Text textKey = new Text();
    private Text textValue = new Text();
    private final IntWritable one = new IntWritable(1);

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return; // We want to ensure the blank lines are not accounted for to avoid overhead.

        String[] parts = line.split("\\s+", 2); // Splits it into 2 parts. First part contains the key (such as Document1) and the second part contains the content. 

        if (parts.length < 2) return;

        String id = parts[0];
        String content = parts[1];

        Set<String> uniqueWords = new HashSet<>();

        StringTokenizer tokenizer = new StringTokenizer(content);

        while (tokenizer.hasMoreTokens()) {
            uniqueWords.add(tokenizer.nextToken().toLowerCase());
        }

        for (String word : uniqueWords) {
            textKey.set(word);
            textValue.set(id);
            context.write(textKey, textValue);
        }
    }
}
