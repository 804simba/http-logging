package com.simba.quartz.aspect;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/simulation")
@RestController
public class SimulatorController {

//    @Traceable("com.simba.quartz.aspect.SimulatorController.start")
    @GetMapping("/start")
    public String start() {
        throw new RuntimeException("Simulation started");
    }
}
