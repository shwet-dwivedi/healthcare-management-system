package com.codehunters.healthcare.service;

import com.codehunters.healthcare.dto.HospitalResultDto;
import com.codehunters.healthcare.model.Doctor;
import com.codehunters.healthcare.model.Hospital;
import com.codehunters.healthcare.repository.DoctorAvailabilityRepository;
import com.codehunters.healthcare.repository.DoctorRepository;
import com.codehunters.healthcare.repository.HospitalRepository;
import com.codehunters.healthcare.util.LocationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmergencyService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorAvailabilityRepository doctorAvailabilityRepository;

    private static final Map<String, double[]> AREA_COORDINATES = new HashMap<>();

    static {
        AREA_COORDINATES.put("Malviya Nagar", new double[]{26.8523, 75.8011});
        AREA_COORDINATES.put("C-Scheme", new double[]{26.9124, 75.8069});
        AREA_COORDINATES.put("Vaishali Nagar", new double[]{26.9045, 75.7406});
        AREA_COORDINATES.put("Mansarovar", new double[]{26.8471, 75.7637});
        AREA_COORDINATES.put("Jagatpura", new double[]{26.7990, 75.8395});
        AREA_COORDINATES.put("Pratap Nagar", new double[]{26.8515, 75.8092});
        AREA_COORDINATES.put("Vidhyadhar Nagar", new double[]{26.9465, 75.7645});
        AREA_COORDINATES.put("Tonk Road", new double[]{26.8206, 75.8086});
        AREA_COORDINATES.put("JLN Marg", new double[]{26.9093, 75.8187});
        AREA_COORDINATES.put("Civil Lines", new double[]{26.9282, 75.8155});
    }

    public List<String> getAreas() {
        return new ArrayList<>(AREA_COORDINATES.keySet());
    }

    public double[] getAreaCoordinates(String area) {
        if (AREA_COORDINATES.containsKey(area)) {
            return AREA_COORDINATES.get(area);
        }
        return new double[]{26.9124, 75.7873};
    }

    public List<HospitalResultDto> findNearbyHospitals(String area) {
        List<Hospital> emergencyHospitals = hospitalRepository.findByEmergencyAvailableTrue();
        double[] areaCoords = getAreaCoordinates(area);
        double areaLat = areaCoords[0];
        double areaLon = areaCoords[1];

        List<HospitalResultDto> results = emergencyHospitals.stream()
            .map(hospital -> {
                double hospitalLat = hospital.getLatitude().doubleValue();
                double hospitalLon = hospital.getLongitude().doubleValue();
                double distance = LocationUtil.haversineDistance(areaLat, areaLon, hospitalLat, hospitalLon);

                List<String> availableDoctors = getAvailableDoctorsAtHospital(hospital.getHospitalId());

                return new HospitalResultDto(
                    hospital.getName(),
                    hospital.getAddress(),
                    String.format("%.2f km", distance),
                    availableDoctors
                );
            })
            .sorted((h1, h2) -> {
                double dist1 = Double.parseDouble(h1.getDistance().split(" ")[0]);
                double dist2 = Double.parseDouble(h2.getDistance().split(" ")[0]);
                return Double.compare(dist1, dist2);
            })
            .collect(Collectors.toList());

        return results;
    }

    private List<String> getAvailableDoctorsAtHospital(Integer hospitalId) {
        List<Doctor> doctors = doctorRepository.findByHospital_HospitalId(hospitalId);
        LocalDate today = LocalDate.now();

        return doctors.stream()
            .filter(doctor -> {
                long availableSlotCount = doctorAvailabilityRepository
                    .findByDoctor_DoctorIdAndSlotDateAndIsBookedFalse(doctor.getDoctorId(), today)
                    .size();
                return availableSlotCount > 0;
            })
            .map(doctor -> doctor.getUser().getFullName())
            .collect(Collectors.toList());
    }
}
