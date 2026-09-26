package com.agilesprint.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.agilesprint.services.UserContext;
import com.agilesprint.services.WorkspaceService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {
    private final WorkspaceService service;

    public WorkspaceController(WorkspaceService service) {
        this.service = service;
    }

    @GetMapping
    public Flux<ApiDtos.WorkspaceResponse> list(Authentication authentication) {
        return service.list(UserContext.from(authentication));
    }

    @PostMapping
    public Mono<ApiDtos.WorkspaceResponse> create(@RequestBody ApiDtos.WorkspaceRequest request,
            Authentication authentication) {
        return service.create(request, UserContext.from(authentication));
    }

    @GetMapping("/{workspaceId}/members")
    public Flux<ApiDtos.MemberResponse> members(@PathVariable("workspaceId") String workspaceId,
            Authentication authentication) {
        return service.members(workspaceId, UserContext.from(authentication));
    }

    @PostMapping("/{workspaceId}/invites")
    public Mono<ApiDtos.MemberResponse> invite(@PathVariable("workspaceId") String workspaceId,
            @RequestBody ApiDtos.InviteRequest request, Authentication authentication) {
        return service.invite(workspaceId, request, UserContext.from(authentication));
    }
}
