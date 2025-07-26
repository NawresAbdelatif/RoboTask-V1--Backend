package com.example.acwa.repositories;

import com.example.acwa.entities.Piece;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PieceRepository extends JpaRepository<Piece, Long> {
    boolean existsByReference(String reference);

    Page<Piece> findByReferenceIgnoreCaseContainingOrDesignationIgnoreCaseContaining(
            String reference, String designation, Pageable pageable
    );

    @Query("SELECT SUM(p.quantite) FROM Piece p")
    Integer sumQuantite();
}

