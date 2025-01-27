package validation;

import java.util.regex.Pattern;
public class CurrencyValidator {
    private static final Pattern VALID_PATTERN = Pattern.compile("[a-zA-z]+");
    private static final int MIN_NAME_LEN = 3;
    private static final int MAX_NAME_LEN = 30;
    private static final int MAX_SIGN_LEN = 3;
    private static final int CODE_LEN = 3;
    private static final CurrencyValidator INSTANCE = new CurrencyValidator();
    private CurrencyValidator() {}
    public static CurrencyValidator getInstance() {
        return INSTANCE;
    }

    public boolean isValidCode(String code) {
        return code != null && VALID_PATTERN.matcher(code).matches() && code.length() == CODE_LEN;
    }
    public boolean isValidName(String name) {
        return name != null && VALID_PATTERN.matcher(name).matches() && name.length() > MIN_NAME_LEN && name.length() < MAX_NAME_LEN;
    }
    public boolean isValidSign(String sign) {
        return sign != null && sign.length() < MAX_SIGN_LEN && !sign.isEmpty();
    }
}
