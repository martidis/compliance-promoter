# ADR 009: monitoring strategy

## Discussion
We are going to implement light monitoring for our PoC. On a high-level, things we are usually interested in our monitoring strategy:
* what do my users experience?
* what is going on in my application?
* what is going on my dependencies and ecosystem?

The behavior we measure to do that are usually:
* latencies 
* error rates
* traffic
* crucial functional flows
* saturation

And then there is alerting.

## Decision
Here we will monitor only:
* Response times of our endpoints
* Counts of requests to our endpoints and error rates
* Crucial functions like when a new shot is created
* The cases where we hit the cache and go to the db
* Logging on errors we have
* Whatever we get out of the box from actuator
* Will have monitoring dashboard using prometheus and grafana
* Will implement no alerting

## Consequences
