package com.codehunters.healthcare.dto;

public class MedicalRecordDto {
    private String doctorName;
    private String date;
    private String diagnosis;
    private String prescription;
    private String notes;

    public MedicalRecordDto() {
    }

    public MedicalRecordDto(String doctorName, String date, String diagnosis, String prescription, String notes) {
        this.doctorName = doctorName;
        this.date = date;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.notes = notes;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
