package DAO;

import Entity.Currency;
import Entity.ExchangeRate;
import utils.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyExchangeDAO {
    private CurrencyExchangeDAO() {}
    private static final CurrencyExchangeDAO INSTANCE = new CurrencyExchangeDAO();
    public static CurrencyExchangeDAO getInstance() {
        return INSTANCE;
    }
    private static final String GET_ALL_SQL = """
            SELECT ER.ID AS ID, ER.Rate AS RATE,
            bc.ID AS BaseCurrencyId, bc.Code AS BaseCurrencyCode, bc.FullName AS BaseCurrencyFullName, bc.Sign AS BaseCurrencySign,
            tc.ID AS TargetCurrencyId, tc.Code AS TargetCurrencyCode, tc.FullName AS TargetCurrencyFullName, tc.Sign AS TargetCurrencySign
            FROM ExchangeRates ER
            INNER JOIN Currencies bc ON ER.BaseCurrencyId = bc.ID
            INNER JOIN Currencies tc ON ER.TargetCurrencyId = tc.ID
            """;
    public List<ExchangeRate> getAll() {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                exchangeRates.add(makeExchangeRate(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException();
        }
        return exchangeRates;
    }
    private ExchangeRate makeExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(resultSet.getInt("id"),
                new Currency(resultSet.getInt("BaseCurrencyId"), resultSet.getString("BaseCurrencyCode"), resultSet.getString("BaseCurrencyFullName"), resultSet.getString("BaseCurrencySign")),
                new Currency(resultSet.getInt("TargetCurrencyId"), resultSet.getString("TargetCurrencyCode"), resultSet.getString("TargetCurrencyFullName"), resultSet.getString("TargetCurrencySign")),
                resultSet.getBigDecimal("rate"));
    }
}
