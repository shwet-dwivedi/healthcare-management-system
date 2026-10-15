package com.codehunters.healthcare.controller;

import com.codehunters.healthcare.dto.HospitalResultDto;
import com.codehunters.healthcare.service.EmergencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/emergency")
public class EmergencyController {

    @Autowired
    private EmergencyService emergencyService;

    @GetMapping("")
    public String emergency(Model model) {
        List<String> areas = emergencyService.getAreas();
        model.addAttribute("areas", areas);
        return "emergency";
    }

    @GetMapping("/results")
    public String results(@RequestParam(required = false) String area, Model model) {
        if (area != null && !area.isEmpty()) {
            List<HospitalResultDto> hospitals = emergencyService.findNearbyHospitals(area);
            model.addAttribute("hospitals", hospitals);
            model.addAttribute("selectedArea", area);
        }
        return "emergency-results";
    }
}
