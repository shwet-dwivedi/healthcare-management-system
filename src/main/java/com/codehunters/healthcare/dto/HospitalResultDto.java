package com.codehunters.healthcare.dto;

import java.util.List;

public class HospitalResultDto {
    private String name;
    private String address;
    private String distance;
    private List<String> availableDoctors;

    public HospitalResultDto() {
    }

    public HospitalResultDto(String name, String address, String distance, List<String> availableDoctors) {
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.availableDoctors = availableDoctors;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<String> getAvailableDoctors() {
        return availableDoctors;
    }

    public void setAvailableDoctors(List<String> availableDoctors) {
        this.availableDoctors = availableDoctors;
    }
}
