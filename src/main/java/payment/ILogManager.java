package payment;

public interface ILogManager {
    void savePaymentLog(Payment payment);
    void saveUsageLog(UsageSession session);
}
