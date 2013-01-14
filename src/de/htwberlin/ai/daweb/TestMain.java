package de.htwberlin.ai.daweb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Sergej Mann <man53r@gmail.com>
 * @version 0.1
 */

public class TestMain {
	private final static String[] classes = new String[] { "china", "not-china" };

	public static void main(String[] args) throws FileNotFoundException, IOException {
		NaiveBayesClassifier classifier = new NaiveBayesClassifier(classes);
		File trainDir = new File("training-china");
		classifier.train(trainDir);
		System.out.println("-----------------------------------------------");
		classifier.printPrios();
		System.out.println("-----------------------------------------------");
		classifier.printCondprob();
		System.out.println("-----------------------------------------------");
		// classifier.printMatrix();
		File document = new File("test-china/5-test.txt");
		String category = classifier.recognizeDocumentclass(document);
		System.out.println("-----------------------------------------------");
		System.out.println("[" + document.getName() + "] document class most likely \"" + category
				+ "\"");
		System.out.println("================================================");

		document = new File("training-china/1-yes.txt");
		category = classifier.recognizeDocumentclass(document);
		System.out.println("[" + document.getName() + "] document class most likely \"" + category
				+ "\"");
		System.out.println("================================================");

		document = new File("training-china/2-yes.txt");
		category = classifier.recognizeDocumentclass(document);
		System.out.println("[" + document.getName() + "] document class most likely \"" + category
				+ "\"");
		System.out.println("================================================");

		document = new File("training-china/3-yes.txt");
		category = classifier.recognizeDocumentclass(document);
		System.out.println("[" + document.getName() + "] document class most likely \"" + category
				+ "\"");
		System.out.println("================================================");

		document = new File("training-china/4-no.txt");
		category = classifier.recognizeDocumentclass(document);
		System.out.println("[" + document.getName() + "] document class most likely \"" + category
				+ "\"");
	}
}
