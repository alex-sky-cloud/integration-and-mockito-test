package boot.junit5.service;

import java.math.BigDecimal;

public class PricingService {

    private ProductVerifier productVerifier;
    private PriceReporter priceReporter;

    public PricingService(ProductVerifier productVerifier) {
        this.productVerifier = productVerifier;
    }

    public PricingService(ProductVerifier productVerifier, PriceReporter priceReporter) {
        this.productVerifier = productVerifier;
        this.priceReporter = priceReporter;
    }

    public BigDecimal calculatePrice(String productName) {

        if (productVerifier.isCurrentlyInStockOfCompetitor(productName)) {
            return new BigDecimal("99.99");
        }

        return new BigDecimal("149.99");
    }

    public BigDecimal calculatePriceCheap(String productName) {

        if (productVerifier.isCurrentlyInStockOfCompetitor(productName)) {

            priceReporter.notify(productName);

            return new BigDecimal("99.99");
        }

        return new BigDecimal("149.99");
    }
}
