import DAO.CurrencyDAO;
import DAO.CurrencyExchangeDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import utils.ConnectionPool;
import validation.ExchangeRateValidator;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeRateDAOTest {
    @Test
    public void test() throws SQLException, JsonProcessingException {
        System.out.println(BigDecimal.ONE.divide(new BigDecimal(100), 6, RoundingMode.HALF_UP));
    }
}
