package it.uniba.di.ivu.sms16.gruppo2.dibapp.utils;

import com.google.android.gms.maps.model.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;


public class MyHashMap extends HashMap<Marker, StudySession> {

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof StudySession) {
            Collection<StudySession> sessioni = values();
            for (StudySession s : sessioni) {
                if (s.id.equals(((StudySession) value).id)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Recupero il marker della sessione
    public Marker getMarker(StudySession value) {
        Set<Marker> markers = keySet();
        for (Marker m : markers) {
            if (get(m).id.equals(value.id)) {
                return m;
            }
        }
        return null;
    }

}
