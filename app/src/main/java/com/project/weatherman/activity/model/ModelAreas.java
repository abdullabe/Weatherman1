package com.project.weatherman.activity.model;

public class ModelAreas {
    private String id;
    private String name;
    private String status_id;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus_id() {
        return status_id;
    }

    public void setStatus_id(String status_id) {
        this.status_id = status_id;
    }
    @Override
    public String toString() {
        return name;
    }
}
