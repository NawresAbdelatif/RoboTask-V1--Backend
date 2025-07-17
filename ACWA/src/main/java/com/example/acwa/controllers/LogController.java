package com.example.acwa.controllers;
import com.example.acwa.entities.LogEntry;
import com.example.acwa.repositories.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.acwa.Dto.PageResult;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/logs")
@PreAuthorize("hasRole('ADMIN')")
public class LogController {

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    private LogEntryRepository logEntryRepository;

    @GetMapping
    public PageResult<LogEntry> getLogs(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "8") int size) {
        long start = System.currentTimeMillis();

        var pageResult = logEntryRepository.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
        PageResult<LogEntry> result = new PageResult<>(
                pageResult.getContent(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages(),
                pageResult.getNumber()
        );

        long duration = System.currentTimeMillis() - start;
        logger.info("[PERF] /api/logs?page={} size={} → {} ms", page, size, duration);

        return result;
    }

    @DeleteMapping("/clear")
    public void clearLogs() {
        logEntryRepository.deleteAll();
    }

}
