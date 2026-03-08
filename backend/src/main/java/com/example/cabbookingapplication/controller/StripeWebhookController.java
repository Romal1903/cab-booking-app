package com.example.cabbookingapplication.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.entity.User;
import com.example.cabbookingapplication.enums.PaymentStatus;
import com.example.cabbookingapplication.repository.PaymentRepository;
import com.example.cabbookingapplication.repository.DriverRepository;
import com.example.cabbookingapplication.repository.UserRepository;
import com.example.cabbookingapplication.service.InvoicePdfService;
import com.example.cabbookingapplication.service.EmailService;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final PaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final DriverRepository driverRepo;
    private final InvoicePdfService invoicePdfService;
    private final EmailService emailService;

    public StripeWebhookController(
            PaymentRepository paymentRepo,
            UserRepository userRepo,
            DriverRepository driverRepo,
            InvoicePdfService invoicePdfService,
            EmailService emailService
    ) {
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.driverRepo = driverRepo;
        this.invoicePdfService = invoicePdfService;
        this.emailService = emailService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) throws Exception {

        Event event = Webhook.constructEvent(
                payload, sigHeader, endpointSecret
        );

        if ("payment_intent.succeeded".equals(event.getType())) {

            PaymentIntent intent =
                    (PaymentIntent) event.getDataObjectDeserializer()
                            .getObject().orElseThrow();

            paymentRepo.findByStripePaymentIntentId(intent.getId())
                    .ifPresent(payment -> {

                        if (payment.getStatus() == PaymentStatus.PAID) {
                                return;
                        }

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

                        byte[] pdfBytes =
                                invoicePdfService.generateInvoice(
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
                    });
        }

        return ResponseEntity.ok("ok");
    }
}
