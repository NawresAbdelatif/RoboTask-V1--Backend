package com.example.acwa.controllers;

import com.example.acwa.Dto.LoginRequest;
import com.example.acwa.Dto.SignupRequest;
import com.example.acwa.Dto.JwtResponse;
import com.example.acwa.entities.User;
import com.example.acwa.repositories.UserRepository;
import com.example.acwa.security.JwtService;
import com.example.acwa.security.JwtUtils;
import com.example.acwa.security.UserDetailsImpl;
import com.example.acwa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.example.acwa.repositories.VerificationTokenRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Map;
import com.example.acwa.Dto.UserResponseDTO;


import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        try {
            userService.registerUser(request);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        // Ajoute explicitement les WebAuthenticationDetails
        authRequest.setDetails(new WebAuthenticationDetails(request));

        Authentication authentication = authenticationManager.authenticate(authRequest);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(item -> item.getAuthority())
                .toList();

        JwtResponse jwtResponse = new JwtResponse();
        jwtResponse.setToken(jwt);
        jwtResponse.setUsername(userDetails.getUsername());
        jwtResponse.setRoles(roles);

        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Hello Admin");
    }

    @GetMapping("/visitor")
    @PreAuthorize("hasRole('VISITOR')")
    public ResponseEntity<String> visitorAccess() {
        return ResponseEntity.ok("Hello Visitor");
    }
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserRoles(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities());
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam String token) {
        var optToken = verificationTokenRepository.findByToken(token);
        if (optToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token invalide"));
        }
        var verificationToken = optToken.get();
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token expiré"));
        }
        User user = verificationToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);

        verificationTokenRepository.delete(verificationToken); // Token à usage unique
        return ResponseEntity.ok(Map.of("message", "Compte activé, vous pouvez vous connecter."));
    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String role
    ) {
        List<User> users = userRepository.findAll();

        List<UserResponseDTO> result = users.stream()
                .filter(u -> (q == null || u.getUsername().toLowerCase().contains(q.toLowerCase()) || u.getEmail().toLowerCase().contains(q.toLowerCase())))
                .filter(u -> (role == null || u.getRoles().stream().anyMatch(r -> r.getName().name().equals(role))))
                .map(u -> {
                    UserResponseDTO dto = new UserResponseDTO();
                    dto.setId(u.getId());
                    dto.setUsername(u.getUsername());
                    dto.setEmail(u.getEmail());
                    Set<String> roles = u.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet());
                    dto.setRoles(roles);
                    return dto;
                })
                .toList();

        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> req) {
        try {
            String newRole = req.get("role");
            userService.changeUserRole(userId, newRole);
            return ResponseEntity.ok(Map.of("message", "Rôle mis à jour"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }



}
