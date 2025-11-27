
package payment;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    private final String paymentID;          // UUID
    private final LocalDateTime paymentTime; // 결제 발생 시간

    private final String memberID;           // 회원 ID
    private final TicketProduct productPurchased;  // 구매한 상품 객체
    private final int price;                 // 결제 금액
    private final String paymentMethod;      // 결제 방식 (카드/현금 등)

    public Payment(String memberID, TicketProduct productPurchased, int price, String paymentMethod) {
        this.paymentID = UUID.randomUUID().toString();
        this.paymentTime = LocalDateTime.now();

        this.memberID = memberID;
        this.productPurchased = productPurchased;
        this.price = price;
        this.paymentMethod = paymentMethod;
    }

    // ====================== Getter ======================

    public String getPaymentID() {
        return paymentID;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public String getMemberID() {
        return memberID;
    }

    /** TicketProduct 전체 객체를 반환해야 JSON 직렬화가 됨 */
    public TicketProduct getProductPurchased() {
        return productPurchased;
    }

    public int getPrice() {
        return price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
