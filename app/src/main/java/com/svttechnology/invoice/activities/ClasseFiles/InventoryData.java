package com.svttechnology.invoice.activities.ClasseFiles;

public class InventoryData {
    String Id,Name,Cost;
    int Count;

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

    public int getCount() {
        return Count;
    }

    public void setCount(int count) {
        Count = count;
    }

    public InventoryData() {
    }

    public InventoryData(String id, String name, String cost, int count) {
        Id = id;
        Name = name;
        Cost = cost;
        Count = count;
    }
}

