package com.example.cabbookingapplication.service.impl;

import com.example.cabbookingapplication.dto.AdminRideResponseDto;
import com.example.cabbookingapplication.dto.AdminUserResponseDto;
import com.example.cabbookingapplication.entity.Payment;
import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.enums.Role;
import com.example.cabbookingapplication.repository.DriverRepository;
import com.example.cabbookingapplication.repository.RideRepository;
import com.example.cabbookingapplication.repository.UserRepository;
import com.example.cabbookingapplication.service.AdminService;
import com.example.cabbookingapplication.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final SupportTicketRepository supportTicketRepository;

    public AdminServiceImpl(
            UserRepository userRepository,
            RideRepository rideRepository,
            DriverRepository driverRepository,
            SupportTicketRepository supportTicketRepository
    ) {
        this.userRepository = userRepository;
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public List<AdminUserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUser)
                .toList();
    }

    @Override
    public List<AdminUserResponseDto> getDrivers() {
        return userRepository.findByRole(Role.DRIVER)
                .stream()
                .map(this::mapUser)
                .toList();
    }

    @Override
    public List<AdminRideResponseDto> getAllRides() {
        return rideRepository.findAllByOrderByBookedAtDesc()
                .stream()
                .map(this::mapRide)
                .toList();
    }

    @Override
    public Map<String, Long> getSystemStats() {

        long totalReports = supportTicketRepository.count();
        long openReports = supportTicketRepository.countByStatus("OPEN");

        return Map.of(
                "users", userRepository.count(),
                "rides", rideRepository.count(),
                "reports", totalReports,
                "openReports", openReports
        );
    }

    private AdminUserResponseDto mapUser(User u) {
        return new AdminUserResponseDto(
                u.getId().toString(),
                u.getName(),
                "",
                u.getEmail(),
                u.getRole().name(),
                u.isEnabled()
        );
    }

    private AdminRideResponseDto mapRide(Ride r) {

        String riderName = null;
        if (r.getRiderId() != null) {
            riderName = userRepository.findById(r.getRiderId())
                    .map(User::getName)
                    .orElse(null);
        }

        String driverName = null;
        if (r.getDriverId() != null) {
            driverName = driverRepository.findById(r.getDriverId())
                    .map(driver -> driver.getUser().getName())
                    .orElse(null);
        }

        Payment payment = r.getPayment();

        return new AdminRideResponseDto(
                r.getId().toString(),
                riderName,
                driverName,
                r.getStatus().name(),
                r.getFare(),
                payment != null ? payment.getStatus().name() : "NOT_PAID"
        );
    }

    @Override
    public void toggleUserStatus(String userId, boolean enabled) {

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Admin cannot be disabled");
        }

        if (user.getRole() != Role.DRIVER) {
            throw new RuntimeException("Only drivers can be disabled");
        }

        user.setEnabled(enabled);
        userRepository.save(user);
    }
}
