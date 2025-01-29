package validation;

import Entity.ExchangeRate;

public class ExchangeRateValidator {
    public boolean isValidCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return baseCurrencyCode.length() == 3 && targetCurrencyCode.length() == 3;
    }
}
