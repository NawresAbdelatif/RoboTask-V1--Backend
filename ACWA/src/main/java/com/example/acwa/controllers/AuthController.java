package com.example.acwa.controllers;

import com.example.acwa.Dto.*;
import com.example.acwa.entities.User;
import com.example.acwa.repositories.UserRepository;
import com.example.acwa.security.JwtService;
import com.example.acwa.security.JwtUtils;
import com.example.acwa.security.UserDetailsImpl;
import com.example.acwa.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.example.acwa.repositories.VerificationTokenRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.Map;


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
        try {
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            authRequest.setDetails(new WebAuthenticationDetails(request));
            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtils.generateJwtToken(userDetails);
            List<String> roles = userDetails.getAuthorities()
                    .stream().map(item -> item.getAuthority()).toList();

            User user = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            JwtResponse jwtResponse = new JwtResponse();
            jwtResponse.setToken(jwt);
            jwtResponse.setUsername(user.getUsername()); // <= ici : renvoie le nom stocké lors de l'inscription !
            jwtResponse.setRoles(roles);

            return ResponseEntity.ok(jwtResponse);

        } catch (DisabledException ex) {
            // Compte désactivé
            return ResponseEntity.status(403)
                    .body(Map.of("message", "Votre compte est désactivé. Veuillez contacter l'administrateur."));
        } catch (BadCredentialsException ex) {
            // Identifiants invalides
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Identifiants invalides"));
        } catch (Exception ex) {
            // Autres erreurs
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Erreur interne : " + ex.getMessage()));
        }
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
            return ResponseEntity.badRequest().body(Map.of("message", "Lien d’activation invalide ou expiré."));
        }

        var verificationToken = optToken.get();
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Lien d’activation expiré."));
        }

        User user = verificationToken.getUser();
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ce compte est déjà activé."));
        }

        user.setEnabled(true);
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return ResponseEntity.ok(Map.of("message", "Compte activé, vous pouvez vous connecter."));
    }


    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> req) {
        try {
            String newRole = req.get("role");
            System.out.println("========= ROLE DEMANDE: " + newRole);
            userService.changeUserRole(userId, newRole);
            return ResponseEntity.ok(Map.of("message", "Rôle mis à jour"));
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé !"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/enabled")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setUserEnabled(@PathVariable Long userId, @RequestBody Map<String, Boolean> req) {
        try {
            Boolean enabled = req.get("enabled");
            userService.setUserEnabled(userId, enabled != null ? enabled : false);
            return ResponseEntity.ok(Map.of("message", "Statut de l'utilisateur mis à jour !"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetailsImpl userDetails) {
            return ResponseEntity.ok(userService.getUserProfile(userDetails.getUsername()));
        }

        return ResponseEntity.badRequest().build();
    }


    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody UserProfileUpdateDTO dto) {
        UserProfileDTO updated = userService.updateUserProfile(currentUser.getUsername(), dto);

        // Regénère le JWT si le username a changé
        if (!currentUser.getUsername().equals(dto.getUsername())) {
            // Génère un nouveau JWT pour le nouveau username
            String newToken = jwtUtils.generateJwtTokenForUsername(dto.getUsername());
            Map<String, Object> response = new HashMap<>();
            response.put("profile", updated);
            response.put("token", newToken);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(Map.of("profile", updated));
    }
    @PutMapping("/password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetailsImpl currentUser,
            @RequestBody PasswordChangeDTO dto) {
        userService.changePassword(currentUser.getUsername(), dto);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié, veuillez vous reconnecter."));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> req) {
        userService.generatePasswordResetToken(req.get("email"));
        return ResponseEntity.ok(Map.of("message", "Si ce mail existe, un lien de réinitialisation a été envoyé."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody Map<String, String> req) {
        userService.resetPassword(token, req.get("password"));
        return ResponseEntity.ok(Map.of("message", "Mot de passe réinitialisé. Vous pouvez vous connecter."));
    }

//    @GetMapping("/users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
//        List<UserProfileDTO> users = userService.getAllUserProfiles();
//        return ResponseEntity.ok(users);
//    }
@GetMapping("/users")
public ResponseEntity<?> getFilteredUsers(
        @RequestParam(defaultValue = "") String search,
        @RequestParam(defaultValue = "all") String role,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<UserProfileDTO> userPage = userService.getFilteredUsers(search, role, pageable);

    Map<String, Object> response = new HashMap<>();
    response.put("users", userPage.getContent());
    response.put("currentPage", userPage.getNumber());
    response.put("totalItems", userPage.getTotalElements());
    response.put("totalPages", userPage.getTotalPages());

    return ResponseEntity.ok(response);
}
    @GetMapping("/users/count-enabled")
    public ResponseEntity<Long> countEnabledUsers() {
        long count = userService.countEnabledUsers();
        return ResponseEntity.ok(count);
    }

}
