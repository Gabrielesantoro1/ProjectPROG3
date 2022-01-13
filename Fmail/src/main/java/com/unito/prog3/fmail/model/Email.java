package com.unito.prog3.fmail.model;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Date;

public class Email implements Serializable {
    private SimpleIntegerProperty id;
    private SimpleStringProperty from;
    private SimpleStringProperty to;
    private SimpleStringProperty object;
    private SimpleStringProperty text;
    private Date date;

    public Email(){}

    /**
     *   This constructor is used by Client because it doesn't know the current id counter.
     */
    public Email(SimpleStringProperty from, SimpleStringProperty to, SimpleStringProperty object, SimpleStringProperty text) {
        this.from = from;
        this.to = to;
        this.object = object;
        this.text = text;
        this.date = new Date();
    }

    /**
     *  This constructor is used by Server because it knows the current id counter.
     */
    public Email(SimpleIntegerProperty id, SimpleStringProperty from, SimpleStringProperty to, SimpleStringProperty object, SimpleStringProperty text, Date date) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.object = object;
        this.text = text;
        this.date = date;
    }

    public int getId() {
        return id.get();
    }

    public String getFrom() {
        return from.get();
    }

    public String getTo() {
        return to.get();
    }

    public String getObject() {
        return object.get();
    }

    public String getText() {
        return text.get();
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "\n              Email{" +
                "id=" + id.get() +
                ", from='" + from.get() + '\'' +
                ", to='" + to.get() + '\'' +
                ", object='" + object.get() + '\'' +
                ", text='" + text.get() + '\'' +
                ", date=" + date +
                '}';
    }
}
