package validation;

import Entity.ExchangeRate;
import java.util.regex.Pattern;
import java.math.BigDecimal;

public class ExchangeRateValidator {
    private final Pattern VALID_RATE_AND_AMOUNT_PATTEN = Pattern.compile("-?(\\d*\\.\\d+|\\d+\\.?\\d*)");
    private final Pattern VALID_CODE_PATTERN = Pattern.compile("\\b[a-zA-Z]+\\b");
    public boolean isValidCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return baseCurrencyCode.length() == 3 && targetCurrencyCode.length() == 3 &&
                VALID_CODE_PATTERN.matcher(baseCurrencyCode).matches() && VALID_CODE_PATTERN.matcher(targetCurrencyCode).matches()
                && !baseCurrencyCode.equals(targetCurrencyCode);
    }
    public boolean isValidRate(String rate) {
        return VALID_RATE_AND_AMOUNT_PATTEN.matcher(rate).matches() && !rate.isEmpty();
    }
    public boolean isValidAmount(String amount) {
        return VALID_RATE_AND_AMOUNT_PATTEN.matcher(amount).matches() && !amount.isEmpty();
    }
}
