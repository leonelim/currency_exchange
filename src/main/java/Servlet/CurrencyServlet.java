package Servlet;

import DTO.CurrencyDTO;
import Services.CurrencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.CurrencyDoesntExistException;
import exceptions.DatabaseCouldNotBeAccessedException;
import exceptions.InvalidInputException;
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
        } catch (DatabaseCouldNotBeAccessedException e) {
            ErrorWriter.write(resp, writer, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e, objectMapper);
        } catch (CurrencyDoesntExistException e) {
            ErrorWriter.write(resp, writer, HttpServletResponse.SC_NOT_FOUND, e, objectMapper);
        } catch (InvalidInputException e) {
            ErrorWriter.write(resp, writer, HttpServletResponse.SC_BAD_REQUEST, e, objectMapper);
        } finally {
            writer.close();
        }
    }
}
