package com.example.acwa.controllers;

import com.example.acwa.Dto.OutilRequestDTO;
import com.example.acwa.Dto.OutilResponseDTO;
import com.example.acwa.entities.Outil;
import com.example.acwa.mappers.OutilMapper;
import com.example.acwa.services.OutilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

@RestController
@RequestMapping("/api/outils")
@CrossOrigin(origins = "http://localhost:4200")
public class OutilController {

    @Autowired
    private OutilService outilService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createOutil(@RequestBody OutilRequestDTO dto, Authentication auth) {
        String email = auth.getName();
        try {
            return ResponseEntity.ok(outilService.createOutil(dto, email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            Files.copy(file.getInputStream(), filePath);

            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur upload image");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updateOutil(@PathVariable Long id, @RequestBody OutilRequestDTO dto, Authentication auth) {
        String email = auth.getName();
        try {
            return ResponseEntity.ok(outilService.updateOutil(id, dto, email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deleteOutil(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        try {
            outilService.deleteOutil(id, email);
            System.out.println("[BACK] Outil supprimée OK id=" + id + " par " + email);
            return ResponseEntity.ok(Collections.singletonMap("message", "Outil supprimée !"));
        } catch (RuntimeException e) {
            System.err.println("[BACK] Erreur suppression id=" + id + " : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<Page<OutilResponseDTO>> getAllOutils(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Outil> pageData = outilService.searchOutils(search, pageable);
        Page<OutilResponseDTO> pageDTO = pageData.map(OutilMapper::toDTO);
        return ResponseEntity.ok(pageDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getOutilById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(outilService.getOutilById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/quantite-total")
    public ResponseEntity<Integer> getTotalQuantite() {
        return ResponseEntity.ok(outilService.getTotalQuantite());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getTotalOutils() {
        return ResponseEntity.ok(outilService.getTotalOutils());
    }
}
