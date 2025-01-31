package DAO;

import Entity.Currency;
import exceptions.DatabaseNotAvailableException;
import utils.ConnectionPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO {
    private static final String GET_ALL_ROWS_SQL = "SELECT * FROM Currencies";
    private static final String GET_CURRENCY_BY_CODE_SQL = "SELECT * FROM Currencies WHERE Code = ?";
    private static final String GET_CURRENCY_BY_ID_SQL = "SELECT * FROM Currencies WHERE ID = ?";
    private static final String SAVE_CURRENCY_SQL = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";
    private static final CurrencyDAO INSTANCE = new CurrencyDAO();
    private CurrencyDAO() {}
    public static CurrencyDAO getInstance() {
        return INSTANCE;
    }
    public List<Currency> getAll() {
        List<Currency> currencies = new ArrayList<>();
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_ROWS_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                currencies.add(makeCurrency(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseNotAvailableException("The database could not be accessed");
        }
        return currencies;
    }
    public  Optional<Currency> get(String code) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CURRENCY_BY_CODE_SQL)) {
            preparedStatement.setString(1, code);
            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = makeCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new DatabaseNotAvailableException("The database could not be accessed");
        }
    }
    public Optional<Currency> get(Integer id) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_CURRENCY_BY_ID_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            Currency currency = null;
            if (resultSet.next()) {
                currency = makeCurrency(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new DatabaseNotAvailableException("The database could not be accessed");
        }
    }
    public Currency save(Currency currency) {
        try (Connection connection = ConnectionPool.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_CURRENCY_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                currency.setId(generatedKeys.getInt(1));
            }
            return currency;
        } catch (SQLException e) {
            throw new DatabaseNotAvailableException("The database could not be accessed");
        }
    }
    private Currency makeCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt("id"), resultSet.getString("code"), resultSet.getString("fullname"), resultSet.getString("sign"));
    }
}

