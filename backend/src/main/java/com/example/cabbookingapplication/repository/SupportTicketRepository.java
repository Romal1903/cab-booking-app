package com.example.cabbookingapplication.repository;

import com.example.cabbookingapplication.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, UUID> {

    List<SupportTicket> findByUserId(UUID userId);

    long countByStatus(String status);
}
