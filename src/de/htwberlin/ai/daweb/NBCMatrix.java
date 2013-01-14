package de.htwberlin.ai.daweb;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sergej Mann <man53r@gmail.com>
 * @version 0.1
 */

public class NBCMatrix {

	private static final int SMOOTHING = 1;
	private List<String> categories;
	private List<String> terms;
	private CalculationData[][] calcData;

	public NBCMatrix(String[] categories) {
		this.categories = new ArrayList<String>(Arrays.asList(categories));
		Collections.sort(this.categories);
		this.terms = new LinkedList<String>();
		this.calcData = new CalculationData[categories.length][];
	}

	private void matrixExpansion(int categoryPlace) {
		int length = this.calcData.length;
		int newTermSize = this.terms.size();
		int newTermIndex = newTermSize - 1;
		for (int i = 0; i < length; i++) {
			long termCount = (i == categoryPlace) ? 1l : 0l;
			if (this.calcData[i] == null) {
				this.calcData[i] = new CalculationData[newTermSize];
				this.calcData[i][newTermIndex] = new CalculationData(termCount);
			} else {
				CalculationData[] cd = new CalculationData[newTermSize];
				for (int j = 0; j < this.calcData[i].length; j++) {
					cd[j] = this.calcData[i][j];
				}
				cd[newTermIndex] = new CalculationData(termCount);
				this.calcData[i] = cd;
			}
		}
	}

	public void addTerm(final String term, final String category) {
		int categoryPlace = this.categories.indexOf(category);
		if (categoryPlace >= 0) {
			int termPlace = this.terms.indexOf(term);
			if (termPlace == -1) {
				this.terms.add(term);
				this.matrixExpansion(categoryPlace);
			} else {
				if (this.calcData[categoryPlace][termPlace] == null) {
					this.calcData[categoryPlace][termPlace] = new CalculationData(1l);
				} else {
					this.calcData[categoryPlace][termPlace].count++;
				}
			}
		}
	}

	public void printMatrix() {
		System.out.println("============================================");
		NumberFormat formatter = new DecimalFormat("###.##########");
		System.out.print('\t');
		for (String category : this.categories) {
			System.out.print("\t" + category + "\t");
		}
		int termSize = this.terms.size();
		for (int i = 0; i < termSize; i++) {
			System.out.println();
			System.out.printf("%10s", this.terms.get(i));
			for (int j = 0; j < this.calcData.length; j++) {
				String f = formatter.format(this.calcData[j][i].condprob);
				System.out.print("\t" + f);
				// System.out.print("\t" + this.calcData[j][i].condprob);
			}
		}
		System.out.println();
		System.out.println("============================================");
	}

	public void printCondprob() {
		for (int i = 0; i < this.calcData.length; i++) {
			for (int j = 0; j < this.calcData[i].length; j++) {
				System.out.printf("%25s = " + this.calcData[i][j].condprob + "\n", "P("
						+ this.terms.get(j) + "|" + this.categories.get(i) + ")");
			}
		}
	}

	private long sumWords(final String category) {
		int index = this.categories.indexOf(category);
		long relust = 0;
		for (int i = 0; i < this.calcData[index].length; i++) {
			if (this.calcData[index][i] != null) {
				relust += this.calcData[index][i].count;
			}
		}
		return relust;
	}

	public void calculateConditionalProbabilities(final String category) {
		if (this.calcData != null) {
			long sumWords = this.sumWords(category);
			int sumVocabulary = this.terms.size();
			int index = this.categories.indexOf(category);
			for (int i = 0; i < this.calcData[index].length; i++) {
				if (this.calcData[index][i] == null) {
					this.calcData[index][i] = new CalculationData(0l);
				}
				this.calcData[index][i].condprob += (this.calcData[index][i].count + SMOOTHING);
				this.calcData[index][i].condprob /= (sumWords + sumVocabulary);
				// System.out.printf("%25s = " +
				// this.calcData[index][i].condprob + "\n", "P("
				// + this.terms.get(i) + "|" + category + ")");
				// System.out.println("P(" + this.terms.get(i) + "|" + category
				// + ") = "+ this.calcData[index][i].condprob);
			}
		}
	}

	public double getCondprob(final String category, final String term) {
		if (category == null || category.isEmpty()) {
			throw new IllegalArgumentException("Parameter \"category\" cannot be null or empty!");
		}
		int categoryPlace = this.categories.indexOf(category);
		if (categoryPlace == -1) {
			throw new IllegalArgumentException("Documentclass \"" + category
					+ "\" Documentclass isn't defined!");
		}
		if (term == null || term.isEmpty()) {
			throw new IllegalArgumentException("Parameter \"term\" cannot be null or empty!");
		}
		int termPlace = this.terms.indexOf(term);
		if (termPlace == -1) {
			return 0.0f;
		}
		return this.calcData[categoryPlace][termPlace].condprob;
	}

}
