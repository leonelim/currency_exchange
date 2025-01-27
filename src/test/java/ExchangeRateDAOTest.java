import DAO.CurrencyDAO;
import DAO.CurrencyExchangeDAO;
import org.junit.Test;
import utils.ConnectionPool;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeRateDAOTest {
    @Test
    public void test() throws SQLException {
        CurrencyExchangeDAO currencyExchangeDAO = CurrencyExchangeDAO.getInstance();
        currencyExchangeDAO.getAll();
    }
}
