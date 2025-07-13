package com.example.acwa.repositories;

import com.example.acwa.entities.LogEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
    Page<LogEntry> findAllByOrderByTimestampDesc(Pageable pageable);
}
