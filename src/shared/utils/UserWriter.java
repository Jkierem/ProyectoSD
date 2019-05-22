package shared.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserWriter {
    private GenericFileWriter<List<String> , List<List<String>>> writer;

    public UserWriter(String path) throws IOException {
        this.writer = new GenericFileWriter<>( path , strs -> String.join(",", strs));
    }

    public void writeFile(HashMap<String,String> data)throws IOException{
        List<List<String>> stringData = new ArrayList<>();
        for( String key : data.keySet() ){
            List<String> tuple = new ArrayList<>();
            tuple.add( key );
            tuple.add( data.get(key) );
            stringData.add(tuple);
        }
        this.writer.writeFile(stringData,"\n");
    }
}
