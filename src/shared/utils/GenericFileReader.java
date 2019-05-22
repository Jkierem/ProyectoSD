package shared.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import shared.functional.Inserter;

public class GenericFileReader<T,Container> {
	
	private FileReader reader;
	private BufferedReader buffer;
	private Inserter<String,T,Container> inserter;
	
	public GenericFileReader(String path, Inserter<String,T,Container> inserter) throws FileNotFoundException {
		this.reader = new FileReader(new File(path));
		this.buffer = new BufferedReader(this.reader);
		this.inserter = inserter;
	}
	
	public Container readFile() throws IOException {
		String line = null;
		while( (line = buffer.readLine()) != null ) {
			this.inserter.insert(line);
		}
		this.close();
		return this.inserter.getData();
	}
	
	public void close() throws IOException {
		this.buffer.close();
		this.reader.close();
	}
}
