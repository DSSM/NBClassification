package de.htwberlin.daweb;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

/**
 * Naive Bayes Classifier wie in
 * http://nlp.stanford.edu/IR-book/html/htmledition
 * /naive-bayes-text-classification-1.html beschrieben
 * 
 * @author Daniel Schmidt
 * @author Sergej Mann
 * 
 */
public class BayesClassifier {
	private final static String[] classes = new String[] { "china", "not-china" };
	// TreeMap<term:string, TreeMap<class:string, number of term in this
	// class:long>> vocabulary
	private static TreeMap<String, TreeMap<String, Long>> vocabulary = new TreeMap<String, TreeMap<String, Long>>();
	// TreeMap<class:string, prior:double> prior
	private static TreeMap<String, Double> prior = new TreeMap<String, Double>();
	// TreeMap<class:string, TreeMap<term:string, condprob:double>> condprob
	private static TreeMap<String, TreeMap<String, Double>> condprob = new TreeMap<String, TreeMap<String, Double>>();

	private static final int SMOOTHING = 1;

	public static void main(String[] args) {
		trainMultinomialNB("training-china");

		/*
		 * Set<Entry<String, TreeMap<String, Long>>> vocabularySet =
		 * vocabulary.entrySet(); for (Entry<String, TreeMap<String, Long>>
		 * vocabularyEntry : vocabularySet) { TreeMap<String, Long> c =
		 * vocabularyEntry.getValue(); Set<Entry<String, Long>> classSet =
		 * c.entrySet(); for (Entry<String, Long> classEntry : classSet) {
		 * System.out.println(vocabularyEntry.getKey() + " => " +
		 * classEntry.getKey() + " [" + classEntry.getValue() + "]"); } }
		 */

		Set<Entry<String, Double>> priorSet = prior.entrySet();
		for (Entry<String, Double> entry : priorSet) {
			System.out.println("P(" + entry.getKey() + ") = " + entry.getValue());
		}
		System.out.println("-------------------------------------");
		Set<Entry<String, TreeMap<String, Double>>> condprobSet = condprob.entrySet();
		for (Entry<String, TreeMap<String, Double>> condprobEntry : condprobSet) {
			TreeMap<String, Double> c = condprobEntry.getValue();
			Set<Entry<String, Double>> wordSet = c.entrySet();
			for (Entry<String, Double> wordEntry : wordSet) {
				System.out.println("P(" + wordEntry.getKey() + " | " + condprobEntry.getKey()
						+ ") = " + wordEntry.getValue());
			}
		}
		System.out.println("-------------------------------------");
		applyMultinomialNB("test-china/5-test.txt");

	}

	/**
	 * Berechnet v, prior und cond. Gibt die Prior- und bedingten
	 * Wahrscheinlichkeiten f�r die Klassen bzw. Terme auf der Konsole aus.
	 * 
	 * @param trainingDir
	 *            Verzeichnis, in dem sich die Trainingsdokumente befinden
	 */
	public static void trainMultinomialNB(String trainingDir) {
		File dir = new File(trainingDir);
		if (!dir.isDirectory()) {
			return;
		}
		File[] children = dir.listFiles();
		if (children == null) {
			return;
		}
		LinkedList<File> trainingFiles = new LinkedList<File>();
		for (File child : children) {
			if (child.isFile()) {
				trainingFiles.add(child);
			}
		}
		double docNumber = (double) trainingFiles.size();
		fillVocabulary(trainingFiles);

		for (String cl : classes) {
			Double pr = prior.get(cl);
			pr = (pr == null) ? 1.0f : pr / docNumber;
			prior.put(cl, pr);
			Set<String> vocabularyKeySet = vocabulary.keySet();
			int vocabularySize = vocabulary.size();
			for (String key : vocabularyKeySet) {
				TreeMap<String, Long> valueMap = vocabulary.get(key);
				if (valueMap != null) {
					Long wordNumInClass = valueMap.get(cl);
					if (wordNumInClass == null) {
						wordNumInClass = 0L;
					}
					Long wordsNumInClass = getNumOfWordsInClassFromTrainingSet(cl);
					Double result = (wordNumInClass + SMOOTHING)
							/ (double) (wordsNumInClass + vocabularySize);
					TreeMap<String, Double> map = condprob.get(cl);
					if (map == null) {
						map = new TreeMap<String, Double>();
					}
					map.put(key, result);
					condprob.put(cl, map);
				}
			}
		}
	}

	private static long getNumOfWordsInClassFromTrainingSet(String cl) {
		Set<Entry<String, TreeMap<String, Long>>> vocabularySet = vocabulary.entrySet();
		long num = 0;
		for (Entry<String, TreeMap<String, Long>> vocabularyEntry : vocabularySet) {
			TreeMap<String, Long> c = vocabularyEntry.getValue();
			Long tmp = c.get(cl);
			if (tmp != null) {
				num += tmp.longValue();
			}
		}
		return num;
	}

	private static void fillVocabulary(LinkedList<File> trainingFiles) {
		if (!trainingFiles.isEmpty()) {
			FileInputStream fileInputStream = null;
			DataInputStream dataInputStream = null;
			for (File file : trainingFiles) {
				try {
					fileInputStream = new FileInputStream(file);
					dataInputStream = new DataInputStream(fileInputStream);
					byte[] b = new byte[dataInputStream.available()];
					dataInputStream.readFully(b);
					String fileContent = new String(b, 0, b.length);
					String[] strArray = fileContent.split(" ");
					boolean isChinaClass = file.getName().contains("yes");
					if (isChinaClass) {
						Double pr = prior.get(classes[0]);
						pr = (pr == null) ? 1.0f : pr + 1.0f;
						prior.put(classes[0], pr);
					} else {
						Double pr = prior.get(classes[1]);
						pr = (pr == null) ? 1.0f : pr + 1.0f;
						prior.put(classes[1], pr);
					}
					for (String str : strArray) {
						TreeMap<String, Long> classMap = vocabulary.get(str);
						if (isChinaClass) {
							if (classMap == null) {
								TreeMap<String, Long> tMap = new TreeMap<String, Long>();
								tMap.put(classes[0], 1L);
								vocabulary.put(str, tMap);
							} else {
								Long number = classMap.get(classes[0]);
								number = (number == null) ? 1L : number + 1;
								classMap.put(classes[0], number);
							}
						} else {
							if (classMap == null) {
								TreeMap<String, Long> tMap = new TreeMap<String, Long>();
								tMap.put(classes[1], 1L);
								vocabulary.put(str, tMap);
							} else {
								Long number = classMap.get(classes[1]);
								number = (number == null) ? 1L : number + 1;
								classMap.put(classes[1], number);
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if (dataInputStream != null) {
							dataInputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (fileInputStream != null) {
							fileInputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Berechnet, ob das Dokument zur Klasse "china" geh�rt oder nicht. Gibt die
	 * Ergebnisse auf der Konsole aus.
	 * 
	 * @param docPath
	 *            Pfad zum Dokument, das klassifiziert werden soll
	 */
	public static void applyMultinomialNB(String docPath) {
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
		try {
			fileInputStream = new FileInputStream(docPath);
			dataInputStream = new DataInputStream(fileInputStream);
			byte[] b = new byte[dataInputStream.available()];
			dataInputStream.readFully(b);
			String fileContent = new String(b, 0, b.length);
			String[] strArray = fileContent.split(" ");
			NumberFormat formatter = new DecimalFormat("###.####");
			for (String cl : classes) {
				double pr = prior.get(cl);
				TreeMap<String, Double> cp = condprob.get(cl);
				if (cp != null) {
					for (String str : strArray) {
						Double condprobValue = cp.get(str);
						if (condprobValue != null) {
							pr *= condprobValue;
						}
					}
				}
				String f = formatter.format(pr);
				System.out.println("P(" + cl + " | d5) <==> " + f);
				// System.out.println("P(" + cl + " | d5) <==> " + rp);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (dataInputStream != null) {
					dataInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
