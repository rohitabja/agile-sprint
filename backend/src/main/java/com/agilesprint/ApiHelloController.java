package com.agilesprint;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiHelloController {
    @GetMapping("/api/hello")
    public Map<String, String> hello() {
        return Map.of("message", "This protected response came from AgileSprint.");
    }
}
