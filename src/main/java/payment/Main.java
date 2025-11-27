
package payment;

public class Main {
    public static void main(String[] args) {
        PriceManager priceManager = new PriceManager("config/price.json");

        int price = priceManager.getPrice(TicketProduct.DAILY_3H);
        System.out.println("3시간권 가격: " + price + "원");
    }
}
