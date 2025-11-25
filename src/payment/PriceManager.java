package payment;

import java.util.HashMap;
import java.util.Map;
// JSON 파일에서 불러온 ‘이용권 가격표’를 들고 있다가 요청하면 그 상품의 가격을 알려주는 클래스
public class PriceManager {
    private final Map<TicketProduct, Integer> priceList;

    public PriceManager(String configFilePath) {
        PriceDataLoader loader = new PriceDataLoader();
        this.priceList = loader.loadPricesFromJson(configFilePath);
    }

    public int getPrice(TicketProduct product) {
        return priceList.getOrDefault(product, 0);
    }
}
