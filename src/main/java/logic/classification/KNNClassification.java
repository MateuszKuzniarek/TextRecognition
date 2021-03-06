package logic.classification;

import javafx.util.Pair;
import logic.extractors.FeatureExtractor;
import logic.metrics.Metric;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class KNNClassification
{
    private Normalizer normalizer;
    private boolean normalizationEnabled = true;
    private ArrayList<ExtractedSample> extractedSamples;
    private ArrayList<String> keywords;
    private ArrayList<TextSample> trainingTextSamples;
    private FeatureExtractor featureExtractor;
    private Metric metric;
    private int k;

    public void init(List<TextSample> samples, int numberOfKeywords)
    {
        trainingTextSamples = new ArrayList<>();
        keywords = new ArrayList<>();
        trainingTextSamples = new ArrayList<>();
        trainingTextSamples.addAll(samples);
        findKeywords(samples, numberOfKeywords);
        System.out.println(keywords);
    }

    public void train()
    {
        normalizer = new Normalizer();
        featureExtractor.initExtractor(keywords);
        extractedSamples = new ArrayList<>();
        for(TextSample sample : trainingTextSamples)
        {
            extractedSamples.add(featureExtractor.extractFeatures(trainingTextSamples, sample));
        }
        if(isNormalizationEnabled())
        {
            normalizer.initializeWages(extractedSamples);
            for(int i = 0; i< extractedSamples.size(); i++)
            {
                normalizer.normalize(extractedSamples.get(i));
            }
        }
    }

    public String classify(TextSample testSample)
    {
        String result = "";
        ExtractedSample testExample = featureExtractor.extractFeatures(trainingTextSamples, testSample);
        if(isNormalizationEnabled()) normalizer.normalize(testExample);
        extractedSamples.sort(Comparator.comparingDouble(a -> metric.getDistance(a.getFeatures(), testExample.getFeatures())));
        HashMap<String, Integer> answers = new HashMap<String, Integer>();
        for(int i=0; i<k; i++)
        {
            String label = extractedSamples.get(i).getLabel();
            if(answers.containsKey(label)) answers.put(label, answers.get(label)+1);
            else answers.put(label, 1);
        }
        int maxFrequency = 0;
        for(Map.Entry<String, Integer> entry : answers.entrySet())
        {
            if(maxFrequency<entry.getValue())
            {
                result = entry.getKey();
                maxFrequency = entry.getValue();
            }
        }
        return result;
    }

    private ArrayList<String> getLabels(List<TextSample> samples)
    {
        ArrayList<String> labels = new ArrayList<>();
        for(TextSample sample : samples)
        {
            if(!labels.contains(sample.getLabels().get(0))) labels.add(sample.getLabels().get(0));
        }
        return labels;
    }

    private HashMap<String, ArrayList<String>> getWordsForLabel(List<TextSample> samples, List<String> labels)
    {
        HashMap<String, ArrayList<String>> wordsForLabel = new HashMap<>();
        for(String label : labels) wordsForLabel.put(label, new ArrayList<>());

        for(TextSample sample : samples)
        {
            wordsForLabel.get(sample.getLabels().get(0)).addAll(sample.getWords());
        }
        return wordsForLabel;
    }

    private boolean checkForWord(String word, ArrayList<Pair<String, Integer>> words)
    {
        for(Pair<String, Integer> pair : words)
        {
            if(word.equals(pair.getKey())) return true;
        }
        return false;
    }

    public void findKeywordsPerClass(List<TextSample> samples, int n)
    {
        ArrayList<String> labels = getLabels(samples);
        HashMap<String, ArrayList<String>> wordsForLabel = getWordsForLabel(samples, labels);
        for(String label : wordsForLabel.keySet())
        {
            ArrayList<Pair<String, Integer>> words = new ArrayList<>();
            for(String word : wordsForLabel.get(label))
            {
                if(!checkForWord(word, words))
                {
                    words.add(new Pair(word, Collections.frequency(wordsForLabel.get(label), word)));
                }
            }
            Collections.sort(words, Comparator.comparingInt(Pair::getValue));
            for(int i=0; i<n; i++) keywords.add(words.get(words.size()-1-i).getKey());
        }
    }

    public void findKeywords(List<TextSample> samples, int n)
    {
        ArrayList<Pair<String, Integer>> words = new ArrayList<>();
        for(TextSample sample : samples)
        {
            for(String word : sample.getWords())
            {
                if(!checkForWord(word, words))
                {
                    words.add(new Pair(word, Collections.frequency(words, word)));
                }
            }
        }
        Collections.sort(words, Comparator.comparingInt(Pair::getValue));
        for(int i=0; i<n; i++) keywords.add(words.get(words.size()-1-i).getKey());
    }
}
