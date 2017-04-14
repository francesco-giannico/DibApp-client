package it.uniba.di.ivu.sms16.gruppo2.dibapp.models;

import java.io.Serializable;

/**
 * Created by antoniolategano on 24/06/16.
 */
public class Note implements Serializable {

    public String title;
    public float avgRating;
    public String description;
    public String studySession;
    public int downloads;
    public String owner;
    public String format;
    public String degreeCourse;
    public String course;
    public String hourUp;
    public String dateUp;
    public int views;
    public boolean notesBoard;
    public int numRating;
    public String refStorage;

    public String id;


    public Note() {
    }

    public Note(String refStorage, String dateUp, String hourUp, String course, String degreeCourse, String format, String owner, String description, String title, boolean notesBoard, String studySession) {
        this.refStorage = refStorage;
        this.dateUp = dateUp;
        this.hourUp = hourUp;
        this.course = course;
        this.degreeCourse = degreeCourse;
        this.format = format;
        this.owner = owner;
        this.description = description;
        this.title = title;
        this.notesBoard = notesBoard;
        this.studySession = studySession;
    }

    public Note(String title, float avgRating, String description, int downloads, String owner, String format, String degreeCourse, String course, String hourUp, String dateUp, int views, boolean notesBoard, int numRating, String refStorage) {
        this.title = title;
        this.avgRating = avgRating;
        this.description = description;
        this.downloads = downloads;
        this.owner = owner;
        this.format = format;
        this.degreeCourse = degreeCourse;
        this.course = course;
        this.hourUp = hourUp;
        this.dateUp = dateUp;
        this.views = views;
        this.notesBoard = notesBoard;
        this.numRating = numRating;
        this.refStorage = refStorage;
    }
}
