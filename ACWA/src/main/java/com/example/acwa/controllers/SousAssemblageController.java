package com.example.acwa.controllers;

import com.example.acwa.Dto.SousAssemblageRequestDTO;
import com.example.acwa.Dto.SousAssemblageResponseDTO;
import com.example.acwa.services.SousAssemblageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sous-assemblages")
public class SousAssemblageController {

    @Autowired
    private SousAssemblageService sousAssemblageService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PostMapping
    public SousAssemblageResponseDTO create(@RequestBody SousAssemblageRequestDTO dto, @RequestParam String email) {
        return sousAssemblageService.createSousAssemblage(dto, email);
    }

    @GetMapping("/by-assemblage/{assemblageId}")
    public List<SousAssemblageResponseDTO> getByAssemblage(@PathVariable Long assemblageId) {
        return sousAssemblageService.getSousAssemblagesByAssemblage(assemblageId);
    }

    @GetMapping("/{id}")
    public SousAssemblageResponseDTO getById(@PathVariable Long id) {
        return sousAssemblageService.getSousAssemblageById(id);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PutMapping("/{id}")
    public SousAssemblageResponseDTO update(@PathVariable Long id, @RequestBody SousAssemblageRequestDTO dto, @RequestParam String email) {
        return sousAssemblageService.updateSousAssemblage(id, dto, email);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, @RequestParam String email) {
        sousAssemblageService.deleteSousAssemblage(id, email);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PutMapping("/reorder/{assemblageId}")
    public void reorder(@PathVariable Long assemblageId, @RequestBody List<Long> orderedIds, @RequestParam String email) {
        sousAssemblageService.reorderSousAssemblages(assemblageId, orderedIds, email);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PutMapping("/{id}/archiver")
    public void archiver(@PathVariable Long id, @RequestParam String email) {
        sousAssemblageService.archiverSousAssemblage(id, email);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('CREATOR')")
    @PutMapping("/{id}/desarchiver")
    public void desarchiver(@PathVariable Long id, @RequestParam String email) {
        sousAssemblageService.desarchiverSousAssemblage(id, email);
    }

}
