package Servlet;

import DTO.ErrorResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

public interface errorWriter {
   default void writeError(HttpServletResponse response, PrintWriter writer, int statusCode, RuntimeException exception, ObjectMapper objectMapper) throws JsonProcessingException {
        response.setStatus(statusCode);
        ErrorResponseDTO message = new ErrorResponseDTO(exception.getMessage());
        writer.write(objectMapper.writeValueAsString(message));
    }
}
