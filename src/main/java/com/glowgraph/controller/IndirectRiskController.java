package com.glowgraph.controller;

import com.glowgraph.dto.IndirectRiskInfo;
import com.glowgraph.service.RoutineService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risks")
@CrossOrigin(origins = "*")
public class IndirectRiskController {

    private final RoutineService routineService;

    public IndirectRiskController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/indirect")
    public List<IndirectRiskInfo> getIndirectRisks() {
        return routineService.findIndirectRisks();
    }
}