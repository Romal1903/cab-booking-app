package com.example.cabbookingapplication.controller;

import com.example.cabbookingapplication.dto.SupportTicketAdminDto;
import com.example.cabbookingapplication.entity.SupportTicket;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.service.SupportTicketService;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/support")
public class SupportTicketController {

    private final SupportTicketService service;

    public SupportTicketController(SupportTicketService service) {
        this.service = service;
    }

    @PostMapping
    public SupportTicket createTicket(@RequestBody SupportTicket ticket) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        ticket.setUserId(user.getId());
        ticket.setRole(user.getRole().name());
        ticket.setCreatedAt(LocalDateTime.now());

        if (ticket.getStatus() == null) {
            ticket.setStatus("OPEN");
        }

        return service.create(ticket);
    }

    @GetMapping("/my")
    public List<SupportTicket> myTickets() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return service.getUserTickets(user.getId());
    }

    @GetMapping("/admin")
    public List<SupportTicketAdminDto> allTickets() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public SupportTicket updateStatus(
            @PathVariable UUID id,
            @RequestParam String status
    ) {

        return service.updateStatus(id, status);
    }
}
