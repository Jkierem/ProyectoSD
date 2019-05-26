package shared.utils;

import shared.functional.Functor;

import java.util.List;

public class OptionalList<T> {
    List<T> data;

    public OptionalList(List<T> data) {
        this.data = data;
    }

    public T get( int index ){
        return this.data.get(index);
    }

    public T getOrElse( int index , T orElse){
        if( this.data.size() > index ){
            return this.data.get(index);
        }else{
            return orElse;
        }
    }

    public <B> B getOrElseMap(int index , B orElse, Functor<T,B> mapping ){
        if( this.data.size() > index ){
            return mapping.apply(this.data.get(index));
        }else{
            return orElse;
        }
    }
}
