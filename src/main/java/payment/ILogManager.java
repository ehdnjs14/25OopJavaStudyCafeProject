package payment;

import Seat.UsageSession;
public interface ILogManager {
    void savePaymentLog(Payment payment);
    void saveUsageLog(UsageSession session);

    void saveOrderLog(String logEntry); // 11/23 주문 로그 메서드
}
