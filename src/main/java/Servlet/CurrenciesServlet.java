package Servlet;

import DTO.CurrencyDTO;
import DTO.ErrorResponseDTO;
import Services.CurrencyService;
import exceptions.CurrencyAlreadyExistsException;
import exceptions.DatabaseNotAvailableException;
import exceptions.InvalidCurrencyCodeException;
import exceptions.InvalidInputException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            List<CurrencyDTO> currenciesDto = currencyService.getAll();
            resp.setContentType("application/json");
            writer.write(objectMapper.writeValueAsString(currenciesDto));
        } catch (DatabaseNotAvailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } finally {
            writer.close();
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String code = req.getParameter("code");
            String name = req.getParameter("name");
            String sign = req.getParameter("sign");
            resp.setContentType("application/json");
            CurrencyDTO currencyDTO = new CurrencyDTO(null, code, name, sign);
            CurrencyDTO savedCurrencyDTO = currencyService.save(currencyDTO);
            writer.write(objectMapper.writeValueAsString(savedCurrencyDTO));
        } catch (DatabaseNotAvailableException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } catch (InvalidInputException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } catch (CurrencyAlreadyExistsException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(HttpServletResponse.SC_CONFLICT, e.getMessage());
            writer.write(objectMapper.writeValueAsString(errorResponseDTO));
        } finally {
            writer.close();
        }
    }
}
