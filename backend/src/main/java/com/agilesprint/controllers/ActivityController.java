package com.agilesprint.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agilesprint.services.ActivityService;
import com.agilesprint.services.UserContext;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/activity")
public class ActivityController {
    private final ActivityService service;

    public ActivityController(ActivityService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<ApiDtos.ActivityResponse> list(@PathVariable("workspaceId") String workspaceId,
            Authentication authentication) {
        return service.list(workspaceId, UserContext.from(authentication));
    }
}
