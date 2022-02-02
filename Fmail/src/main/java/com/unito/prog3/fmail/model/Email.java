package com.unito.prog3.fmail.model;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Email implements Serializable {
    private int id;
    private String from;
    private String to;
    private String object;
    private String text;
    private Date date;

    public Email(){}

    /**
     *   This constructor is used by Client because it doesn't know the current id counter.
     */
    public Email(String from, String to, String object, String text) {
        this.from = from;
        this.to = to;
        this.object = object;
        this.text = text;
        this.date = new Date();
    }

    /**
     *  This constructor is used by Server because it knows the current id counter.
     */
    public Email(int id, String from, String to, String object, String text, Date date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.object = object;
        this.text = text;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {this.id = id;}

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getObject() {
        return object;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "\n              Email{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", object='" + object + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
