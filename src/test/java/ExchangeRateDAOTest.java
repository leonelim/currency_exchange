import DAO.CurrencyDAO;
import DAO.CurrencyExchangeDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import utils.ConnectionPool;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeRateDAOTest {
    @Test
    public void test() throws SQLException, JsonProcessingException {
        CurrencyExchangeDAO currencyExchangeDAO = CurrencyExchangeDAO.getInstance();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(currencyExchangeDAO.getAll()));
    }
}
