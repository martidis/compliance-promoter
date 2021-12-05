# ADR 002: Primary key selection
This is going to be simply a CRUD api with almost no business logic. The primary key selection we consider here is essentially
the dilemma, do I want to expose the primary key as identifier for the resource in my api or should I have a different field?

Requirements for the identifier of the resources:
* not be easily guessable 
* not expose information 
* ideally to be kind of readable

## Decision
While acknowledging the consequences below, we will use UUIDs for primary keys and these values will also be exposed as resource identifiers in our api

## Rationale
* we are not going to scale, this is just a small utility application for a demo with mock data, let's keep perspective
* we sacrifice readability. api will be used by systems, human users should use the  application name to find it and then they would use the UI to make changes

## Consequences
* Concerns with UUIDs as primary keys are not great performance wise as primary keys, but we would in any case need to index the identifier we use in our api to identify resources, this is how entries will be queried (other than aic rating)
  * use more space  
  * more costly insertion cause of size and generation time 
  * not sortable (not important for our use case)
  * maybe but compare performance (?) and slower searches
* The coupling of the primary key with external resource identifier has the problem that if either of them need to change for reasons, the other is affected
* Readability of the id is sacrificed. Human consumers should use application name an the GUI (if we had one)
