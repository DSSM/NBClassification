package de.htwberlin.ai.daweb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author Sergej Mann <man53r@gmail.com>
 * @version 0.1
 */

public class NaiveBayesClassifier {

	private final String[] classes;
	private double[] priors;
	private NBCMatrix matrix;

	public NaiveBayesClassifier(final String[] classes) {
		if (classes == null || classes.length == 0) {
			throw new IllegalArgumentException("Parameter \"fileName\" cannot be null or empty!");
		}
		this.classes = classes;
		this.priors = new double[classes.length];
		this.matrix = new NBCMatrix(this.classes);
	}

	public void train(File trainDir) {
		if (trainDir == null) {
			throw new IllegalArgumentException("Parameter \"trainDir\" cannot be null!");
		}
		if (!trainDir.isDirectory()) {
			throw new IllegalArgumentException("Parameter \"trainDir\" isn't a directory!");
		}
		File[] children = trainDir.listFiles();
		if (children == null) {
			throw new IllegalArgumentException("Directory cannot be empty!");
		}
		for (int i = 0; i < this.priors.length; i++) {
			this.priors[i] = 0.0f;
		}
		for (File child : children) {
			if (child.isFile()) {
				DocumentReader reader = null;
				try {
					reader = new DocumentReader(child);
					String docContent = reader.getContent();
					String[] terms = docContent.split(" ");
					boolean isChinaClass = child.getName().contains("yes");
					if (isChinaClass) {
						this.priors[0] += 1.0f;
						for (String term : terms) {
							this.matrix.addTerm(term, classes[0]);
						}
					} else {
						this.priors[1] += 1.0f;
						for (String term : terms) {
							this.matrix.addTerm(term, classes[1]);
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} else if (child.isDirectory()) {
				this.train(child);
			}
		}
		for (int i = 0; i < this.priors.length; i++) {
			this.priors[i] /= children.length;
		}
		this.calculateConditionalProbabilities();
	}

	private void calculateConditionalProbabilities() {
		for (String category : classes) {
			this.matrix.calculateConditionalProbabilities(category);
		}
	}

	public String recognizeDocumentclass(File document) throws FileNotFoundException, IOException {
		if (document == null) {
			throw new IllegalArgumentException("Parameter \"document\" cannot be null!");
		}
		if (!document.isFile()) {
			throw new IllegalArgumentException("Parameter \"document\" isn't a file!");
		}
		DocumentReader reader = null;
		reader = new DocumentReader(document);
		String docContent;
		docContent = reader.getContent();
		String[] terms = docContent.split(" ");
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		double[] docPrior = new double[classes.length];
		for (int i = 0; i < docPrior.length; i++) {
			docPrior[i] = 1.0f;
		}
		int biggestPlace = -1;
		double biggest = -1.0f;
		double tmpPrio;
		for (int i = 0; i < classes.length; i++) {
			tmpPrio = 1.0f;
			for (String term : terms) {
				tmpPrio *= this.matrix.getCondprob(classes[i], term);
			}
			docPrior[i] = priors[i] * tmpPrio;
			NumberFormat formatter = new DecimalFormat("###.##########");
			String f = formatter.format(docPrior[i]);
			System.out.printf("%24s <-> " + f + "\n", "P(" + classes[i] + "|Dx)");
			// System.out.printf("%24s <-> " + docPrior[i] + "\n", "P(" +
			// classes[i] + "|Dx)");
			if (docPrior[i] > biggest) {
				biggest = docPrior[i];
				biggestPlace = i;
			}
		}
		if (0 <= biggestPlace && biggestPlace < classes.length) {
			return classes[biggestPlace];
		}
		return null;
	}

	public void printMatrix() {
		this.matrix.printMatrix();
	}

	public void printCondprob() {
		this.matrix.printCondprob();
	}

	public void printPrios() {
		for (int i = 0; i < classes.length; i++) {
			System.out.printf("%25s = " + this.priors[i] + "\n", "P(" + classes[i] + ")");
		}
	}

}
