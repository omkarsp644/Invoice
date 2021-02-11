package com.svttechnology.invoice.activities.ClasseFiles;

public class InventoryData {
    String Id,Name,Cost;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCost() {
        return Cost;
    }

    public void setCost(String cost) {
        Cost = cost;
    }

    public InventoryData() {
    }

    public InventoryData(String id, String name, String cost) {
        Id = id;
        Name = name;
        Cost = cost;
    }
}

