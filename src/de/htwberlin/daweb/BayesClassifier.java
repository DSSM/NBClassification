package de.htwberlin.daweb;

/**
 * Naive Bayes Classifier wie in
 * http://nlp.stanford.edu/IR-book/html/htmledition/naive-bayes-text-classification-1.html
 * beschrieben
 * 
 * @author 
 *
 */
public class BayesClassifier {
  // private static String[] classes = new String[] {"china", "not-china"};
  // private static ... v = ...;
  // private static ... prior = ...;
  // private static ... condprob = ...;
  
  public static void main(String[] args) {
    trainMultinomialNB("training-china");
    applyMultinomialNB("test-china/5-test.txt");
  }

  /**
   * Berechnet v, prior und cond.
   * Gibt die Prior- und bedingten Wahrscheinlichkeiten für die Klassen bzw. Terme
   * auf der Konsole aus.
   * 
   * @param trainingDir Verzeichnis, in dem sich die Trainingsdokumente befinden
   */
  public static void trainMultinomialNB(String trainingDir) {

  }

  /**
   * Berechnet, ob das Dokument zur Klasse "china" gehört oder nicht.
   * Gibt die Ergebnisse auf der Konsole aus.
   * 
   * @param docPath Pfad zum Dokument, das klassifiziert werden soll
   */
  public static void applyMultinomialNB(String docPath) {

  }

}
