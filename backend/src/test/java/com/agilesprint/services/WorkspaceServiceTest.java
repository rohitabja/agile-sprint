package com.agilesprint.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.agilesprint.domain.WorkspaceMember;
import com.agilesprint.domain.WorkspaceRole;
import com.agilesprint.repositories.BoardColumnRepository;
import com.agilesprint.repositories.BoardRepository;
import com.agilesprint.repositories.WorkspaceMemberRepository;
import com.agilesprint.repositories.WorkspaceRepository;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class WorkspaceServiceTest {
    @Mock
    private WorkspaceRepository workspaceRepository;
    @Mock
    private WorkspaceMemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardColumnRepository columnRepository;

    private WorkspaceService service;
    private final UserContext user = new UserContext("user-1", "alice", "alice@example.com", "Alice");

    @BeforeEach
    void setUp() {
        service = new WorkspaceService(workspaceRepository, memberRepository, boardRepository, columnRepository);
    }

    @Test
    void membershipFallsBackToUsernameWhenUserIdLookupMisses() {
        WorkspaceMember member = WorkspaceMember.builder().workspaceId("workspace-1").userId("user-1")
                .username("alice").role(WorkspaceRole.ADMIN).build();
        when(memberRepository.findByWorkspaceIdAndUserId("workspace-1", "user-1")).thenReturn(Mono.empty());
        when(memberRepository.findByWorkspaceIdAndUsername("workspace-1", "alice")).thenReturn(Mono.just(member));

        StepVerifier.create(service.requireAdmin("workspace-1", user))
                .assertNext(result -> assertEquals(WorkspaceRole.ADMIN, result.getRole()))
                .verifyComplete();
    }

    @Test
    void membershipRejectsNonMembers() {
        when(memberRepository.findByWorkspaceIdAndUserId("workspace-1", "user-1")).thenReturn(Mono.empty());
        when(memberRepository.findByWorkspaceIdAndUsername("workspace-1", "alice")).thenReturn(Mono.empty());

        StepVerifier.create(service.membership("workspace-1", user))
                .expectErrorMessage("You are not a member of this workspace")
                .verify();
    }
}
