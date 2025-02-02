package Servlet;

import DTO.ExchangeRateDTO;
import Services.CurrencyExchangeService;
import Services.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.DatabaseCouldNotBeAccessedException;
import exceptions.InvalidInputException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final CurrencyExchangeService currencyExchangeService = CurrencyExchangeService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            List<ExchangeRateDTO> exchangeRates = currencyExchangeService.getAll();
            resp.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(exchangeRates));
        } catch (DatabaseCouldNotBeAccessedException e) {
            ErrorWriter.write(resp, writer, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, objectMapper);
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rate = req.getParameter("rate");
            resp.setContentType("application/json");
            ExchangeRateDTO exchangeRateDTO = currencyExchangeService.save(baseCurrencyCode, targetCurrencyCode, rate);
            writer.write(objectMapper.writeValueAsString(exchangeRateDTO));
        } catch (DatabaseCouldNotBeAccessedException e) {
            ErrorWriter.write(resp, writer, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, objectMapper);
        } catch (InvalidInputException e) {
            ErrorWriter.write(resp, writer, HttpServletResponse.SC_BAD_REQUEST, e, objectMapper);
        }
    }
}
