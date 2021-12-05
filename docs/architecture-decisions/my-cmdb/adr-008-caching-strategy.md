# ADR 008: Caching strategy 

## Decision
* We are going to cache only queries on aic rating. Reasoning: 
  * This is the most interesting dataset from the way that the application is going to be used
  * While we have multi-column indexes on aic, this is the heaviest query for our use case and most often will be the largest dataset to be retrieved
* The key used for the cache is the aic rating
* We are going to invalidate the cache entry for aic when an item with the given aic rating/key is deleted
* We are going to invalidate the cache entry for aic when an item with the given aic rating/key is updated and its aic values changed
* For invalidating on delete and update, our previous adr about retrieving the item first essentially allows that, because we then know what the aic rating of the item is and if we should invalidate as well of which aic key
* We are going to use a distributed in-memory datastore for caching (hazelcast)
  * This is an overkill and brings issues of network calls and synchronization on nodes
  * We do this because we want to keep things interesting

## Rationale

## Consequences
