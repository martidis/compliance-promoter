### Objectives
We will have a minimal monitoring setup for our applications. Things that we are interested
* what do our customers experience
  * error rates
  * response times
* what is happening in our application
  * errors
  * cache hit/miss
  * database queries

We are going to collect these with micrometer, store in prometheus and display in grafana

* check response time, count and error rate (for one endpoint only)
* check garbage collection
* check memory usage?
* check cache
* check db

what about alerts? No alerts for now, I think it is besides the point
