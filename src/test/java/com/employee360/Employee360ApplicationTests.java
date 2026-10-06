package com.employee360;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.employee360.repository.DepartmentRepository;
import com.employee360.repository.LeaveTypeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Employee360ApplicationTests {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${local.server.port}")
    private int port;

    @Test
    void seededRolesAndNewUsersCanApplyForLeaveAndCompleteApproval() throws Exception {
        List<Map.Entry<String, String>> leaveApplicants = List.of(
                Map.entry("depthead", "depthead123"),
                Map.entry("manager", "manager123"),
                Map.entry("employee", "employee123"));

        long leaveTypeId = leaveTypeRepository.findAll().get(0).getId();
        LocalDate baseDate = nextWorkingDay(LocalDate.now().plusDays(30));
        Long employeeRequestId = null;
        String employeeToken = null;

        for (int i = 0; i < leaveApplicants.size(); i++) {
            Map.Entry<String, String> demoUser = leaveApplicants.get(i);
            JsonNode login = login(demoUser.getKey(), demoUser.getValue());
            LocalDate leaveDate = nextWorkingDay(baseDate.plusDays(i * 3L));
            JsonNode request = applyForLeave(
                    login.get("token").asText(),
                    login.get("userId").asLong(),
                    leaveTypeId,
                    leaveDate);

            assertEquals("PENDING", request.get("status").asText());
            if ("employee".equals(demoUser.getKey())) {
                employeeRequestId = request.get("id").asLong();
                employeeToken = login.get("token").asText();
            }
        }

        assertNotNull(employeeRequestId);
        assertEquals(
                200,
                getStatus("/api/approvals/" + employeeRequestId, employeeToken));
        assertEquals(
                403,
                getStatus(
                        "/api/approvals/" + employeeRequestId,
                        login("depthead", "depthead123").get("token").asText()));

        JsonNode adminLogin = login("admin", "admin123");
        JsonNode hrLogin = login("hr", "hr123");
        String adminToken = adminLogin.get("token").asText();
        String hrToken = hrLogin.get("token").asText();
        assertEquals(200, getStatus("/api/admin/users", adminToken));
        assertEquals(403, getStatus("/api/admin/users", hrToken));
        assertEquals(403, getStatus("/api/admin/workflow-rules", hrToken));
        assertEquals(200, getStatus("/api/admin/workflow-rules", adminToken));
        assertEquals(
                403,
                postStatus(
                        "/api/leaves",
                        Map.of(
                                "userId", adminLogin.get("userId").asLong(),
                                "leaveTypeId", leaveTypeId,
                                "startDate", baseDate.plusDays(20).toString(),
                                "endDate", baseDate.plusDays(20).toString(),
                                "reason", "Admin must not apply for leave"),
                        adminToken));
        assertEquals(
                403,
                postStatus(
                        "/api/leaves",
                        Map.of(
                                "userId", hrLogin.get("userId").asLong(),
                                "leaveTypeId", leaveTypeId,
                                "startDate", baseDate.plusDays(23).toString(),
                                "endDate", baseDate.plusDays(23).toString(),
                                "reason", "HR must not apply for leave"),
                        hrToken));

        JsonNode customLeaveType = postJson(
                "/api/admin/leave-types",
                Map.of(
                        "name", "Workflow Test Leave",
                        "entitlement", 5,
                        "maxConsecutiveLeave", 5),
                adminLogin.get("token").asText());
        Long departmentId = departmentRepository.findAll().get(0).getId();
        postJson(
                "/api/admin/users",
                Map.of(
                        "name", "New Manager",
                        "username", "workflow-test-manager",
                        "password", "test-password-123",
                        "role", "MANAGER",
                        "departmentId", departmentId),
                adminLogin.get("token").asText());
        JsonNode newManagerLogin = login("workflow-test-manager", "test-password-123");
        JsonNode newManagerRequest = applyForLeave(
                newManagerLogin.get("token").asText(),
                newManagerLogin.get("userId").asLong(),
                customLeaveType.get("id").asLong(),
                nextWorkingDay(baseDate.plusDays(30)));
        assertEquals("PENDING", newManagerRequest.get("status").asText());

        assertEquals(
                403,
                postStatus(
                        "/api/approvals/" + employeeRequestId + "/approve",
                        Map.of(),
                        adminToken));
        JsonNode managerLogin = login("manager", "manager123");
        JsonNode approved = postJson(
                "/api/approvals/" + employeeRequestId + "/approve",
                Map.of(),
                managerLogin.get("token").asText());
        assertEquals("APPROVED", approved.get("requestStatus").asText());

        LocalDate longLeaveStart = nextWorkingDay(baseDate.plusDays(60));
        JsonNode longLeave = applyForLeave(
                login("employee", "employee123").get("token").asText(),
                login("employee", "employee123").get("userId").asLong(),
                leaveTypeId,
                longLeaveStart,
                longLeaveStart.plusDays(11));

        JsonNode managerApproval = postJson(
                "/api/approvals/" + longLeave.get("id").asLong() + "/approve",
                Map.of(),
                managerLogin.get("token").asText());
        assertEquals("PENDING", managerApproval.get("requestStatus").asText());

        JsonNode departmentHeadLogin = login("depthead", "depthead123");
        JsonNode headApproval = postJson(
                "/api/approvals/" + longLeave.get("id").asLong() + "/approve",
                Map.of(),
                departmentHeadLogin.get("token").asText());
        assertEquals("PENDING", headApproval.get("requestStatus").asText());
        JsonNode hrApproval = postJson(
                "/api/approvals/" + longLeave.get("id").asLong() + "/approve",
                Map.of(),
                hrToken);
        assertEquals("APPROVED", hrApproval.get("requestStatus").asText());
    }

    private JsonNode login(String username, String password) throws Exception {
        return postJson(
                "/api/auth/login",
                Map.of("username", username, "password", password),
                null);
    }

    private JsonNode applyForLeave(
            String token,
            long userId,
            long leaveTypeId,
            LocalDate leaveDate) throws Exception {
        return applyForLeave(token, userId, leaveTypeId, leaveDate, leaveDate);
    }

    private JsonNode applyForLeave(
            String token,
            long userId,
            long leaveTypeId,
            LocalDate startDate,
            LocalDate endDate) throws Exception {
        return postJson(
                "/api/leaves",
                Map.of(
                        "userId", userId,
                        "leaveTypeId", leaveTypeId,
                        "startDate", startDate.toString(),
                        "endDate", endDate.toString(),
                        "reason", "Integration test leave request"),
                token);
    }

    private JsonNode postJson(String path, Object body, String token) throws Exception {
        HttpRequest.Builder request = authorizedRequest(path, token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request.build(), HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        return objectMapper.readTree(response.body());
    }

    private int getStatus(String path, String token) throws Exception {
        HttpRequest request = authorizedRequest(path, token)
                .GET()
                .build();
        return HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString())
                .statusCode();
    }

    private int postStatus(String path, Object body, String token) throws Exception {
        HttpRequest.Builder request = authorizedRequest(path, token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
        return HttpClient.newHttpClient()
                .send(request.build(), HttpResponse.BodyHandlers.ofString())
                .statusCode();
    }

    private HttpRequest.Builder authorizedRequest(String path, String token) {
        HttpRequest.Builder request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                ;

        if (token != null) {
            request.header("Authorization", "Bearer " + token);
        }
        return request;
    }

    private LocalDate nextWorkingDay(LocalDate date) {
        LocalDate nextDate = date;
        while (nextDate.getDayOfWeek() == DayOfWeek.SATURDAY
                || nextDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            nextDate = nextDate.plusDays(1);
        }
        return nextDate;
    }
}
