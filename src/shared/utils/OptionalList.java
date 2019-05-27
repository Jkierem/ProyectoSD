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
        if( this.data.size() > index && index >= 0){
            return this.data.get(index);
        }else{
            return orElse;
        }
    }

    public int findNextIndexOrMinusOne( T value ){
        return this.data.contains(value) ? this.data.indexOf(value) + 1 : -1 ;
    }

    public boolean contains( T value ){
        return this.data.contains(value);
    }

    public T findNextOrElse( T value , T orElse ){
        return this.getOrElse( this.findNextIndexOrMinusOne(value) , orElse);
    }

    public boolean containsAny( List<T> values ) {
        boolean contains = false;
        for( T v : values ){
            contains = contains || this.data.contains(v);
        }
        return contains;
    }

    public <B> B getOrElseMap(int index , B orElse, Functor<T,B> mapping ){
        if( this.data.size() > index ){
            return mapping.apply(this.data.get(index));
        }else{
            return orElse;
        }
    }
}
