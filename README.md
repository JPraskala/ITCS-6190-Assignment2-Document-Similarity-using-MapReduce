# Assignment 2: Document Similarity using MapReduce

**Name:** Jacob Praskala

**Student ID:** 800989838

## Approach and Implementation

### Mapper Design
[Explain the logic of your Mapper class. What is its input key-value pair? What does it emit as its output key-value pair? How does it help in solving the overall problem?]

The Mapper class takes the input file and prepares all the building blocks needed to calculate the Jaccard Similarity in the Reducer class. Its input key-value pair is an Object containing the key, such as Document1, and a Text object containing one or more words. In the map method, for any lines that are not blank, the Mapper splits the line into two parts, with the first containing the key and the second part containing the content. The content part is then passed into a StringTokenizer object because StringTokenizer breaks up a piece of text into individual words or tokens, making it easy for the Mapper to read. After gathering the unique words into a set of Strings, the context variable then calls the write function, which is the output key-value pair. The output key-value pair are both Text objects with the key containing all distinct words, and the value containing the document id. The reason the output is setup this way is because of the way Hadoop shuffles the data; it shuffles it in a way that makes it easy for the Reducer to calculate the Jaccard Similarity. What is being built is known as an inverted index, and an example is cat -> [doc1, doc2]; this example says cat is present in documents 1 and 2. If it were to be flipped, you would get all the words in a document, but it would not tell you which documents share a word.


### Reducer Design
[Explain the logic of your Reducer class. What is its input key-value pair? How does it process the values for a given key? What does it emit as the final output? How do you calculate the Jaccard Similarity here?]

The Reducer class calculates the Jaccard Similarity itself. Its input key-value pairs are both Text Objects, which were created from the Mapper class. In global memory, a Map is created with a String as the key and a set of Strings as the value. In the reduce method, it loops through all the values and passes it into the Map; it does this by adding the value to the Map as a key if it doesn't exist, and it adds the key parameter variable as the value in the Map. By formatting the Map this way, it will hold the information about which documents the word exists in. For example, docA -> [word1, word2] could be an entry in the Map. 

In the cleanup method, the keyset in the map is first passed into an ArrayList of type String for easy access, and then the Collections.sort method is called on the ArrayList so documents 1 and 2 are processed first. After the method is called, two loops are created. Inside these loops, two sets of type String are created, containing the words in document A and the other in document B. The program first calculates the intersection by creating another set containing the words from document A, and then calling the retainAll method to get words from document B that are also in document A. Calculating the union is similar, except the addAll method is called, which adds all the words from document B into the union set. 

The Jaccard Similarity is then calculated by taking the size of the intersection set and dividing it by the size of the union set. To round the answer to 2 decimal places, I took the result, multiplied it by 100, and then divided it by 100. The reason I did it this way is that Java's round function always rounds to the nearest whole number; it does not allow the programmer to specify how many decimal places to round to like Python's round function does. Because of this, the way I did it is the easiest way to ensure the answer is rounded to 2 decimal places in Java. Lastly, the context variable then calls the write function by passing a Text Object as the key containing the formatting in the output file, and the value is of type DoubleWritable.   

### Overall Data Flow
[Describe how data flows from the initial input files, through the Mapper, shuffle/sort phase, and the Reducer to produce the final output.]

In this MapReduce Document Similarity Analysis, the Driver controls the flow of the program as it contains the main method. In the Driver, I created a Configuration Object and then a Job Object specifying the configuration variable and the name of the job, which, in my case, I named "Document Similarity". After setting the classes, map output key/values, and the set output key/values, the program then reads from the input file, which resides in the shared-folder directory inside the input directory. The input file is called small_dataset.txt, which contains 5 documents and some basic words.

The text in the input file is then processed by the Mapper, which splits each line or document into 2 parts as specified before. Once the Mapper performs its computations and outputs the necessary format, the Reducer then takes the output from the Mapper and uses it to calculate the Jaccard Similarity for the final output. The shuffle/sort phase is divided into two parts. The first part is in the Mapper, which builds an inverted index with the word as the key and the documents in which the word is present as the values. The second part is in the Reducer class, which uses a Map to store the document id as the key and the words the document contains as the values. When executed, the output tells the user which documents were compared and how similar they are. For example, ```Document5, Document4 Similarity:  0.5``` says document 4 and document 5 are 50% similar to each other. Additionally, ```Document4, Document3 Similarity:  0.0``` says documents 4 and 3 are not similar whatsoever. 


---

## Setup and Execution

### ` Note: The below commands are the ones used for the Hands-on. You need to edit these commands appropriately towards your Assignment to avoid errors. `

### 1. **Start the Hadoop Cluster**

Run the following command to start the Hadoop cluster:

```bash
docker compose up -d
```

### 2. **Build the Code**

Build the code using Maven:

```bash
mvn clean package
```

### 4. **Copy JAR to Docker Container**

Copy the JAR file to the Hadoop ResourceManager container:

```bash
docker cp target/WordCountUsingHadoop-0.0.1-SNAPSHOT.jar resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 5. **Move Dataset to Docker Container**

Copy the dataset to the Hadoop ResourceManager container:

```bash
docker cp shared-folder/input/data/input.txt resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 6. **Connect to Docker Container**

Access the Hadoop ResourceManager container:

```bash
docker exec -it resourcemanager /bin/bash
```

Navigate to the Hadoop directory:

```bash
cd /opt/hadoop-3.2.1/share/hadoop/mapreduce/
```

### 7. **Set Up HDFS**

Create a folder in HDFS for the input dataset:

```bash
hadoop fs -mkdir -p /input/data
```

Copy the input dataset to the HDFS folder:

```bash
hadoop fs -put ./input.txt /input/data
```

### 8. **Execute the MapReduce Job**

Run your MapReduce job using the following command: Here I got an error saying output already exists so I changed it to output1 instead as destination folder

```bash
hadoop jar /opt/hadoop-3.2.1/share/hadoop/mapreduce/WordCountUsingHadoop-0.0.1-SNAPSHOT.jar com.example.controller.Controller /input/data/input.txt /output1
```

### 9. **View the Output**

To view the output of your MapReduce job, use:

```bash
hadoop fs -cat /output1/*
```

### 10. **Copy Output from HDFS to Local OS**

To copy the output from HDFS to your local machine:

1. Use the following command to copy from HDFS:
    ```bash
    hdfs dfs -get /output1 /opt/hadoop-3.2.1/share/hadoop/mapreduce/
    ```

2. use Docker to copy from the container to your local machine:
   ```bash
   exit 
   ```
    ```bash
    docker cp resourcemanager:/opt/hadoop-3.2.1/share/hadoop/mapreduce/output1/ shared-folder/output/
    ```
3. Commit and push to your repo so that we can able to see your output


---

## Challenges and Solutions

[Describe any challenges you faced during this assignment. This could be related to the algorithm design (e.g., how to generate pairs), implementation details (e.g., data structures, debugging in Hadoop), or environmental issues. Explain how you overcame these challenges.]

During this assignment, the biggest challenge I faced was understanding the MapReduce data flow. When I started the assignment, I was not entirely sure how the Mapper and the Reducer work. I initially thought the Mapper would calculate the Jaccard Similarity, but I later found out the Reducer is supposed to handle that calculation. I figured it out by using the Hands-On assignment as a guide on how to structure my files and using internet resources to learn more about how MapReduce works and what is expected in each function. I believe this assignment helped me gain a fundamental knowledge of MapReduce. If I encounter a situation where I need to use it, I will be a lot more comfortable implementing it compared to before. 

The second and final issue I faced was executing the program itself on the command line. I was able to create the Docker Container, but I could not execute my MapReduce job, and it puzzled me for a significant amount of time. What I discovered was that the src directory was lacking the java folder, which caused the problem of the Docker command line not finding any of my classes. After creating a Java directory inside the src, I was able to successfully execute the MapReduce job and get my output. 

---
## Sample Input

**Input from `small_dataset.txt`**
```
Document1 This is a sample document containing words
Document2 Another document that also has words
Document3 Sample text with different words
```
## Sample Output

**Output from `small_dataset.txt`**
```
"Document1, Document2 Similarity: 0.56"
"Document1, Document3 Similarity: 0.42"
"Document2, Document3 Similarity: 0.50"
```
## Obtained Output: (Place your obtained output here.)
```
Document1, Document2 Similarity:	0.67
Document1, Document3 Similarity:	0.67
Document1, Document4 Similarity:	0.0
Document1, Document5 Similarity:	0.0
Document2, Document3 Similarity:	0.33
Document2, Document4 Similarity:	0.0
Document2, Document5 Similarity:	0.0
Document3, Document4 Similarity:	0.0
Document3, Document5 Similarity:	0.0
Document4, Document5 Similarity:	0.5

```
