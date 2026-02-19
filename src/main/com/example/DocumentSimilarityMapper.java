package com.example;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;

import java.io.IOException;
import java.util.StringTokenizer;

public class DocumentSimilarityMapper extends Mapper<Object, Text, Text, IntWritable>  {
    private Text text = new Text();
    private final IntWritable one = new IntWritable(1);

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
        String line = value.toString().trim();
        if (line.isEmpty()) return; // We want to ensure the blank lines are not accounted for to avoid overhead.

        String[] parts = line.split("\\s+", 2); // Splits it into 2 parts. First part contains the key (such as Document1) and the second part contains the content. 

        if (parts.length < 2) return;

        String id = parts[0];
        String content = parts[1];

        StringTokenizer tokenizer = new StringTokenizer(content);

        while (tokenizer.hasMoreTokens()) {
            String term = tokenizer.nextToken().toLowerCase(); // The toLowerCase is to avoid any case sensitivity 

            text.set(term + "@" + id);
            context.write(text, one);
        }
    }
}
