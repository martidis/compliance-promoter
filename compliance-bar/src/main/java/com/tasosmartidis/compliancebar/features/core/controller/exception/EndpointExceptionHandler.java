package com.tasosmartidis.compliancebar.features.core.controller.exception;

import com.tasosmartidis.compliancebar.features.core.model.exception.ResourceNotFoundException;
import com.tasosmartidis.compliancebar.features.core.model.exception.TooManyRequestsException;
import io.micrometer.core.instrument.Metrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@ControllerAdvice
public class EndpointExceptionHandler {

	public static final String ERROR_PAGE = "error";
	private static final String COMPLIANCE_BAR_ERRORS_COUNTER = "compliancebar.errors.counter";

	@ExceptionHandler(ResourceNotFoundException.class)
	public String complianceShotNotFoundException(ResourceNotFoundException ex, Model model) {
		log.error("We cannot find the resource of the request. Message: {}",ex.getMessage());
		Metrics.counter(COMPLIANCE_BAR_ERRORS_COUNTER).increment();
		model.addAttribute("errorMessage", "We seem to be ghosted!");
		return ERROR_PAGE;
	}

	@ExceptionHandler(TooManyRequestsException.class)
	public String rateLimittingKickedIn(TooManyRequestsException ex, Model model) {
		log.error("Rate limit for creating shots reached. Message: {}",ex.getMessage());
		Metrics.counter(COMPLIANCE_BAR_ERRORS_COUNTER).increment();
		model.addAttribute("errorMessage", "Too many shots, slow down! " +
				"You can have create shots again in: " + ex.getTryAgainInMilliseconds()/1_000_000_000 + " seconds");
		return ERROR_PAGE;
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public String validationFailException(MethodArgumentNotValidException ex) {
		log.error("Syntactically correct request but failed functional validation. Message: {}",ex.getMessage());
		Metrics.counter(COMPLIANCE_BAR_ERRORS_COUNTER).increment();
		return ERROR_PAGE;

	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public String dataIntegrityViolationBecauseOfUniqueConstraint(DataIntegrityViolationException ex) {
		log.error("Data integrity constraint violation. Message: {}", ex.getMessage());
		Metrics.counter(COMPLIANCE_BAR_ERRORS_COUNTER).increment();
		return ERROR_PAGE;
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public String methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
		log.error("Syntactically correct request but failed functional validation of request in controller. Message: {}",ex.getMessage());
		Metrics.counter(COMPLIANCE_BAR_ERRORS_COUNTER).increment();
		return ERROR_PAGE;
	}

	@ExceptionHandler(Exception.class)
	public String catchWhateverWeMissed(Exception ex) {
		log.error("Something we missed was caught in the generic error handler. Message: {}", ex.getMessage());
		Metrics.counter(COMPLIANCE_BAR_ERRORS_COUNTER).increment();
		return ERROR_PAGE;
	}
}
