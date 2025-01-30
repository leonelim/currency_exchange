package Servlet;

import DTO.ErrorResponseDTO;
import DTO.ExchangeRateDTO;
import Services.CurrencyExchangeService;
import Services.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.DatabaseNotAvailableException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final CurrencyExchangeService currencyExchangeService = CurrencyExchangeService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            List<ExchangeRateDTO> exchangeRates = currencyExchangeService.getAll();
            resp.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(exchangeRates));
        } catch (DatabaseNotAvailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String baseCurrencyCode = req.getParameter("baseCurrencyCode");
            String targetCurrencyCode = req.getParameter("targetCurrencyCode");
            String rate = req.getParameter("rate");

        } catch (DatabaseNotAvailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        }
    }
}
