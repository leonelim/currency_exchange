package Servlet;

import DTO.ErrorResponseDTO;
import DTO.ExchangeRateDTO;
import Services.CurrencyExchangeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.DatabaseNotAvailableException;
import exceptions.ExchangeRateDoesntExistsException;
import exceptions.InvalidInputException;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet implements errorWriter{
    CurrencyExchangeService currencyExchangeService = CurrencyExchangeService.getInstance();
    ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        String pathInfo = req.getPathInfo();
        String baseCurrencyCode = pathInfo.substring(1, 4);
        String targetCurrencyCode = pathInfo.substring(4);
        try {
            ExchangeRateDTO exchangeRateDTO = currencyExchangeService.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            resp.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(exchangeRateDTO));
        } catch (DatabaseNotAvailableException e) {
            writeError(resp, writer, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, objectMapper);
        } catch (InvalidInputException e) {
            writeError(resp, writer, HttpServletResponse.SC_BAD_REQUEST, e, objectMapper);
        } catch (ExchangeRateDoesntExistsException e) {
            writeError(resp, writer, HttpServletResponse.SC_NOT_FOUND, e, objectMapper);
        }

    }

    @Override
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        try {
            String path = req.getPathInfo();
            String baseCurrencyCode = path.substring(1, 4);
            String targetCurrencyCode = path.substring(4);
            String rate = req.getParameter("rate");
            ExchangeRateDTO exchangeRate = currencyExchangeService.update(baseCurrencyCode, targetCurrencyCode, rate);
            writer.write(objectMapper.writeValueAsString(exchangeRate));
        } catch (InvalidInputException e) {
            writeError(resp, writer, HttpServletResponse.SC_BAD_REQUEST, e, objectMapper);
        } catch (DatabaseNotAvailableException e) {
            writeError(resp, writer, HttpServletResponse.SC_NOT_FOUND, e, objectMapper);
        }
    }
}
