# ADR 004: Database access patterns

## Discussion
* Constructing the dashboard requires fetching all compliance shots, configuration items and their status from the junction table. Also the number of teams (system owners) and at least a bounded list of names for the shots and configuration items
* This is a very expensive calculation. It could make our page loads slow and keep the connection and threads working on fetching and transmitting data for this one page request
* We could try to improve.
  * First of all we want to implement pagination when fetching configuration items and compliance shots (not exactly related to dashboard view)
  * We could try to precalculate the statistics (dashboard view) and have it ready in our cache. This could be a scheduled task
    * We need to make sure that new changes by the user (such as adding a shot) are visible to them, so update our cache. We do not care if other users do not immediately see the changes of this user. We lean towards eventual consistency on this

Keeping in mind our context, we will keep our solution appropriate to our PoC nature of this project. Queries and calculations are executed every time. Caching the responses from the database will spare us from some queries. Caches are invalidated when a new shot is created

## Decision
Naive implementation of the querying for the analytics in our dashboard. Utilize cache only to spare db queries

## Consequences
We would need rework if we want to use it in real-life 
