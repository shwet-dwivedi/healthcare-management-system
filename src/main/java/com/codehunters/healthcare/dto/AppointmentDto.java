package com.codehunters.healthcare.dto;

import com.codehunters.healthcare.model.Appointment.AppointmentStatus;
import java.time.LocalDate;
import java.time.LocalTime;

public class AppointmentDto {
    private Integer appointmentId;
    private String patientName;
    private String doctorName;
    private String specialization;
    private LocalDate date;
    private LocalTime time;
    private String reason;
    private AppointmentStatus status;

    public AppointmentDto() {
    }

    public AppointmentDto(Integer appointmentId, String patientName, String doctorName, 
                         String specialization, LocalDate date, LocalTime time, String reason, 
                         AppointmentStatus status) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.specialization = specialization;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
