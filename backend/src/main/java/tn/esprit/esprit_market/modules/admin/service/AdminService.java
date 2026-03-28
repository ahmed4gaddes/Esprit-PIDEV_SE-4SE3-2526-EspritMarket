package tn.esprit.esprit_market.modules.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.esprit_market.exceptions.UserException;
import tn.esprit.esprit_market.modules.admin.dto.AdminAuditLogDTO;
import tn.esprit.esprit_market.modules.admin.dto.AdminDailyActivityDTO;
import tn.esprit.esprit_market.modules.admin.dto.AdminDashboardKpiDTO;
import tn.esprit.esprit_market.modules.admin.dto.AdminDashboardResponseDTO;
import tn.esprit.esprit_market.modules.admin.dto.PagedResponseDTO;
import tn.esprit.esprit_market.modules.admin.entity.AdminAuditLog;
import tn.esprit.esprit_market.modules.admin.repository.AdminAuditLogRepository;
import tn.esprit.esprit_market.modules.event.entities.Ticket;
import tn.esprit.esprit_market.modules.event.repositories.EventRepository;
import tn.esprit.esprit_market.modules.event.repositories.LiveSessionRepository;
import tn.esprit.esprit_market.modules.event.repositories.TicketRepository;
import tn.esprit.esprit_market.modules.service.dto.InternshipApplicationDTO;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplication;
import tn.esprit.esprit_market.modules.service.entity.InternshipApplicationStatus;
import tn.esprit.esprit_market.modules.service.repository.InternshipApplicationRepository;
import tn.esprit.esprit_market.modules.service.repository.InternshipRepository;
import tn.esprit.esprit_market.modules.store.repository.IRepositoryProduct;
import tn.esprit.esprit_market.modules.store.repository.StoreRepository;
import tn.esprit.esprit_market.modules.user.entity.User;
import tn.esprit.esprit_market.modules.user.enums.Role;
import tn.esprit.esprit_market.modules.user.repository.UserRepository;
import tn.esprit.esprit_market.modules.user.service.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final IRepositoryProduct productRepository;
    private final EventRepository eventRepository;
    private final LiveSessionRepository liveSessionRepository;
    private final InternshipRepository internshipRepository;
    private final InternshipApplicationRepository internshipApplicationRepository;
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final AdminAuditLogRepository adminAuditLogRepository;

    public AdminDashboardResponseDTO getDashboard(int days) {
        List<User> users = userRepository.findAll();
        List<InternshipApplication> applications = internshipApplicationRepository.findAll();
        List<Ticket> tickets = ticketRepository.findAll();

        AdminDashboardKpiDTO kpis = AdminDashboardKpiDTO.builder()
                .totalUsers(users.size())
                .activeUsers(users.stream().filter(User::isActive).count())
                .totalStores(storeRepository.count())
                .totalProducts(productRepository.count())
                .totalEvents(eventRepository.count())
                .totalLives(liveSessionRepository.count())
                .totalInternships(internshipRepository.count())
                .pendingInternshipApplications(
                        applications.stream().filter(a -> a.getStatus() == InternshipApplicationStatus.PENDING).count())
                .ticketsSold(tickets.size())
                .ticketRevenue(tickets.stream().mapToDouble(Ticket::getPrice).sum())
                .build();

        return AdminDashboardResponseDTO.builder()
                .kpis(kpis)
                .activity(buildActivity(days, users, applications, tickets))
                .build();
    }

    public PagedResponseDTO<User> getUsers(String role, Boolean active, String q, int page, int size) {
        List<User> filtered = userRepository.findAll().stream()
                .filter(u -> role == null || role.isBlank() || u.getRole().name().equalsIgnoreCase(role))
                .filter(u -> active == null || u.isActive() == active)
                .filter(u -> {
                    if (q == null || q.isBlank()) {
                        return true;
                    }
                    String needle = q.toLowerCase(Locale.ROOT).trim();
                    String name = u.getName() == null ? "" : u.getName().toLowerCase(Locale.ROOT);
                    String email = u.getEmail() == null ? "" : u.getEmail().toLowerCase(Locale.ROOT);
                    return name.contains(needle) || email.contains(needle);
                })
                .sorted(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());

        return toPage(filtered, page, size);
    }

    public User updateUserRole(Long id, String role, String actorEmail) {
        Role parsedRole = Role.valueOf(role.trim().toUpperCase(Locale.ROOT));
        User user = userService.getUserById(id);
        User actor = userService.getUserByEmail(actorEmail);

        if (actor.getId().equals(user.getId()) && parsedRole != Role.ADMIN) {
            logAudit("USER_ROLE_UPDATE_BLOCKED_SELF", actor, user,
                    "Blocked attempt to remove own ADMIN role. Requested role=" + parsedRole);
            throw new UserException("You cannot remove your own ADMIN role.");
        }
        if (user.getRole() == Role.ADMIN && parsedRole != Role.ADMIN && countActiveAdmins() <= 1) {
            logAudit("USER_ROLE_UPDATE_BLOCKED_LAST_ADMIN", actor, user,
                    "Blocked attempt to demote last active admin. Requested role=" + parsedRole);
            throw new UserException("Cannot demote the last active admin.");
        }

        Role previousRole = user.getRole();
        user.setRole(parsedRole);
        User saved = userRepository.save(user);
        logAudit("USER_ROLE_UPDATED", actor, saved,
                "Role changed from " + previousRole + " to " + parsedRole);
        return saved;
    }

    public User toggleUserStatus(Long id, String actorEmail) {
        User target = userService.getUserById(id);
        User actor = userService.getUserByEmail(actorEmail);

        if (actor.getId().equals(target.getId())) {
            logAudit("USER_STATUS_TOGGLE_BLOCKED_SELF", actor, target,
                    "Blocked attempt to disable own account.");
            throw new UserException("You cannot disable your own account.");
        }
        if (target.getRole() == Role.ADMIN && target.isActive() && countActiveAdmins() <= 1) {
            logAudit("USER_STATUS_TOGGLE_BLOCKED_LAST_ADMIN", actor, target,
                    "Blocked attempt to disable last active admin.");
            throw new UserException("Cannot disable the last active admin.");
        }

        User updated = userService.toggleUserStatus(id);
        logAudit("USER_STATUS_TOGGLED", actor, updated,
                "User active status toggled to " + updated.isActive());
        return updated;
    }

    public void deleteUser(Long id, String actorEmail) {
        User target = userService.getUserById(id);
        User actor = userService.getUserByEmail(actorEmail);

        if (actor.getId().equals(target.getId())) {
            logAudit("USER_DELETE_BLOCKED_SELF", actor, target,
                    "Blocked attempt to delete own account.");
            throw new UserException("You cannot delete your own account.");
        }
        if (target.getRole() == Role.ADMIN && target.isActive() && countActiveAdmins() <= 1) {
            logAudit("USER_DELETE_BLOCKED_LAST_ADMIN", actor, target,
                    "Blocked attempt to delete last active admin.");
            throw new UserException("Cannot delete the last active admin.");
        }

        userService.deleteUser(id);
        logAudit("USER_DELETED", actor, target, "User account deleted.");
    }

    public PagedResponseDTO<AdminAuditLogDTO> getAuditLogs(String action, String q, int page, int size) {
        List<AdminAuditLogDTO> filtered = adminAuditLogRepository.findAll().stream()
                .filter(log -> action == null || action.isBlank() || log.getAction().equalsIgnoreCase(action))
                .filter(log -> {
                    if (q == null || q.isBlank()) {
                        return true;
                    }
                    String needle = q.toLowerCase(Locale.ROOT).trim();
                    String actor = log.getActorEmail() == null ? "" : log.getActorEmail().toLowerCase(Locale.ROOT);
                    String target = log.getTargetUserEmail() == null ? "" : log.getTargetUserEmail().toLowerCase(Locale.ROOT);
                    String details = log.getDetails() == null ? "" : log.getDetails().toLowerCase(Locale.ROOT);
                    return actor.contains(needle) || target.contains(needle) || details.contains(needle);
                })
                .sorted(Comparator.comparing(AdminAuditLog::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapAuditLog)
                .collect(Collectors.toList());

        return toPage(filtered, page, size);
    }

    public PagedResponseDTO<InternshipApplicationDTO> getInternshipApplications(
            String status, Long internshipId, Long companyId, String q, int page, int size) {
        List<InternshipApplicationDTO> filtered = internshipApplicationRepository.findAll().stream()
                .filter(app -> status == null || status.isBlank()
                        || app.getStatus().name().equalsIgnoreCase(status))
                .filter(app -> internshipId == null || app.getInternship().getId().equals(internshipId))
                .filter(app -> companyId == null || (app.getInternship().getCreator() != null
                        && app.getInternship().getCreator().getId() != null
                        && app.getInternship().getCreator().getId().equals(companyId)))
                .filter(app -> {
                    if (q == null || q.isBlank()) {
                        return true;
                    }
                    String needle = q.toLowerCase(Locale.ROOT).trim();
                    String applicantName = app.getApplicant().getName() == null ? "" : app.getApplicant().getName().toLowerCase(Locale.ROOT);
                    String applicantEmail = app.getApplicant().getEmail() == null ? "" : app.getApplicant().getEmail().toLowerCase(Locale.ROOT);
                    String internshipTitle = app.getInternship().getTitle() == null ? "" : app.getInternship().getTitle().toLowerCase(Locale.ROOT);
                    return applicantName.contains(needle) || applicantEmail.contains(needle) || internshipTitle.contains(needle);
                })
                .sorted(Comparator.comparing(InternshipApplication::getAppliedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::mapApplication)
                .collect(Collectors.toList());

        return toPage(filtered, page, size);
    }

    private InternshipApplicationDTO mapApplication(InternshipApplication app) {
        InternshipApplicationDTO dto = new InternshipApplicationDTO();
        dto.setId(app.getId());
        dto.setInternshipId(app.getInternship().getId());
        dto.setInternshipTitle(app.getInternship().getTitle());
        dto.setApplicantId(app.getApplicant().getId());
        dto.setApplicantName(app.getApplicant().getName());
        dto.setApplicantEmail(app.getApplicant().getEmail());
        dto.setCvUrl(app.getCvUrl());
        dto.setCoverLetter(app.getCoverLetter());
        dto.setStatus(app.getStatus());
        dto.setAppliedAt(app.getAppliedAt());
        dto.setReviewedAt(app.getReviewedAt());
        dto.setReviewerComment(app.getReviewerComment());
        return dto;
    }

    private List<AdminDailyActivityDTO> buildActivity(int days, List<User> users,
                                                      List<InternshipApplication> applications, List<Ticket> tickets) {
        int safeDays = Math.max(1, Math.min(days, 30));
        List<AdminDailyActivityDTO> points = new ArrayList<>();
        LocalDate now = LocalDate.now();

        for (int i = safeDays - 1; i >= 0; i--) {
            LocalDate date = now.minusDays(i);
            long newUsers = users.stream()
                    .filter(u -> toLocalDate(u.getCreatedAt()) != null && toLocalDate(u.getCreatedAt()).equals(date))
                    .count();
            long newApplications = applications.stream()
                    .filter(a -> toLocalDate(a.getAppliedAt()) != null && toLocalDate(a.getAppliedAt()).equals(date))
                    .count();
            long soldTickets = tickets.stream()
                    .filter(t -> toLocalDate(t.getPurchaseDate()) != null && toLocalDate(t.getPurchaseDate()).equals(date))
                    .count();

            points.add(AdminDailyActivityDTO.builder()
                    .date(date.toString())
                    .newUsers(newUsers)
                    .newApplications(newApplications)
                    .ticketsSold(soldTickets)
                    .build());
        }

        return points;
    }

    private LocalDate toLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private <T> PagedResponseDTO<T> toPage(List<T> source, int page, int size) {
        int safeSize = Math.max(1, Math.min(size, 100));
        int safePage = Math.max(page, 0);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, source.size());
        List<T> items = fromIndex >= source.size() ? List.of() : source.subList(fromIndex, toIndex);

        int totalPages = source.isEmpty() ? 0 : (int) Math.ceil((double) source.size() / safeSize);
        return PagedResponseDTO.<T>builder()
                .items(items)
                .page(safePage)
                .size(safeSize)
                .totalElements(source.size())
                .totalPages(totalPages)
                .hasNext(safePage + 1 < totalPages)
                .hasPrevious(safePage > 0)
                .build();
    }

    private long countActiveAdmins() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN && u.isActive())
                .count();
    }

    private AdminAuditLogDTO mapAuditLog(AdminAuditLog log) {
        return AdminAuditLogDTO.builder()
                .id(log.getId())
                .action(log.getAction())
                .actorUserId(log.getActorUserId())
                .actorEmail(log.getActorEmail())
                .targetUserId(log.getTargetUserId())
                .targetUserEmail(log.getTargetUserEmail())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt() == null ? null : LocalDateTime.ofInstant(log.getCreatedAt().toInstant(), ZoneId.systemDefault()))
                .build();
    }

    private void logAudit(String action, User actor, User target, String details) {
        AdminAuditLog log = AdminAuditLog.builder()
                .action(action)
                .actorUserId(actor == null ? null : actor.getId())
                .actorEmail(actor == null ? "unknown" : actor.getEmail())
                .targetUserId(target == null ? null : target.getId())
                .targetUserEmail(target == null ? null : target.getEmail())
                .details(details)
                .build();
        adminAuditLogRepository.save(log);
    }
}
