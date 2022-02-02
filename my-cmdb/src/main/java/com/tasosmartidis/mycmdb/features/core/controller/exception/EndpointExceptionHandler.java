package com.tasosmartidis.mycmdb.features.core.controller.exception;

import com.tasosmartidis.mycmdb.features.core.model.api.ErrorResponse;
import com.tasosmartidis.mycmdb.features.core.model.exception.NonExistentConfigurationItemException;
import com.tasosmartidis.mycmdb.features.core.model.exception.TooManyRequestsException;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@Slf4j
@ControllerAdvice
public class EndpointExceptionHandler {

	private static final String MYCMDB_ERRORS_COUNTER = "mycmdb.errors.counter";

	@ExceptionHandler(TransactionSystemException.class)
	public ResponseEntity<ErrorResponse> validationOfRequestFieldsFailed(TransactionSystemException ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error(ex.getMessage());
		// using jparepository makes crud operations transactional by default
		// so our constraint violations are caught as transactionsystemexceptions
		if(ex.getRootCause() instanceof ConstraintViolationException) {
			return ResponseEntity.unprocessableEntity()
					.body(new ErrorResponse("We understand your request but cannot process it." +
							"Did you send valid data?"));
		}

		// for other cases this is thrown, I have to think of possibilities. for now generic error
		return ResponseEntity.internalServerError()
				.body(new ErrorResponse("We messed up, but nothing bad happened. " +
						"Try again later. Sorry!"));
	}

	@ExceptionHandler(NonExistentConfigurationItemException.class)
	public ResponseEntity unknownConfigurationItemException(NonExistentConfigurationItemException ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error("Couldn't find requested configuration item. Message: {}", ex.getMessage());
		return ResponseEntity.notFound().build();
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity argumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error(ex.getMessage());
		return ResponseEntity.badRequest().build();
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> dataIntegrityViolationBecauseOfUniqueConstraint(DataIntegrityViolationException ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error("Uniqueness constraint violation. Message: {}", ex.getMessage());
		return ResponseEntity.unprocessableEntity()
				.body(new ErrorResponse("The CI name exists! Try another one"));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> constraintViolationException(ConstraintViolationException ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error(ex.getMessage());
		return ResponseEntity.unprocessableEntity()
				.body(new ErrorResponse(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error("Syntactically correct request but failed functional validation. Message: {}",ex.getMessage());
		return ResponseEntity.unprocessableEntity()
				.body(new ErrorResponse("We understand your request but cannot process it." +
						"Did you send valid data?"));
	}

	@ExceptionHandler(TooManyRequestsException.class)
	public ResponseEntity<ErrorResponse> clientReachedRateLimit(TooManyRequestsException ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error("Rate limiting exception thrown. Message: {}",ex.getMessage());
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.body(new ErrorResponse("Slow down! Don't be greedy"));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> catchWhateverWeMissed(Exception ex) {
		Metrics.counter(MYCMDB_ERRORS_COUNTER).increment();
		log.error("Something we missed was caught in the generic error handler. Message: {}", ex.getMessage());
		return ResponseEntity.internalServerError()
				.body(new ErrorResponse("We messed up, but nothing bad happened. " +
				"Try again later. Sorry!"));
	}
}
