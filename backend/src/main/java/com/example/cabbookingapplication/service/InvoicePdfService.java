package com.example.cabbookingapplication.service;

import com.example.cabbookingapplication.entity.Payment;
import com.example.cabbookingapplication.entity.Ride;
import com.example.cabbookingapplication.entity.User;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InvoicePdfService {

    public byte[] generateInvoice(Ride ride, Payment payment, User rider, String driverName) {

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

            Paragraph title = new Paragraph("CAB BOOKING INVOICE")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20)
                    .setBold();

            document.add(title);
            document.add(new Paragraph("\n"));

            Paragraph riderHeader = new Paragraph("Rider Details")
                    .setBold()
                    .setFontSize(14);

            document.add(riderHeader);

            Table riderTable = new Table(2);
            riderTable.setWidth(UnitValue.createPercentValue(100));

            riderTable.addCell(new Cell().add(new Paragraph("Name")));
            riderTable.addCell(new Cell().add(new Paragraph(rider.getName())));

            riderTable.addCell(new Cell().add(new Paragraph("Email")));
            riderTable.addCell(new Cell().add(new Paragraph(rider.getEmail())));

            document.add(riderTable);
            document.add(new Paragraph("\n"));

            Paragraph rideHeader = new Paragraph("Ride Details")
                    .setBold()
                    .setFontSize(14);

            document.add(rideHeader);

            Table rideTable = new Table(2);
            rideTable.setWidth(UnitValue.createPercentValue(100));

            rideTable.addCell("Ride ID");
            rideTable.addCell(ride.getId().toString());

            rideTable.addCell("Driver Name");
            rideTable.addCell(driverName != null ? driverName : "Not Assigned");

            rideTable.addCell("Pickup Location");
            rideTable.addCell(ride.getPickupLat() + ", " + ride.getPickupLng());

            rideTable.addCell("Drop Location");
            rideTable.addCell(ride.getDropLat() + ", " + ride.getDropLng());

            rideTable.addCell("Distance");
            rideTable.addCell(String.format("%.2f km", ride.getDistanceKm()));

            rideTable.addCell("Booking Time");
            rideTable.addCell(
                    ride.getBookedAt().format(formatter)
            );

            document.add(rideTable);
            document.add(new Paragraph("\n"));

            Paragraph paymentHeader = new Paragraph("Payment Details")
                    .setBold()
                    .setFontSize(14);

            document.add(paymentHeader);

            Table paymentTable = new Table(2);
            paymentTable.setWidth(UnitValue.createPercentValue(100));

            paymentTable.addCell("Amount Paid (₹)");
            paymentTable.addCell(String.format("%.2f", payment.getAmount()));

            paymentTable.addCell("Payment Status");
            paymentTable.addCell(payment.getStatus().name());

            paymentTable.addCell("Paid At");

            if (payment.getPaidAt() != null) {
                paymentTable.addCell(payment.getPaidAt().format(formatter));
            } else {
                paymentTable.addCell("-");
            }

            paymentTable.addCell("Stripe Payment ID");
            paymentTable.addCell(
                    payment.getStripePaymentIntentId() != null ?
                            payment.getStripePaymentIntentId() :
                            "-"
            );

            document.add(paymentTable);
            document.add(new Paragraph("\n"));

            Paragraph footer = new Paragraph("Thank you for riding with us!")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic();

            document.add(footer);

            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }
}
