package de.htwberlin.daweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Naive Bayes Classifier wie in
 * http://nlp.stanford.edu/IR-book/html/htmledition
 * /naive-bayes-text-classification-1.html beschrieben
 * 
 * @author
 * 
 */
public class BayesClassifier {
	private final static String[] classes = new String[] { "china", "not-china" };
	private static LinkedHashMap<String, Long> v = new LinkedHashMap<String, Long>();
	private static LinkedList<File> trainingFiles = new LinkedList<File>();
	// private static ... prior = ...;
	// private static ... condprob = ...;

	public static void main(String[] args) {
		trainMultinomialNB("training-china");
		applyMultinomialNB("test-china/5-test.txt");
		Set<Entry<String, Long>> set = v.entrySet();
		for (Entry<String, Long> entry : set) {
			System.out.println(entry.getKey() + " [" + entry.getValue() + "]");
		}
		
	}

	/**
	 * Berechnet v, prior und cond. Gibt die Prior- und bedingten
	 * Wahrscheinlichkeiten für die Klassen bzw. Terme auf der Konsole aus.
	 * 
	 * @param trainingDir
	 *            Verzeichnis, in dem sich die Trainingsdokumente befinden
	 */
	public static void trainMultinomialNB(String trainingDir) {
		File dir = new File(trainingDir);
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			if (children != null) {
				for (File child : children) {
					if (child.isFile()) {
						trainingFiles.add(child);
					}
				}
			}
		}
		scanTrainingFile();
		int docCount = trainingFiles.size();
	}

	private static void scanTrainingFile() {
		if (!trainingFiles.isEmpty()) {
			FileReader fr = null;
			BufferedReader br = null;
			for (File file : trainingFiles) {
				try {
					fr = new FileReader(file);
					br = new BufferedReader(fr);
					String line;
					while ((line = br.readLine()) != null) {
						String[] array = line.split(" ");
						for (String str : array) {
							if (v.containsKey(str)) {
								Long count = v.get(str);
								count++;
								v.put(str, count);
							} else {
								v.put(str, 1L);
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Berechnet, ob das Dokument zur Klasse "china" gehört oder nicht. Gibt die
	 * Ergebnisse auf der Konsole aus.
	 * 
	 * @param docPath
	 *            Pfad zum Dokument, das klassifiziert werden soll
	 */
	public static void applyMultinomialNB(String docPath) {

	}

}
