package Entity;

import java.math.BigDecimal;

public class ExchangeResult {
    Currency baseCurrency;
    Currency targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;
}
