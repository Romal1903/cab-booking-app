package com.example.cabbookingapplication.service;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    public PaymentIntent createPaymentIntent(Double amountInr) throws Exception {

        if (amountInr == null) {
            throw new RuntimeException("Fare is null");
        }

        if (amountInr < 30) {
            throw new RuntimeException("Minimum payable amount is ₹30");
        }

        long amountInPaise = Math.round(amountInr * 100);

        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInPaise)
                        .setCurrency("inr")
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams
                                        .AutomaticPaymentMethods
                                        .builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build();

        return PaymentIntent.create(params);
    }
}
