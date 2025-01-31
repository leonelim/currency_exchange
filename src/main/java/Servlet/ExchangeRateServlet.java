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
public class ExchangeRateServlet extends HttpServlet {
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
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } catch (InvalidInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } catch (ExchangeRateDoesntExistsException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        }

    }

    @Override
    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        resp.setContentType("application/json");
        writer.write("111");
        try {
            String path = req.getPathInfo();
            String baseCurrencyCode = path.substring(1, 4);
            String targetCurrencyCode = path.substring(4);
            String rate = req.getParameter("rate");
            boolean isUpdated = currencyExchangeService.update(baseCurrencyCode, targetCurrencyCode, rate);
            writer.write(objectMapper.writeValueAsString(isUpdated));
        } catch (InvalidInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } catch (DatabaseNotAvailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        }
    }
}
