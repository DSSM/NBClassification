package de.htwberlin.ai.daweb;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Sergej Mann <man53r@gmail.com>
 * @version 0.1
 */

public class DocumentReader {

	private String content;
	private byte[] bytes;
	// private File file;
	// private FileInputStream fileInputStream;
	private DataInputStream dataInputStream;

	public DocumentReader(String fileName) throws FileNotFoundException {
		if (fileName == null || fileName.isEmpty()) {
			throw new IllegalArgumentException("Parameter \"fileName\" cannot be null or empty!");
		}
		File file = new File(fileName);
		if (!file.isFile()) {
			throw new IllegalArgumentException("Parameter \"fileName\" isn't a file!");
		}
		FileInputStream fileInputStream = new FileInputStream(file);
		this.dataInputStream = new DataInputStream(fileInputStream);
		this.content = null;
		this.bytes = null;
	}

	public DocumentReader(File file) throws FileNotFoundException {
		if (file == null) {
			throw new IllegalArgumentException("Parameter \"file\" cannot be null!");
		}
		if (!file.isFile()) {
			throw new IllegalArgumentException("Parameter \"file\" isn't a file!");
		}
		FileInputStream fileInputStream = new FileInputStream(file);
		this.dataInputStream = new DataInputStream(fileInputStream);
		this.content = null;
		this.bytes = null;
	}

	public int available() throws IOException {
		return this.dataInputStream.available();
	}

	public byte[] readFully() throws IOException {
		if (this.bytes == null) {
			this.bytes = new byte[this.available()];
			this.dataInputStream.readFully(this.bytes);
			if (this.content == null) {
				this.content = new String(bytes, 0, bytes.length);
			}
		}
		return bytes;
	}

	public String getContent() throws IOException {
		if (this.content == null) {
			this.readFully();
		}
		return this.content;
	}

	public void close() throws IOException {
		this.dataInputStream.close();
	}

}
