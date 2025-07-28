package com.example.acwa.repositories;


import com.example.acwa.entities.SousAssemblage;
import com.example.acwa.entities.Assemblage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SousAssemblageRepository extends JpaRepository<SousAssemblage, Long> {
    List<SousAssemblage> findByAssemblage(Assemblage assemblage);

    List<SousAssemblage> findByAssemblage_Id(Long assemblageId);
}
