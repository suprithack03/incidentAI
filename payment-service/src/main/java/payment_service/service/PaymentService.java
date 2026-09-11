package payment_service.service;

import payment_service.entity.Payment;
import payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePayment(Long id, Payment updatedPayment) {
        Payment existingPayment = getPaymentById(id);

        existingPayment.setUserId(updatedPayment.getUserId());
        existingPayment.setAmount(updatedPayment.getAmount());
        existingPayment.setStatus(updatedPayment.getStatus());

        return paymentRepository.save(existingPayment);
    }

    public void deletePayment(Long id) {
        Payment existingPayment = getPaymentById(id);
        paymentRepository.delete(existingPayment);
    }
}