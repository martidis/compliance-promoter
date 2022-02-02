package com.tasosmartidis.compliancebar.features.core.controller;

import com.tasosmartidis.compliancebar.features.core.model.api.ComplianceShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.api.ConfigurationItemsSelectionForShotRequest;
import com.tasosmartidis.compliancebar.features.core.model.domain.ComplianceShot;
import com.tasosmartidis.compliancebar.features.core.model.domain.ConfigurationItem;
import com.tasosmartidis.compliancebar.features.core.model.exception.TooManyRequestsException;
import com.tasosmartidis.compliancebar.features.core.model.view.Viewable;
import com.tasosmartidis.compliancebar.features.core.service.ComplianceShotsService;
import com.tasosmartidis.compliancebar.features.core.service.RateLimitingService;
import com.tasosmartidis.compliancebar.features.core.service.ViewsConstructionService;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Controller
public class ComplianceBarController {

	private final ViewsConstructionService viewsConstructionService;
	private final ComplianceShotsService complianceShotsService;
	private final Cache complianceBarCache;
	private final RateLimitingService rateLimitingService;

	@Timed(value = "compliancebar.controller.get.dashboard", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.get.dashboard")
	@GetMapping({"/overview", "/"})
	public String overview(Model model, Authentication authentication) {
		var overview = viewsConstructionService.createDashboardView();
		setModelAttributes(model, authentication,"overview", overview);
		return "overview";
	}

	@Timed(value = "compliancebar.controller.get.complianceshots", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.get.complianceshots")
	@GetMapping("/compliance-shots")
	public String complianceShots(Model model, Authentication authentication) {
		var complianceShotsView = viewsConstructionService.createComplianceShotsOverview();
		setModelAttributes(model, authentication,"complianceShotsView", complianceShotsView);
		return "compliance-shots";
	}

	@Timed(value = "compliancebar.controller.get.complianceshots.id", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.get.complianceshots.id")
	@GetMapping("/compliance-shots/{shotId}")
	public String complianceShotDetails(@PathVariable String shotId, Model model, Authentication authentication) {
		var complianceShotDetailsView = viewsConstructionService.createComplianceShotDetailsView(shotId);
		setModelAttributes(model, authentication,"complianceShotDetailsView", complianceShotDetailsView);
		return "compliance-shot-details";
	}

	@Timed(value = "compliancebar.controller.get.configurationitems", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.get.configurationitems")
	@GetMapping("/configuration-items")
	public String configurationItems(Model model, Authentication authentication) {
		var configurationItemsView = viewsConstructionService.createConfigurationItemsView();
		setModelAttributes(model, authentication,"configurationItemsView", configurationItemsView);
		return "configuration-items";
	}

	@Timed(value = "compliancebar.controller.get.configurationitems.id", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.get.configurationitems.id")
	@GetMapping("/configuration-items/{id}")
	public String configurationItemDetails(@PathVariable String id, Model model, Authentication authentication) {
		var configurationItemView = viewsConstructionService.createConfigurationItemDetailsView(id);
		setModelAttributes(model, authentication,"configurationItemView", configurationItemView);
		return "configuration-item-details";
	}

	@Timed(value = "compliancebar.controller.get.complianceshots.requests", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.get.complianceshots.requests")
	@GetMapping("/compliance-shots/requests")
	public String initiateComplianceShot(Model model, Authentication authentication) {
		setModelAttributes(model, authentication,"complianceShot", ComplianceShot.builder().build());
		return "create-shot";
	}

	@Timed(value = "compliancebar.controller.post.complianceshots.requests", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.post.complianceshots.requests")
	@PostMapping("/compliance-shots/requests")
	public String initiateComplianceShot(@Valid ComplianceShot complianceShot, BindingResult bindingResult,
							   Model model, Authentication authentication) {
		if(bindingResult.hasErrors()) {
			return "create-shot";
		}

		if(complianceShotsService.complianceShotTitleExists(complianceShot.getTitle())) {
			model.addAttribute("message", "Title exists! Select another one");
			return "create-shot";
		}

		var cisSelectionForShotRequest = complianceShotsService.createCIsSelectionForShotRequest(complianceShot, authentication);
		setModelAttributes(model, authentication,"cisSelectionForShotRequest", cisSelectionForShotRequest);
		log.debug("Initiated creation of compliance shot with id: {}", cisSelectionForShotRequest.getComplianceShotRequestId());
		return "configuration-items-selection";
	}

	@Timed(value = "compliancebar.controller.post.complianceshots.requests.id", percentiles = {0.95, 0.99}, histogram = true)
	@Counted(value = "compliancebar.controller.post.complianceshots.requests.id")
	@PostMapping("/compliance-shots/requests/{complianceShotRequestId}")
	public String finalizeComplianceShot(@PathVariable String complianceShotRequestId, Model model,
							   @ModelAttribute ConfigurationItemsSelectionForShotRequest cisSelectionForShotRequest,
							   RedirectAttributes redirectAttributes, Authentication authentication) {

		checkRateLimitReached(authentication);

		Set<String> selectedCisInRequest = getOnlySelectedCis(cisSelectionForShotRequest);
		if(selectedCisInRequest.isEmpty()) {
			createWarningScreenForNoSelectedCIs(complianceShotRequestId, model, authentication);
			return "configuration-items-selection";
		}

		complianceShotsService.createComplianceShot(complianceShotRequestId, selectedCisInRequest);

		redirectAttributes.addFlashAttribute("message", "Compliance shot created!");
		UserDetails user = (UserDetails) authentication.getPrincipal();
		redirectAttributes.addFlashAttribute("user", user.getUsername());
		log.info("Completed creation of compliance shot with id: {}", cisSelectionForShotRequest.getComplianceShotRequestId());
		return "redirect:/overview";
	}

	private void checkRateLimitReached(Authentication authentication) {
		var clientUsername = authentication.getName();
		Bucket tokenBucket = rateLimitingService.resolveBucket(clientUsername);
		ConsumptionProbe probe = tokenBucket.tryConsumeAndReturnRemaining(1);
		if (!probe.isConsumed()) {
			throw new TooManyRequestsException("Customer " + clientUsername + " exceeded the rate limit", probe.getNanosToWaitForRefill());
		}
	}

	private void createWarningScreenForNoSelectedCIs(String complianceShotRequestId, Model model, Authentication authentication) {
		var initialComplianceShotRequest = complianceBarCache.get(complianceShotRequestId, ComplianceShotRequest.class);
		setModelAttributes(model, authentication,"cisSelectionForShotRequest", new ConfigurationItemsSelectionForShotRequest(new ArrayList<>(initialComplianceShotRequest.getApplicableConfigurationItems()), complianceShotRequestId));
		model.addAttribute("message", "At least one configuration item must be selected for the shot");
	}

	private Set<String> getOnlySelectedCis(ConfigurationItemsSelectionForShotRequest cisSelectionForShotRequest) {
		return cisSelectionForShotRequest.getApplicableConfigurationItems()
				.stream()
				.filter(ci -> ci.getId() != null)
				.map(ConfigurationItem::getId)
				.collect(Collectors.toUnmodifiableSet());
	}

	private void setModelAttributes(Model model, Authentication authentication, String attributeName, Viewable viewable) {
		model.addAttribute(attributeName, viewable);
		UserDetails user = (UserDetails) authentication.getPrincipal();
		model.addAttribute("user", user.getUsername());
	}


}
