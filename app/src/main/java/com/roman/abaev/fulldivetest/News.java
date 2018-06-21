package com.roman.abaev.fulldivetest;


import java.util.UUID;

public class News implements Comparable<News>{
    private UUID id;
    private String name;
    private String links;
    private String date;

    public News(String name, String links, String date) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.links = links;
        this.date = date;
    }

    public UUID getUuid() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLinks() {
        return links;
    }

    public String getDate() {
        return date;
    }


    @Override
    public int compareTo(News n) {
        if (getDate() == null || n.getDate() == null )
            return 0;
        return getDate().compareTo(n.getDate());
    }
}
