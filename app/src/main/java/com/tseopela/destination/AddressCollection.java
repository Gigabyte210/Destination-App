package com.tseopela.destination;

public class AddressCollection {
    private String location; //attribute

    //default constructor
    public AddressCollection()
    {
    }

    //constructor with attributes
    public AddressCollection(String loc)
    {
        this.location = loc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Favourite Locations" + "\n" +
                "================" + '\n' +
                "Location: " + location;
    }
}
