package shared.utils;

import java.io.FileNotFoundException;
import java.io.IOException;

import shared.functional.Functor;
import shared.functional.Inserter;

public abstract class AbstractCSVReader<Container> {
	
	private Inserter<String , String[], Container> inserter;
	private GenericFileReader<String[],Container> fh;

	protected abstract void insert( String[] tuple , Container container);
	protected abstract Container instanciate();
	
	public AbstractCSVReader(String path, String delimiter) throws FileNotFoundException {
		Functor<String , String[]> mapping = x -> x.split(delimiter);
		this.inserter = new Inserter<>( this::instanciate , mapping, this::insert );
		this.fh = new GenericFileReader<>(path, inserter);
	} 
	
	public Container readFile() throws IOException{
		return this.fh.readFile();
	}

}
