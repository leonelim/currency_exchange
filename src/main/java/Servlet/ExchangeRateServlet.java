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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet implements errorWriter {
    CurrencyExchangeService currencyExchangeService = CurrencyExchangeService.getInstance();
    ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            this.doPatch(req, resp);
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
            BufferedReader reader = req.getReader();
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            String rate = stringBuilder.toString().substring(5);
            reader.close();
            String path = req.getPathInfo();
            String baseCurrencyCode = path.substring(1, 4);
            String targetCurrencyCode = path.substring(4);
            ExchangeRateDTO exchangeRate = currencyExchangeService.update(baseCurrencyCode, targetCurrencyCode, rate);
            writer.write(objectMapper.writeValueAsString(exchangeRate));
        } catch (
                InvalidInputException e) {
            writeError(resp, writer, HttpServletResponse.SC_BAD_REQUEST, e, objectMapper);
        } catch (
                DatabaseNotAvailableException e) {
            writeError(resp, writer, HttpServletResponse.SC_NOT_FOUND, e, objectMapper);
        }
    }
}
