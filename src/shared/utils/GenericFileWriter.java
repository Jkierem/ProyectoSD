package shared.utils;

import shared.functional.Functor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GenericFileWriter< T , Container extends Iterable<T> > {

    private BufferedWriter buffer;
    private FileWriter writer;
    private String path;
    private Functor< T , String > mapping;

    public GenericFileWriter(String path, Functor<T, String> mapping) throws IOException {
        this.mapping = mapping;
        this.path = path;
    }

    private void close() throws IOException {
        this.buffer.close();
        this.writer.close();
    }

    public void writeFile(Container data, String delimiter) throws IOException{
        this.writer = new FileWriter(new File(this.path));
        this.buffer = new BufferedWriter(writer);
        for( T element: data){
            this.buffer.write(this.mapping.apply(element) + delimiter);
        }
        this.close();
    }
}
