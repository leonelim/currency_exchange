package Servlet;

import DTO.CurrencyDTO;
import DTO.ErrorResponseDTO;
import Services.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.CurrencyDoesntExistException;
import exceptions.DatabaseNotAvailableException;
import exceptions.InvalidCurrencyCodeException;
import exceptions.InvalidInputException;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import java.io.*;
import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        String code = req.getPathInfo().substring(1);
        try {
            CurrencyDTO currencyDTO = currencyService.get(code);
            resp.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(currencyDTO));
        } catch (DatabaseNotAvailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } catch (CurrencyDoesntExistException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } catch (InvalidInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } finally {
            writer.close();
        }
    }
}
