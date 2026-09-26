package com.agilesprint.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import com.agilesprint.controllers.ApiDtos;
import com.agilesprint.domain.Board;
import com.agilesprint.domain.BoardColumn;
import com.agilesprint.domain.Workspace;
import com.agilesprint.domain.WorkspaceMember;
import com.agilesprint.domain.WorkspaceRole;
import com.agilesprint.mappers.ApiMapper;
import com.agilesprint.repositories.BoardColumnRepository;
import com.agilesprint.repositories.BoardRepository;
import com.agilesprint.repositories.WorkspaceMemberRepository;
import com.agilesprint.repositories.WorkspaceRepository;
import com.agilesprint.services.ServiceExceptions.Forbidden;
import com.agilesprint.services.ServiceExceptions.NotFound;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class WorkspaceService {
    private static final List<String[]> DEFAULT_COLUMNS = List.of(
            new String[] { "To Do", "todo" }, new String[] { "In Progress", "in-progress" },
            new String[] { "Testing", "testing" }, new String[] { "Done", "done" });

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardColumnRepository columnRepository;

    public WorkspaceService(WorkspaceRepository workspaceRepository, WorkspaceMemberRepository memberRepository,
            BoardRepository boardRepository, BoardColumnRepository columnRepository) {
        this.workspaceRepository = workspaceRepository;
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.columnRepository = columnRepository;
    }

    public Flux<ApiDtos.WorkspaceResponse> list(UserContext user) {
        return memberRepository.findByUserId(user.id())
                .switchIfEmpty(memberRepository.findByUsername(user.username()))
                .flatMap(memberRepositoryMember -> workspaceRepository.findById(memberRepositoryMember.getWorkspaceId()))
                .flatMap(workspace -> memberRepository.findByWorkspaceId(workspace.getId()).count()
                        .map(count -> ApiMapper.workspace(workspace, count)));
    }

    public Mono<ApiDtos.WorkspaceResponse> create(ApiDtos.WorkspaceRequest request, UserContext user) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            return Mono.error(new IllegalArgumentException("Workspace name is required"));
        }
        Instant now = Instant.now();
        Workspace workspace = Workspace.builder().name(request.name().trim())
                .description(request.description() == null ? "" : request.description().trim())
                .ownerId(user.id()).ownerUsername(user.username()).createdAt(now).updatedAt(now).build();
        return workspaceRepository.save(workspace)
                .flatMap(saved -> memberRepository.save(WorkspaceMember.builder().workspaceId(saved.getId())
                        .userId(user.id()).username(user.username()).email(user.email()).role(WorkspaceRole.ADMIN)
                        .joinedAt(now).build()).thenReturn(saved))
                .flatMap(saved -> {
                    Board board = Board.builder().workspaceId(saved.getId()).name("Main board").createdAt(now).build();
                    return boardRepository.save(board).flatMap(savedBoard -> columnRepository.saveAll(
                            Flux.range(0, DEFAULT_COLUMNS.size()).map(index -> BoardColumn.builder()
                                    .boardId(savedBoard.getId()).name(DEFAULT_COLUMNS.get(index)[0])
                                    .key(DEFAULT_COLUMNS.get(index)[1]).position(index).build()))
                            .then(Mono.just(saved)));
                })
                .map(saved -> ApiMapper.workspace(saved, 1));
    }

    public Mono<Workspace> requireWorkspace(String workspaceId, UserContext user) {
        return membership(workspaceId, user).then(workspaceRepository.findById(workspaceId)
                .switchIfEmpty(Mono.error(new NotFound("Workspace not found"))));
    }

    public Mono<WorkspaceMember> membership(String workspaceId, UserContext user) {
        return memberRepository.findByWorkspaceIdAndUserId(workspaceId, user.id())
                .switchIfEmpty(memberRepository.findByWorkspaceIdAndUsername(workspaceId, user.username()))
                .switchIfEmpty(Mono.error(new Forbidden("You are not a member of this workspace")));
    }

    public Mono<WorkspaceMember> requireAdmin(String workspaceId, UserContext user) {
        return membership(workspaceId, user).flatMap(member -> member.getRole() == WorkspaceRole.ADMIN
                ? Mono.just(member) : Mono.error(new Forbidden("Workspace admin permission required")));
    }

    public Flux<ApiDtos.MemberResponse> members(String workspaceId, UserContext user) {
        return requireWorkspace(workspaceId, user).thenMany(memberRepository.findByWorkspaceId(workspaceId)
                .map(ApiMapper::member));
    }

    public Mono<ApiDtos.MemberResponse> invite(String workspaceId, ApiDtos.InviteRequest request, UserContext user) {
        if (request == null || request.username() == null || request.username().isBlank()) {
            return Mono.error(new IllegalArgumentException("A Keycloak username is required"));
        }
        return requireAdmin(workspaceId, user)
                .then(memberRepository.findByWorkspaceIdAndUsername(workspaceId, request.username().trim()))
                .flatMap(existing -> Mono.<WorkspaceMember>error(
                        new IllegalArgumentException("That username is already a member")))
                .switchIfEmpty(memberRepository.save(WorkspaceMember.builder().workspaceId(workspaceId)
                        .userId(request.username().trim()).username(request.username().trim())
                        .email(request.email() == null ? "" : request.email().trim()).role(WorkspaceRole.MEMBER)
                        .joinedAt(Instant.now()).build()))
                .map(ApiMapper::member);
    }
}
