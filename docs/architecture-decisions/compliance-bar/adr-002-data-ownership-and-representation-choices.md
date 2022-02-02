# ADR 002: data ownership and representation choices

## Discussion
As mentioned in adr-001, our two crucial entities are _compliance-shots_ and _configuration-items_. These differ in ownership
* compliance-shots are this application's ownership and its persistence is the source of truth
* configuration items are owned by the external cmdb systems to which we integrate. We need to maintain some information
to be able to serve our purpose but will be the very necessary because we want to avoid as much as possible being part of 
their lifecycle

## Decision
For _configuration-items_, we persist
* external id and cmdb name
* system owner
* team email
* ci name

For _compliance-shots_, we persist
* title
* description
* minAvailiability, minIntegrity, minConfidentiality
* reference (to the policy)
* tutorial (on what you have to do to be compliant)
* compliance-level (blocking or not). This can be used to provide a hook for CI/CD to validate if they are compliant and thus allowed to release to prod.

## Consequences
For _configuration-items_
* if the external id changes, it will be entered as a new configuration-item (see also adr-003)
* if details like system owner and team email change, when configuration-item is retrieved for a next shot, these values will be updated 
* we do not want to maintain their aic rating
* aic (availability, confidentiality, integrity) rating of the CI is not maintained by us

That implies that if aic changes for example, or a new CI is created, there should be a sync. For example by having compliance-bar
exposing an endpoint that returns all configuration compliance shots applicable for a given aic rating and registers the CI for being
applicable for these shots
