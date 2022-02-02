# ADR 006: handling relevant changes outside our control

## Discussion
A main actor in our compliance story is outside our control, specifically the configuration items. We integrate with 
external cmdb systems and retrieve applicable items for a given shot. We inform their owners then. But from the time we 
get the response the information we have about the CIs might be altered. Other AIC ratings, other system owners, CIs created
or removed. We would need to have endpoints for these systems or in scripts maintained by teams as a workaround to inform us
about changes and sync up.

Also we need to be notified by teams about the progress they make for a given compliance shot. So an endpoint in our api for this as well.

## Decision
* Endpoint for providing all applicable compliance-shots for a given aic rating. And registering which CI now has to implement these shots
* Endpoint for updating the status of a compliance-shot for a CI
* Endpoint for changes in the details of a CI (e.g., the aic rating) and then we go an cancel the shots that are not relevant anymore

## Consequences
