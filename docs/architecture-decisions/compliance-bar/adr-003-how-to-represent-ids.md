# ADR 003: how to represent ids

## Discussion
We need identities to represent the compliance-shot and configuration-item entities. These ids will also be part of our 
urls (identifying the resources). Our wish-list for such ids are:
* not be easily guessable
* not expose information
* ideally to be kind of readable

I want to go with a UUID or similar (see below for configuration items)

Problem is that we are going to use these ids as primary keys for compliance-shots and configuration items and this brings downsides:
* takes more space
* more costly insertion cause of size and generation time
* not sortable (not important for our use case)
* maybe but compare performance (?) and slower searches
* The coupling of the primary key with external resource identifier has the problem that if either of them need to change for reasons, the other is affected

The concerns that are applicable for me are the space and latency hit we would get. But data access patterns of our 
implementation, we would need to expose a value like UUID and be queried by it, we would use it as an index. That means:
* We wouldnt save the space
* Only during insertion time we would get a hit but our app is not write heavy
* slower comparison for retrieval but we would have to retrieve using this value anyway

**What id for the configuration items?**
When retrieving configuration items for a new shot and we are going to persist them, we need to be able to know if this 
an item we have seen before or a new one. The configuration items are uniquely identified from the externalId+cmdbName combo.
When inserting configuration items for a new compliace-shot, we want to do a batch insert and let the orm framework find 
and merge the items that exist, instead of querying the db ourselves. To do this we will create id by hashing the 
externalId+cmdbName combination. This way when batch inserting CIs, existing items will be identified and updated by the framework.

## Decision
* compliance-shot ids are UUIDs
* configuration-item ids are a hash of their externalId+cmdbName

## Consequences
All the cons mentioned in the discussion section
