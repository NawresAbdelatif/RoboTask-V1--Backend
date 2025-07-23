package com.example.acwa.controllers;
import com.example.acwa.Dto.PieceRequestDTO;
import com.example.acwa.Dto.PieceResponseDTO;
import com.example.acwa.entities.Piece;
import com.example.acwa.mappers.PieceMapper;
import com.example.acwa.services.PieceService;
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

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/pieces")
@CrossOrigin(origins = "http://localhost:4200")
public class PieceController {

    @Autowired
    private PieceService pieceService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PostMapping("/create")
    public ResponseEntity<?> createPiece(@RequestBody PieceRequestDTO dto, Authentication auth) {
        String email = auth.getName();
        try {
            return ResponseEntity.ok(pieceService.createPiece(dto, email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Définir le dossier d’upload (ex : ./uploads)
            String uploadDir = "uploads/";
            Files.createDirectories(Paths.get(uploadDir));

            // Générer un nom unique
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir + fileName);

            // Enregistrer le fichier
            Files.copy(file.getInputStream(), filePath);

            // Retourner l’URL relative du fichier
            String imageUrl = "/uploads/" + fileName;
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur upload image");
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PutMapping("/{id}/update")
    public ResponseEntity<?> updatePiece(@PathVariable Long id, @RequestBody PieceRequestDTO dto, Authentication auth) {
        String email = auth.getName();
        try {
            return ResponseEntity.ok(pieceService.updatePiece(id, dto, email));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> deletePiece(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        try {
            pieceService.deletePiece(id, email);
            System.out.println("[BACK] Pièce supprimée OK id=" + id + " par " + email);
            return ResponseEntity.ok(Collections.singletonMap("message", "Pièce supprimée !"));
        } catch (RuntimeException e) {
            System.err.println("[BACK] Erreur suppression id=" + id + " : " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping
    public ResponseEntity<Page<PieceResponseDTO>> getAllPieces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Piece> pageData = pieceService.searchPieces(search, pageable);
        Page<PieceResponseDTO> pageDTO = pageData.map(PieceMapper::toDTO);
        return ResponseEntity.ok(pageDTO);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getPieceById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(pieceService.getPieceById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
