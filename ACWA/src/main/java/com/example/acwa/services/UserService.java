package com.example.acwa.services;
import com.example.acwa.Dto.PasswordChangeDTO;
import com.example.acwa.Dto.SignupRequest;
import com.example.acwa.Dto.UserProfileDTO;
import com.example.acwa.Dto.UserProfileUpdateDTO;
import com.example.acwa.entities.Role;
import com.example.acwa.entities.RoleName;
import com.example.acwa.entities.User;
import com.example.acwa.entities.VerificationToken;
import com.example.acwa.repositories.RoleRepository;
import com.example.acwa.repositories.UserRepository;
import com.example.acwa.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private JavaMailSender mailSender;

    public void registerUser(SignupRequest request) {


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already used");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(false);

        Set<Role> userRoles = new HashSet<>();

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            Role defaultRole = roleRepository.findByName(RoleName.ROLE_VISITOR)
                    .orElseThrow(() -> new RuntimeException("Default role not found"));
            userRoles.add(defaultRole);
        } else {
            for (String roleStr : request.getRoles()) {
                RoleName roleName = RoleName.valueOf("ROLE_" + roleStr.toUpperCase());
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found"));
                userRoles.add(role);
            }
        }

        user.setRoles(userRoles);
        userRepository.save(user);

        // --- Génère un token de vérification ---
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // valable 24h
        verificationTokenRepository.save(verificationToken);

        // --- Prépare et envoie le mail de vérification ---
        String url = "http://localhost:4200/activate?token=" + token;        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Vérification de votre email");
        mailMessage.setText(
                "Bonjour " + user.getUsername() + ",\n\n"
                        + "Merci de vous être inscrit. Veuillez cliquer sur le lien ci-dessous pour activer votre compte :\n"
                        + url + "\n\n"
                        + "Ce lien est valable 24h.\n\nL'équipe ACWA."
        );

        mailSender.send(mailMessage);
    }

    public void generatePasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun utilisateur"));

        // Supprimer l'ancien token de ce user s'il existe
        verificationTokenRepository.deleteAllByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        VerificationToken resetToken = new VerificationToken(token, user, LocalDateTime.now().plusHours(1));
        verificationTokenRepository.save(resetToken);

        String url = "http://localhost:4200/pages/reset-password?token=" + token;

        // Préparer et envoyer le mail ici !
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("Réinitialisation de votre mot de passe");
        mailMessage.setText(
                "Bonjour " + user.getUsername() + ",\n\n"
                        + "Veuillez cliquer sur le lien ci-dessous pour réinitialiser votre mot de passe :\n"
                        + url + "\n\n"
                        + "Ce lien est valable 1h.\n\nL'équipe ACWA."
        );

        mailSender.send(mailMessage);
    }

    public void resetPassword(String token, String newPassword) {
        VerificationToken vt = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));
        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) throw new RuntimeException("Token expiré");
        User user = vt.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        verificationTokenRepository.delete(vt);
    }

//    public void registerUser(SignupRequest request) {
//        if (userRepository.existsByUsername(request.getUsername())) {
//            throw new RuntimeException("Username already taken");
//        }
//
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new RuntimeException("Email already used");
//        }
//
//        User user = new User();
//        user.setUsername(request.getUsername());
//        user.setEmail(request.getEmail());
//        user.setPassword(passwordEncoder.encode(request.getPassword()));
//
//        Set<Role> userRoles = new HashSet<>();
//
//        if (request.getRoles() == null || request.getRoles().isEmpty()) {
//            Role defaultRole = roleRepository.findByName(RoleName.ROLE_VISITOR)
//                    .orElseThrow(() -> new RuntimeException("Default role not found"));
//            userRoles.add(defaultRole);
//        } else {
//            for (String roleStr : request.getRoles()) {
//                RoleName roleName = RoleName.valueOf("ROLE_" + roleStr.toUpperCase());
//                Role role = roleRepository.findByName(roleName)
//                        .orElseThrow(() -> new RuntimeException("Role not found"));
//                userRoles.add(role);
//            }
//        }
//
//        user.setRoles(userRoles);
//        userRepository.save(user);
//    }

    public UserProfileDTO getUserProfile(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));
        return dto;
    }

    public UserProfileDTO updateUserProfile(String username, UserProfileUpdateDTO updateDTO) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        // Si l'email change, vérifie qu'il n'est pas déjà utilisé par un autre user
        if (!user.getEmail().equals(updateDTO.getEmail()) && userRepository.existsByEmail(updateDTO.getEmail())) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        user.setUsername(updateDTO.getUsername());
        user.setEmail(updateDTO.getEmail());
        userRepository.save(user);
        return getUserProfile(username);
    }

    public void changePassword(String username, PasswordChangeDTO dto) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }


    public void changeUserRole(Long userId, String newRoleName) {
        if (newRoleName == null || newRoleName.isBlank()) {
            throw new RuntimeException("Role is required");
        }
        RoleName rn;
        try {
            rn = RoleName.valueOf(newRoleName);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid role: " + newRoleName);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role role = roleRepository.findByName(rn)
                .orElseThrow(() -> new RuntimeException("Role not found in DB: " + rn));
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
        user.setRoles(roleSet);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Utilisateur introuvable");
        }
        verificationTokenRepository.deleteAllByUserId(userId);
        userRepository.deleteById(userId);
    }
    public void setUserEnabled(Long userId, boolean enabled) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    public List<UserProfileDTO> getAllUserProfiles() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserProfileDTO dto = new UserProfileDTO();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setRoles(user.getRoles().stream()
                            .map(role -> role.getName().name())
                            .collect(Collectors.toSet()));
                    dto.setEnabled(user.isEnabled());
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public Page<UserProfileDTO> getFilteredUsers(String query, String role, Pageable pageable) {
        Page<User> userPage;

        boolean hasQuery = query != null && !query.isBlank();
        boolean hasRole = role != null && !role.equalsIgnoreCase("all");

        RoleName roleName = null;
        if (hasRole) {
            try {
                roleName = RoleName.valueOf(role); // conversion sécurisée
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Rôle invalide : " + role);
            }
        }

        if (hasQuery && hasRole) {
            userPage = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndRoles_Name(
                    query, query, roleName, pageable);
        } else if (hasQuery) {
            userPage = userRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    query, query, pageable);
        } else if (hasRole) {
            userPage = userRepository.findByRoles_Name(roleName, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(user -> {
            UserProfileDTO dto = new UserProfileDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setEmail(user.getEmail());
            dto.setRoles(user.getRoles().stream()
                    .map(r -> r.getName().name())
                    .collect(Collectors.toSet()));
            dto.setEnabled(user.isEnabled());
            return dto;
        });
    }
    public long countEnabledUsers() {
        return userRepository.countByEnabled(true);
    }

}






