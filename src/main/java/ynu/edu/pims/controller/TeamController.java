package ynu.edu.pims.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import zxylearn.bcnlserver.common.UserContext;
import zxylearn.bcnlserver.pojo.DTO.TeamCreateRequestDTO;
import zxylearn.bcnlserver.pojo.DTO.TeamMemberVO;
import zxylearn.bcnlserver.pojo.DTO.TeamUpdateRequestDTO;
import zxylearn.bcnlserver.pojo.entity.Team;
import zxylearn.bcnlserver.pojo.entity.TeamJoinApply;
import zxylearn.bcnlserver.pojo.entity.TeamMember;
import zxylearn.bcnlserver.service.TeamJoinApplyService;
import zxylearn.bcnlserver.service.TeamMemberService;
import zxylearn.bcnlserver.service.TeamService;
import zxylearn.bcnlserver.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;

@Tag(name = "团队模块")
@RestController
@RequestMapping("/team")
@Transactional
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamMemberService teamMemberService;

    @Autowired
    private TeamJoinApplyService teamJoinApplyService;


    @Operation(summary = "申请创建团队")
    @PostMapping("/apply-create")
    public ResponseEntity<?> applyCreateTeam(@RequestBody TeamCreateRequestDTO teamCreateRequestDTO) {

        Long userId = Long.parseLong(UserContext.getUserId());

        Team team = Team.builder()
                .name(teamCreateRequestDTO.getName())
                .banner(teamCreateRequestDTO.getBanner())
                .logo(teamCreateRequestDTO.getLogo())
                .description(teamCreateRequestDTO.getDescription())
                .ownerId(userId)
                .status(0) // 0 待审核
                .build();

        if (!teamService.save(team)) {
            return ResponseEntity.status(500).body(Map.of("error", "创建团队失败"));
        }

        return ResponseEntity.ok(Map.of("team", team));
    }

    @Operation(summary = "同意创建团队")
    @PostMapping("/approve-create")
    public ResponseEntity<?> approveCreateTeam(@RequestParam Long teamId) {

        String role = UserContext.getUserRole();
        if (!role.equals(JwtUtil.ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "无权限操作"));
        }

        Team team = teamService.getById(teamId);
        if (team == null) {
            return ResponseEntity.status(404).body(Map.of("error", "团队不存在"));
        }

        // 将创建者加入团队
        TeamMember teamMember = TeamMember.builder()
                .memberId(team.getOwnerId())
                .teamId(team.getId())
                .position("团队创建者")
                .joinTime(LocalDateTime.now())
                .build();

        if (!teamMemberService.save(teamMember)) {
            return ResponseEntity.status(500).body(Map.of("error", "添加团队创建者为成员失败"));
        }

        team.setStatus(1); // 1 已通过
        if (!teamService.updateById(team)) {
            return ResponseEntity.status(500).body(Map.of("error", "同意创建团队失败"));
        }

        return ResponseEntity.ok(Map.of("team", team));
    }

    @Operation(summary = "拒绝创建团队")
    @PostMapping("/reject-create")
    public ResponseEntity<?> rejectCreateTeam(@RequestParam Long teamId) {

        String role = UserContext.getUserRole();
        if (!role.equals(JwtUtil.ADMIN)) {
            return ResponseEntity.status(403).body(Map.of("error", "无权限操作"));
        }

        Team team = teamService.getById(teamId);
        if (team == null) {
            return ResponseEntity.status(404).body(Map.of("error", "团队不存在"));
        }

        team.setStatus(2); // 2 已拒绝
        if (!teamService.updateById(team)) {
            return ResponseEntity.status(500).body(Map.of("error", "拒绝创建团队失败"));
        }

        return ResponseEntity.ok(Map.of("team", team));
    }

    @Operation(summary = "删除团队")
    @PostMapping("/delete")
    public ResponseEntity<?> deleteTeam(@RequestParam Long teamId) {

        Long userId = Long.parseLong(UserContext.getUserId());
        String role = UserContext.getUserRole();

        Team team = teamService.getById(teamId);
        if (team == null || !team.getStatus().equals(1)) {
            return ResponseEntity.status(404).body(Map.of("error", "团队不存在"));
        }

        if (team.getStatus().equals(3)) {
            return ResponseEntity.status(400).body(Map.of("error", "团队已被删除"));
        }

        if (role.equals(JwtUtil.USER) && !userId.equals(team.getOwnerId())) {
            return ResponseEntity.status(403).body(Map.of("error", "无权限操作"));
        }

        team.setStatus(3); // 3 已删除
        if (!teamService.updateById(team)) {
            return ResponseEntity.status(500).body(Map.of("error", "删除团队失败"));
        }

        return ResponseEntity.ok(Map.of("team", team));
    }

    @Operation(summary = "申请加入团队")
    @PostMapping("/apply-join")
    public ResponseEntity<?> applyJoinTeam(@RequestParam Long teamId) {

        Long userId = Long.parseLong(UserContext.getUserId());

        // 团队合法性检验
        Team team = teamService.getById(teamId);
        if (team == null || team.getStatus() != 1) {
            return ResponseEntity.status(404).body(Map.of("error", "团队不存在或已被删除"));
        }

        // 已是成员检验
        if (teamMemberService.getTeamMember(team.getId(), userId) != null) {
            return ResponseEntity.status(400).body(Map.of("error", "已是团队成员"));
        }

        // 是否已提交申请检验
        TeamJoinApply teamJoinApply = teamJoinApplyService.getTeamJoinApply(teamId, userId);
        if (teamJoinApply != null && teamJoinApply.getStatus() == 0) {
            return ResponseEntity.status(400).body(Map.of("error", "已提交加入团队申请，正在审核中"));
        }

        teamJoinApply = TeamJoinApply.builder()
                .applicantId(userId)
                .teamId(teamId)
                .applyTime(LocalDateTime.now())
                .status(0) // 0 待审核
                .build();

        if (!teamJoinApplyService.save(teamJoinApply)) {
            return ResponseEntity.status(500).body(Map.of("error", "申请加入团队失败"));
        }

        return ResponseEntity.ok(Map.of("teamJoinApply", teamJoinApply));
    }

    @Operation(summary = "同意加入团队申请")
    @PostMapping("/approve-join-apply")
    public ResponseEntity<?> approveJoinTeamApplication(@RequestParam Long teamJoinApplyId) {

        Long userId = Long.parseLong(UserContext.getUserId());
        String role = UserContext.getUserRole();

        // 申请合法性检验
        TeamJoinApply teamJoinApply = teamJoinApplyService.getById(teamJoinApplyId);
        if (teamJoinApply == null || teamJoinApply.getStatus() != 0) {
            return ResponseEntity.status(404).body(Map.of("error", "加入团队申请不存在或已被处理"));
        }

        // 已是成员检验
        if (teamMemberService.getTeamMember(teamJoinApply.getTeamId(), teamJoinApply.getApplicantId()) != null) {
            return ResponseEntity.status(400).body(Map.of("error", "已是团队成员"));
        }

        // 团队合法性检验
        Team team = teamService.getById(teamJoinApply.getTeamId());
        if (team == null || team.getStatus() != 1) {
            return ResponseEntity.status(404).body(Map.of("error", "团队不存在或已被删除"));
        }

        if (role.equals(JwtUtil.USER) && !userId.equals(team.getOwnerId())) {
            return ResponseEntity.status(403).body(Map.of("error", "无权限操作"));
        }

        // 更新申请状态
        teamJoinApply.setStatus(1); // 1 已同意
        if (!teamJoinApplyService.updateById(teamJoinApply)) {
            return ResponseEntity.status(500).body(Map.of("error", "更新加入团队申请状态失败"));
        }

        // 添加成员
        TeamMember teamMember = TeamMember.builder()
                .memberId(teamJoinApply.getApplicantId())
                .teamId(teamJoinApply.getTeamId())
                .position("普通成员")
                .joinTime(LocalDateTime.now())
                .build();

        if (!teamMemberService.save(teamMember)) {
            return ResponseEntity.status(500).body(Map.of("error", "添加团队成员失败"));
        }

        return ResponseEntity.ok(Map.of("teamJoinApply", teamJoinApply));
    }

    @Operation(summary = "获取团队二跳知识图谱（团队->成员->其他团队）")
    @PostMapping("/graph")
    public ResponseEntity<?> getTeamGraph(@RequestParam Long teamId) {
        Team team = teamService.getById(teamId);
        if (team == null || team.getStatus() == null || team.getStatus() != 1) {
            return ResponseEntity.status(404).body(Map.of("error", "团队不存在或未通过审核"));
        }

        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> links = new ArrayList<>();
        Set<String> nodeIds = new HashSet<>();
        Set<String> linkKeys = new HashSet<>();
        Map<Long, Team> teamCache = new HashMap<>();

        Supplier<Map<Long, Team>> cacheSupplier = () -> teamCache;

        // helper to add node
        var addNode = (java.util.function.BiConsumer<Map<String, Object>, Integer>) (node, symbolSize) -> {
            String id = (String) node.get("id");
            if (nodeIds.add(id)) {
                if (symbolSize != null) {
                    node.put("symbolSize", symbolSize);
                }
                nodes.add(node);
            }
        };

        // helper to add link
        java.util.function.Consumer<Map<String, String>> addLink = link -> {
            String key = link.get("source") + "->" + link.get("target");
            if (linkKeys.add(key)) {
                links.add(new HashMap<>(link));
            }
        };

        // center team node
        addNode.accept(new HashMap<>(Map.of(
                "id", "team:" + team.getId(),
                "name", team.getName(),
                "category", "Org"
        )), 42);

        // members
        List<TeamMemberVO> members = teamMemberService.getTeamMemberList(teamId);
        for (TeamMemberVO member : members) {
            if (member.getUser() == null || member.getUser().getId() == null) {
                continue;
            }
            Long memberId = member.getUser().getId();
            String memberName = member.getUser().getName() != null && !member.getUser().getName().isEmpty()
                    ? member.getUser().getName()
                    : member.getUser().getUsername();
            String memberNodeId = "user:" + memberId;

            addNode.accept(new HashMap<>(Map.of(
                    "id", memberNodeId,
                    "name", memberName != null ? memberName : "User " + memberId,
                    "category", "Member"
            )), 28);

            addLink.accept(Map.of(
                    "source", "team:" + team.getId(),
                    "target", memberNodeId
            ));

            // other teams this member joined (excluding current, excluding deleted/rejected)
            List<Long> joinedTeamIds = teamMemberService.getTeamIdsByMemberId(memberId);
            for (Long otherTeamId : joinedTeamIds) {
                if (otherTeamId == null || otherTeamId.equals(teamId)) {
                    continue;
                }

                Team otherTeam = cacheSupplier.get().computeIfAbsent(otherTeamId, id -> teamService.getById(id));
                if (otherTeam == null || otherTeam.getStatus() == null || otherTeam.getStatus() != 1) {
                    continue; // skip non-approved/deleted
                }

                String otherTeamNodeId = "team:" + otherTeam.getId();
                addNode.accept(new HashMap<>(Map.of(
                        "id", otherTeamNodeId,
                        "name", otherTeam.getName(),
                        "category", "Related Org"
                )), 34);

                addLink.accept(Map.of(
                        "source", memberNodeId,
                        "target", otherTeamNodeId
                ));
            }
        }

        return ResponseEntity.ok(Map.of(
                "nodes", nodes,
                "links", links
        ));
    }
}
