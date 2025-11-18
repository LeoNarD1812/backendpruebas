package pe.edu.upeu.sysasistencia.excepciones;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Importar AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse> handleAllExceptions(Exception ex, WebRequest request) {
        CustomResponse err = new CustomResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ModelNotFoundException.class)
    public ResponseEntity<CustomResponse> handleModelNotFoundException(ModelNotFoundException ex, WebRequest request) {
        CustomResponse err = new CustomResponse(
                HttpStatus.NOT_FOUND.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(err, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<CustomResponse> handleArithmeticException(ArithmeticException ex, WebRequest request) {
        CustomResponse err = new CustomResponse(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                ex.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }

    // Manejador específico para AccessDeniedException
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<CustomResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        CustomResponse err = new CustomResponse(
                HttpStatus.FORBIDDEN.value(),
                LocalDateTime.now(),
                "Acceso Denegado: No tienes permiso para realizar esta acción.",
                request.getDescription(false)
        );
        return new ResponseEntity<>(err, HttpStatus.FORBIDDEN);
    }

    // COMENTADO TEMPORALMENTE PARA DEPURAR EL ERROR 400 DE ACCESO DENEGADO
    // @ExceptionHandler(RuntimeException.class)
    // public ResponseEntity<CustomResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
    //     CustomResponse err = new CustomResponse(
    //             HttpStatus.BAD_REQUEST.value(),
    //             LocalDateTime.now(),
    //             ex.getMessage(),
    //             request.getDescription(false)
    //     );
    //     return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    // }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField().concat(":").concat(err.getDefaultMessage()))
                .collect(Collectors.joining(","));
        CustomResponse err = new CustomResponse(
                HttpStatus.BAD_REQUEST.value(),
                LocalDateTime.now(),
                msg,
                request.getDescription(false)
        );
        return new ResponseEntity<>(err, HttpStatus.BAD_REQUEST);
    }
}