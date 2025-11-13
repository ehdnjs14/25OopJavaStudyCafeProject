package payment;

import Seat.UsageSession;
public interface ILogManager {
    void savePaymentLog(Payment payment);
    void saveUsageLog(UsageSession session);
}
