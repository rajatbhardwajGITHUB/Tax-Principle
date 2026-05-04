package com.example.Usermangement.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.Usermangement.Bean.PaymentOrder;
import com.example.Usermangement.Bean.ServiceItem;
import com.example.Usermangement.Bean.User;
import com.example.Usermangement.Enums.PaymentOrderStatus;
import com.example.Usermangement.Exceptions.InactiveServicePurchaseException;
import com.example.Usermangement.Exceptions.InvalidPaymentSignatureException;
import com.example.Usermangement.Exceptions.PaymentOrderNotFoundException;
import com.example.Usermangement.Exceptions.ServiceNotFoundException;
import com.example.Usermangement.Exceptions.UserNotFoundException;
import com.example.Usermangement.Model.PaymentOrderRequest;
import com.example.Usermangement.Model.PaymentOrderResponse;
import com.example.Usermangement.Model.PaymentVerificationRequest;
import com.example.Usermangement.Model.PurchaseResponse;
import com.example.Usermangement.Repository.PaymentOrderRepository;
import com.example.Usermangement.Repository.ServiceItemRepository;
import com.example.Usermangement.Repository.UserRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RazorpayPaymentGatewayService implements PaymentGatewayService {

    private static final String ORDER_URL = "https://api.razorpay.com/v1/orders";

    private final PaymentOrderRepository paymentOrderRepository;
    private final UserRepository userRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final PurchaseService purchaseService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${app.razorpay.key-id:}")
    private String razorpayKeyId;

    @Value("${app.razorpay.key-secret:}")
    private String razorpayKeySecret;

    @Value("${app.razorpay.currency:INR}")
    private String razorpayCurrency;

    public RazorpayPaymentGatewayService(
            PaymentOrderRepository paymentOrderRepository,
            UserRepository userRepository,
            ServiceItemRepository serviceItemRepository,
            PurchaseService purchaseService,
            ObjectMapper objectMapper) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.userRepository = userRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.purchaseService = purchaseService;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newHttpClient();
    }

    @Override
    public PaymentOrderResponse createOrder(String email, PaymentOrderRequest request) {
        ensureGatewayConfigured();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ServiceItem service = serviceItemRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException("Service not found"));

        if (!Boolean.TRUE.equals(service.getActive())) {
            throw new InactiveServicePurchaseException("Selected service is inactive");
        }

        long amountInPaise = toPaise(service.getPrice());
        String receipt = "svc-" + service.getId() + "-usr-" + user.getId() + "-" + System.currentTimeMillis();

        JsonNode payload = objectMapper.createObjectNode()
                .put("amount", amountInPaise)
                .put("currency", razorpayCurrency)
                .put("receipt", receipt)
                .put("payment_capture", 1);

        JsonNode orderNode = createRazorpayOrder(payload);
        String gatewayOrderId = orderNode.path("id").asText(null);
        if (gatewayOrderId == null || gatewayOrderId.isBlank()) {
            throw new IllegalStateException("Razorpay order id was not returned");
        }

        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setServiceItem(service);
        paymentOrder.setGatewayOrderId(gatewayOrderId);
        paymentOrder.setAmount(service.getPrice());
        paymentOrder.setCurrency(razorpayCurrency);
        paymentOrder.setStatus(PaymentOrderStatus.CREATED);
        paymentOrder.setReceipt(receipt);
        paymentOrderRepository.save(paymentOrder);

        PaymentOrderResponse response = new PaymentOrderResponse();
        response.setKeyId(razorpayKeyId);
        response.setOrderId(gatewayOrderId);
        response.setServiceId(service.getId());
        response.setServiceName(service.getName());
        response.setAmount(service.getPrice());
        response.setAmountInPaise(amountInPaise);
        response.setCurrency(razorpayCurrency);
        return response;
    }

    @Override
    public PurchaseResponse verifyPayment(String email, PaymentVerificationRequest request) {
        ensureGatewayConfigured();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        PaymentOrder paymentOrder = paymentOrderRepository
                .findByGatewayOrderIdAndUserId(request.getOrderId(), user.getId())
                .orElseThrow(() -> new PaymentOrderNotFoundException("Payment order not found"));

        if (!isSignatureValid(request.getOrderId(), request.getPaymentId(), request.getSignature())) {
            throw new InvalidPaymentSignatureException("Invalid payment signature");
        }

        paymentOrder.setGatewayPaymentId(request.getPaymentId());
        paymentOrder.setGatewaySignature(request.getSignature());
        paymentOrder.setStatus(PaymentOrderStatus.PAID);
        paymentOrderRepository.save(paymentOrder);

        return purchaseService.createPaidPurchase(email, paymentOrder.getServiceItem().getId(), request.getPaymentId());
    }

    private JsonNode createRazorpayOrder(JsonNode payload) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ORDER_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Basic " + basicAuth())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("Razorpay order creation failed: " + response.body());
            }
            return objectMapper.readTree(response.body());
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create Razorpay order", ex);
        }
    }

    private boolean isSignatureValid(String orderId, String paymentId, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(razorpayKeySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8));
            String expected = bytesToHex(digest);
            return MessageDigest.isEqual(
                    expected.getBytes(StandardCharsets.UTF_8),
                    signature.toLowerCase().getBytes(StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to verify payment signature", ex);
        }
    }

    private long toPaise(BigDecimal amount) {
        return amount.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    private String basicAuth() {
        String raw = razorpayKeyId + ":" + razorpayKeySecret;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    private void ensureGatewayConfigured() {
        if (razorpayKeyId == null || razorpayKeyId.isBlank() || razorpayKeySecret == null || razorpayKeySecret.isBlank()) {
            throw new IllegalStateException("Razorpay gateway credentials are not configured");
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder(bytes.length * 2);
        for (byte value : bytes) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
