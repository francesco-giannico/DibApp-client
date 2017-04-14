package it.uniba.di.ivu.sms16.gruppo2.dibapp.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by antoniolategano on 12/07/16.
 */
public class ComplexObject<T> implements Serializable {

    private List<T> list;

    public ComplexObject(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }

}
