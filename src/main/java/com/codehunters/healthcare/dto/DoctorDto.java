package com.codehunters.healthcare.dto;

import java.math.BigDecimal;

public class DoctorDto {
    private Integer doctorId;
    private String name;
    private String specialization;
    private BigDecimal fee;

    public DoctorDto() {
    }

    public DoctorDto(Integer doctorId, String name, String specialization, BigDecimal fee) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.fee = fee;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}
