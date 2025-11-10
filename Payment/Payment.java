package payment;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {
    private final String paymentID;
    private final LocalDateTime paymentTime;

    private final String memberID;
    private final TicketProduct productPurchased;
    private final int price;
    private final String paymentMethod;

    public Payment(String memberID, TicketProduct productPurchased, int price, String paymentMethod) {
        this.paymentID = UUID.randomUUID().toString();
        this.paymentTime = LocalDateTime.now();

        this.memberID = memberID;
        this.productPurchased = productPurchased;
        this.price = price;
        this.paymentMethod = paymentMethod;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getTicketProduct() {
        return productPurchased.toString();
    }

    public int getPrice() {
        return price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentTime() {
        return paymentTime.toString();
    }

}
