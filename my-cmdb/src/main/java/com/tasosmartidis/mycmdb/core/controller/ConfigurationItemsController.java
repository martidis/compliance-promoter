package com.tasosmartidis.mycmdb.core.controller;

import com.tasosmartidis.mycmdb.core.model.api.ConfigurationItemRequest;
import com.tasosmartidis.mycmdb.core.model.api.ConfigurationItemResponse;
import com.tasosmartidis.mycmdb.core.service.ConfigurationItemsService;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Validated
@RestController
@RequestMapping("/api/configuration/items")
public class ConfigurationItemsController {

	private final ConfigurationItemsService ciService;

	@GetMapping
	@Timed(value = "mycmdb.controller.configuration.items.get.requests", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "mycmdb.controller.configuration.items.get.requests")
	public ResponseEntity<List<ConfigurationItemResponse>> retrieveConfigurationItems(
			@RequestParam(required = false, defaultValue = "1") @Min(1) @Max(3) Integer minAvailability,
			@RequestParam(required = false, defaultValue = "1") @Min(1) @Max(3) Integer minIntegrity,
			@RequestParam(required = false, defaultValue = "1") @Min(1) @Max(3) Integer minConfidentiality) {
		log.info("Retrieve configuration items with min aic {}{}{}", minAvailability, minIntegrity, minConfidentiality);
		return ResponseEntity.ok(ciService.retrieveConfigurationItems(minAvailability, minIntegrity, minConfidentiality));
	}

	@GetMapping("/{id}")
	@Timed(value = "mycmdb.configuration.items.id.get.requests", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "mycmdb.configuration.items.id.get.requests")
	public ResponseEntity<ConfigurationItemResponse> retrieveConfigurationItem(@PathVariable UUID id) {
		log.info("Retrieve configuration item with id {}", id);
		return ResponseEntity.ok(ciService.retrieveConfigurationItem(id.toString()));
	}

	@PostMapping
	@Timed(value = "mycmdb.configuration.items.post.requests", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "mycmdb.configuration.items.post.requests")
	public ResponseEntity<ConfigurationItemResponse> createConfigurationItem(@Validated @RequestBody ConfigurationItemRequest request) {
		log.info("Create configuration item");

		var entity = ciService.createConfigurationItem(request);

		return ResponseEntity.ok(entity);
	}

	@PutMapping("/{id}")
	@Timed(value = "mycmdb.configuration.items.id.put.requests", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "mycmdb.configuration.items.id.put.requests")
	public ResponseEntity updateConfigurationItem(@PathVariable UUID id,
												  @Validated @RequestBody ConfigurationItemRequest request) {
		log.info("Update configuration item with id {}", id);

		ciService.updateConfigurationItem(id.toString(), request);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	@Timed(value = "mycmdb.configuration.items.id.delete.requests", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "mycmdb.configuration.items.id.delete.requests")
	public ResponseEntity deleteConfigurationItem(@PathVariable String id) {
		log.info("Delete configuration item with id {}", id);

		ciService.deleteConfigurationItem(id);

		return ResponseEntity.ok().build();
	}


}
