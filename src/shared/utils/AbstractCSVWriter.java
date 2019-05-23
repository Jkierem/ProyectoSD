package shared.utils;

import java.io.IOException;

public abstract class AbstractCSVWriter<Container> {
    private GenericFileWriter<String[] , Iterable<String[]>> writer;
    private String delimiter;

    protected abstract Iterable<String[]> convertData( Container c );

    public AbstractCSVWriter(String path, String delimiter)throws IOException {
        this.writer = new GenericFileWriter<>(path, this::mapping);
        this.delimiter = delimiter;
    }

    private String mapping( String[] tuple ){
        return String.join(this.delimiter,tuple);
    }

    public void writeFile( Container data )throws IOException {
        this.writer.writeFile( this.convertData(data)  , "\n");
    }
}
