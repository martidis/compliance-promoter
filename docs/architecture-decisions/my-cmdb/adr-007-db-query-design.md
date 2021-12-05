# ADR 007: DB query design
We persist the records our mycmdb in a database. We interact with this database mainly for CRUD operations. Here we mention any interesting decisions related to querying

## Decision
1. update and delete operations perform a retrieval of the item first and then either update or delete (exception thrown if item missing)

## Rationale
1. For this item there is a clear downside. But it helps us with caching (see adr-008). The most important flow of this api is querying based on the aic, and we will cache based on this aic. If the response is changed cos an item was updated their aic or deleted, we decided to invalidate cache for the given items. For this we need to know existing aic rating other than the id (for delete) or the updated aic values. I value this functionality more than the performance hit during update/delete since they will be very infrequent operations

## Consequences
1. we do an extra lookup for every update/delete and that of course costs us in performance
