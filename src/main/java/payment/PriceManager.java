package payment;

import java.util.HashMap;
import java.util.Map;

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
