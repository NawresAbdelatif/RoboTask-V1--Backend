package com.example.acwa.repositories;

import com.example.acwa.entities.Outil;
import com.example.acwa.entities.Piece;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OutilRepository extends JpaRepository<Outil, Long> {
    boolean existsByReference(String reference);

    Page<Outil> findByReferenceIgnoreCaseContainingOrDesignationIgnoreCaseContaining(
            String reference, String designation, Pageable pageable
    );

    @Query("SELECT SUM(o.quantite) FROM Outil o")
    Integer sumQuantite();

}
