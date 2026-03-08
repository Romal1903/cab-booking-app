package com.example.cabbookingapplication.controller;

import com.example.cabbookingapplication.dto.FareEstimateDTO;
import com.example.cabbookingapplication.dto.RideResponseDTO;
import com.example.cabbookingapplication.entity.Driver;
import com.example.cabbookingapplication.entity.Payment;
import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.enums.PaymentStatus;
import com.example.cabbookingapplication.enums.RideStatus;
import com.example.cabbookingapplication.enums.Role;
import com.example.cabbookingapplication.repository.DriverRepository;
import com.example.cabbookingapplication.repository.PaymentRepository;
import com.example.cabbookingapplication.repository.RideRepository;
import com.example.cabbookingapplication.repository.UserRepository;
import com.example.cabbookingapplication.service.RideFareService;
import com.example.cabbookingapplication.service.StripeService;
import com.example.cabbookingapplication.enums.RideEventType;
import com.stripe.model.PaymentIntent;

import jakarta.transaction.Transactional;

import com.example.cabbookingapplication.service.EmailService;
import com.example.cabbookingapplication.service.InvoicePdfService;
import com.example.cabbookingapplication.service.RideEventService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/rides")
public class RideController {

    private final RideRepository rideRepo;
    private final UserRepository userRepo;
    private final RideFareService fareService;
    private final PaymentRepository paymentRepo;
    private final DriverRepository driverRepo;
    private final RideEventService rideEventService;
    private final StripeService stripeService;
    private final InvoicePdfService invoicePdfService;
    private final EmailService emailService;

    public RideController(
            RideRepository rideRepo,
            UserRepository userRepo,
            RideFareService fareService,
            PaymentRepository paymentRepo,
            DriverRepository driverRepo,
            RideEventService rideEventService,
            StripeService stripeService,
            InvoicePdfService invoicePdfService,
            EmailService emailService
    ) {
        this.rideRepo = rideRepo;
        this.userRepo = userRepo;
        this.fareService = fareService;
        this.paymentRepo = paymentRepo;
        this.driverRepo = driverRepo;
        this.rideEventService = rideEventService;
        this.stripeService = stripeService;
        this.invoicePdfService = invoicePdfService;
        this.emailService = emailService;
    }

    @PostMapping("/book")
    public Ride bookRide(@RequestBody Ride ride) {

        User rider = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (rider.getRole() != Role.RIDER) {
            throw new RuntimeException("Only riders can book rides");
        }

        double distance = fareService.calculateDistance(
                ride.getPickupLat(),
                ride.getPickupLng(),
                ride.getDropLat(),
                ride.getDropLng()
        );

        ride.setRiderId(rider.getId());
        ride.setDistanceKm(distance);
        ride.setFare(fareService.calculateFare(distance));
        ride.setStatus(RideStatus.REQUESTED);
        ride.setBookedAt(LocalDateTime.now());

        Ride savedRide = rideRepo.save(ride);

        rideEventService.notifyDrivers(
                ride,
                RideEventType.RIDE_REQUESTED,
                "New ride request available"
        );

        Payment payment = new Payment();
        payment.setRide(savedRide);
        payment.setRiderId(rider.getId());
        payment.setAmount(savedRide.getFare());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);

        return savedRide;
    }

    @GetMapping("/estimate")
    public FareEstimateDTO estimateFare(
            @RequestParam double pickupLat,
            @RequestParam double pickupLng,
            @RequestParam double dropLat,
            @RequestParam double dropLng
    ) {
        double distance = fareService.calculateDistance(
                pickupLat, pickupLng, dropLat, dropLng
        );
        return new FareEstimateDTO(distance, fareService.calculateFare(distance));
    }

    @GetMapping("/rider/history")
    public List<RideResponseDTO> riderHistory() {

        System.out.println("RIDE HISTORY API HIT");
        
        User rider = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return rideRepo.findByRiderIdOrderByBookedAtDesc(rider.getId())
                .stream()
                .map(this::mapRide)
                .toList();
    }

    @PostMapping("/cancel/{id}")
    public Ride cancelRide(@PathVariable UUID id) {

        Ride ride = rideRepo.findById(id).orElseThrow();
        ride.setStatus(RideStatus.CANCELLED);

        rideEventService.notifyRider(
                ride,
                RideEventType.RIDE_CANCELLED,
                "Ride has been cancelled"
        );

        rideEventService.notifyDriver(
                ride,
                RideEventType.RIDE_CANCELLED,
                "Ride was cancelled by rider"
        );

        return rideRepo.save(ride);
    }

    @GetMapping("/available")
    public List<RideResponseDTO> availableRides() {

        return rideRepo.findByStatusOrderByBookedAtDesc(RideStatus.REQUESTED)
                .stream()
                .map(this::mapRide)
                .toList();
    }

    @PostMapping("/accept/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public Ride acceptRide(@PathVariable UUID id) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        driverRepo.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        Ride ride = rideRepo.findById(id).orElseThrow();

        Driver driver = driverRepo.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Driver profile not found"));

        ride.setDriverId(driver.getId());
        ride.setStatus(RideStatus.ACCEPTED);

        rideEventService.notifyRider(
                ride,
                RideEventType.RIDE_ACCEPTED,
                "Driver has accepted your ride"
        );

        rideEventService.notifyDriver(
                ride,
                RideEventType.RIDE_ACCEPTED,
                "You have accepted the ride"
        );

        return rideRepo.save(ride);
    }

    @PostMapping("/start/{id}")
    public Ride startRide(@PathVariable UUID id) {

        Ride ride = rideRepo.findById(id).orElseThrow();
        ride.setStatus(RideStatus.STARTED);

        rideEventService.notifyRider(
                ride,
                RideEventType.RIDE_STARTED,
                "Ride has started"
        );

        return rideRepo.save(ride);
    }

    @PostMapping("/complete/{id}")
    public Ride completeRide(@PathVariable UUID id) {

        Ride ride = rideRepo.findById(id).orElseThrow();
        ride.setStatus(RideStatus.COMPLETED);

        rideEventService.notifyRider(
                ride,
                RideEventType.RIDE_COMPLETED,
                "Ride completed successfully"
        );

        return rideRepo.save(ride);
    }

    @GetMapping("/driver/active")
    public List<RideResponseDTO> driverActive() {

        Driver driver = getCurrentDriver();

        return rideRepo.findByDriverIdAndStatusInOrderByBookedAtDesc(
                driver.getId(),
                List.of(RideStatus.ACCEPTED, RideStatus.STARTED)
        ).stream().map(this::mapRide).toList();
    }

    @GetMapping("/driver/completed")
    public List<RideResponseDTO> driverCompleted() {

        Driver driver = getCurrentDriver();

        return rideRepo.findByDriverIdAndStatusOrderByBookedAtDesc(
                driver.getId(),
                RideStatus.COMPLETED
        ).stream().map(this::mapRide).toList();
    }

    @GetMapping("/driver/cancelled")
    public List<RideResponseDTO> driverCancelled() {

        Driver driver = getCurrentDriver();

        return rideRepo.findByDriverIdAndStatusOrderByBookedAtDesc(
                driver.getId(),
                RideStatus.CANCELLED
        ).stream().map(this::mapRide).toList();
    }

    @PostMapping("/pay/{id}")
    @PreAuthorize("hasRole('RIDER')")
    public Map<String, String> markPaymentDone(@PathVariable UUID id) throws Exception {
        try {
            User rider = (User) SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();

            Ride ride = rideRepo.findById(id).orElseThrow();

            if (!ride.getRiderId().equals(rider.getId())) {
                throw new RuntimeException("Not your ride");
            }

            if (ride.getStatus() != RideStatus.COMPLETED) {
                throw new RuntimeException("Ride not completed");
            }

            Payment payment = paymentRepo.findByRide_Id(id)
                    .orElseThrow();

            Double fare = ride.getFare();
            if (fare == null) {
                throw new RuntimeException("Fare not calculated for this ride");
            }

            PaymentIntent intent =
                    stripeService.createPaymentIntent(fare);

            payment.setStripePaymentIntentId(intent.getId());
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepo.save(payment);

            return Map.of("clientSecret", intent.getClientSecret());
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        
    }

    @PostMapping("/pay/confirm")
    @PreAuthorize("hasRole('RIDER')")
    @Transactional
    public void confirmPayment(@RequestBody Map<String, String> body) {

        String paymentIntentId = body.get("paymentIntentId");

        Payment payment = paymentRepo
                .findByStripePaymentIntentId(paymentIntentId)
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.PAID) return;

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaidAt(LocalDateTime.now());
        paymentRepo.save(payment);

        Ride ride = payment.getRide();

        User rider = userRepo.findById(payment.getRiderId())
                .orElseThrow();

        String driverName = null;

        if (ride.getDriverId() != null) {
        driverName = driverRepo.findById(ride.getDriverId())
                .map(d -> d.getUser().getName())
                .orElse("Unknown Driver");
        }

        byte[] pdfBytes = invoicePdfService.generateInvoice(
                ride,
                payment,
                rider,
                driverName
        );

        emailService.sendInvoiceEmail(
                rider.getEmail(),
                rider.getName(),
                pdfBytes
        );

        rideEventService.notifyRider(
                ride,
                RideEventType.PAYMENT_COMPLETED,
                "Payment completed successfully"
        );
    }

    @GetMapping("/invoice/{rideId}")
        @PreAuthorize("hasRole('RIDER')")
        public ResponseEntity<byte[]> downloadInvoice(@PathVariable UUID rideId) {

        User rider = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Ride ride = rideRepo.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (!ride.getRiderId().equals(rider.getId())) {
                throw new RuntimeException("Not your ride");
        }

        Payment payment = paymentRepo.findByRide_Id(rideId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.PAID) {
                throw new RuntimeException("Invoice available only after payment");
        }

        String driverName = null;

        if (ride.getDriverId() != null) {
        driverName = driverRepo.findById(ride.getDriverId())
                .map(d -> d.getUser().getName())
                .orElse("Unknown Driver");
        }

        byte[] pdfBytes = invoicePdfService.generateInvoice(
                ride,
                payment,
                rider,
                driverName
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice-" + rideId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
        }

    private Driver getCurrentDriver() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return driverRepo.findByUser(user)
                .orElseThrow(() ->
                    new RuntimeException("Driver profile not found for user " + user.getEmail())
                );
    }

    private RideResponseDTO mapRide(Ride ride) {

        String riderName = ride.getRiderId() == null ? null :
                userRepo.findById(ride.getRiderId())
                        .map(User::getName)
                        .orElse(null);

        String driverName = null;

        if (ride.getDriverId() != null) {
            driverName = driverRepo.findById(ride.getDriverId())
                    .map(driver -> driver.getUser().getName())
                    .orElse(null);
        }

        PaymentStatus paymentStatus =
                paymentRepo.findByRide_Id(ride.getId())
                        .map(Payment::getStatus)
                        .orElse(PaymentStatus.PENDING);

        return new RideResponseDTO(
                ride.getId(),
                ride.getPickupLat(),
                ride.getPickupLng(),
                ride.getDropLat(),
                ride.getDropLng(),
                ride.getDistanceKm(),
                ride.getFare(),
                ride.getStatus().name(),
                paymentStatus.name(),
                riderName,
                driverName
        );
    }
}
