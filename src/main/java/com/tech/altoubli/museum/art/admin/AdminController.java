package com.tech.altoubli.museum.art.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/delete-user")
    public ResponseEntity<Map<String, String>> deleteUser(
            @RequestBody DeleteRequest request
    ){
        adminService.deleteUser(request.getEmail());
        HashMap<String, String> res = new HashMap<>();
        res.put("Status", "User Account has been locked.");
        return ResponseEntity.ok(res);
    }

    @GetMapping("users-list")
    public List<UserDto> getAllUsers() {
        return adminService.getAllUsers();
    }
}
