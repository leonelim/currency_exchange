package Servlet;

import DTO.ErrorResponseDTO;
import DTO.ExchangeResultDTO;
import Services.CurrencyExchangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.DatabaseNotAvailableException;
import exceptions.NoExchangeRateFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet implements errorWriter {
    CurrencyExchangeService currencyExchangeService = CurrencyExchangeService.getInstance();
    ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String baseCurrencyCode = req.getParameter("from");
            String targetCurrencyCode = req.getParameter("to");
            String amount = req.getParameter("amount");
            resp.setContentType("application/json");
            ExchangeResultDTO exchangeResultDTO = currencyExchangeService.currencyExchange(baseCurrencyCode, targetCurrencyCode, amount);
            writer.write(objectMapper.writeValueAsString(exchangeResultDTO))    ;
        } catch (DatabaseNotAvailableException e) {
            writeError(resp, writer, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, objectMapper);
        } catch (NoExchangeRateFoundException e) {
            writeError(resp, writer, HttpServletResponse.SC_NOT_FOUND, e, objectMapper);
        }
    }
}
