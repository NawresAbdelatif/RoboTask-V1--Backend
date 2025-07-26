package com.example.acwa.controllers;

import com.example.acwa.Dto.AssemblageRequestDTO;
import com.example.acwa.Dto.AssemblageResponseDTO;
import com.example.acwa.Dto.PageResult;
import com.example.acwa.entities.Assemblage;
import com.example.acwa.mappers.AssemblageMapper;
import com.example.acwa.services.AssemblageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/assemblages")
@CrossOrigin(origins = "http://localhost:4200")
public class AssemblageController {

    @Autowired
    private AssemblageService assemblageService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PostMapping("/create")
    public ResponseEntity<AssemblageResponseDTO> create(
            @PathVariable Long projectId,
            @RequestBody AssemblageRequestDTO dto,
            Authentication auth) {
        String email = auth.getName();
        Assemblage assemblage = assemblageService.createAssemblage(projectId, dto, email);
        return ResponseEntity.ok(AssemblageMapper.toDTO(assemblage));
    }

    @GetMapping
    public ResponseEntity<PageResult<AssemblageResponseDTO>> list(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dateCreation").descending());
        Page<Assemblage> assemblages = assemblageService.getAssemblagesForProject(projectId, search, pageable);

        List<AssemblageResponseDTO> dtos = assemblages.getContent().stream()
                .map(AssemblageMapper::toDTO)
                .collect(Collectors.toList());

        PageResult<AssemblageResponseDTO> result = new PageResult<>(
                dtos,
                assemblages.getTotalElements(),
                assemblages.getTotalPages(),
                assemblages.getNumber()
        );
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssemblageResponseDTO> get(@PathVariable Long id) {
        Assemblage assemblage = assemblageService.getAssemblageById(id);
        return ResponseEntity.ok(AssemblageMapper.toDTO(assemblage));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PutMapping("/{id}/update")
    public ResponseEntity<AssemblageResponseDTO> update(
            @PathVariable Long id,
            @RequestBody AssemblageRequestDTO dto,
            Authentication auth) {
        String email = auth.getName();
        Assemblage updated = assemblageService.updateAssemblage(id, dto, email);
        return ResponseEntity.ok(AssemblageMapper.toDTO(updated));
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        assemblageService.deleteAssemblage(id, email);
        return ResponseEntity.ok().build();
    }
}



