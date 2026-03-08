package com.example.cabbookingapplication.service;

import com.example.cabbookingapplication.dto.SupportTicketAdminDto;
import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.entity.SupportTicket;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.repository.DriverRepository;
import com.example.cabbookingapplication.repository.RideRepository;
import com.example.cabbookingapplication.repository.SupportTicketRepository;
import com.example.cabbookingapplication.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SupportTicketService {

    private SupportTicketRepository repository;
    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;

    public SupportTicketService(
            SupportTicketRepository repository,
            UserRepository userRepository,
            RideRepository rideRepository,
            DriverRepository driverRepository
    ) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
    }

    public SupportTicket create(SupportTicket ticket) {
        return repository.save(ticket);
    }

    public List<SupportTicket> getUserTickets(UUID userId) {
        return repository.findByUserId(userId);
    }

    public List<SupportTicketAdminDto> getAll() {

        return repository.findAll()
                .stream()
                .map(ticket -> {

                    String userName = userRepository.findById(ticket.getUserId())
                            .map(User::getName)
                            .orElse("Unknown");

                    String driverName = null;

                    if (ticket.getRideId() != null) {

                        Ride ride = rideRepository.findById(ticket.getRideId()).orElse(null);

                        if (ride != null && ride.getDriverId() != null) {
                            driverName = driverRepository.findById(ride.getDriverId())
                                    .map(d -> d.getUser().getName())
                                    .orElse(null);
                        }
                    }

                    return new SupportTicketAdminDto(
                            ticket.getId(),
                            ticket.getSubject(),
                            ticket.getMessage(),
                            ticket.getStatus(),
                            ticket.getRole(),
                            userName,
                            driverName,
                            ticket.getRideId(),
                            ticket.getCreatedAt()
                    );

                }).toList();
    }

    public SupportTicket updateStatus(UUID id, String status) {

        SupportTicket ticket = repository.findById(id)
                .orElseThrow();

        ticket.setStatus(status);

        return repository.save(ticket);
    }
}
