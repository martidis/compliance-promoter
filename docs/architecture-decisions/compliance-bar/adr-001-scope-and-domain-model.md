# ADR 001: scope and domain model

## Discussion
The project is a proof of concept, showcasing an idea. We might over-engineer or employ technologies not justified by 
the use case in order to make it interesting but the business context and the domain is limited to the compliance shots 
initiative. Our domain model will be represented accordingly.

## Decision
The tool has two crucial entities, _compliance-shots_ and _configuration-items_. These inherently have a many-to-many 
relationship, and their relationship will involve an extra payload, status. To make it more clear
* a compliance shot will have one or more configuration items for which it is applicable
* a configuration item (known to our system) will have one or more compliance shots attached to it
* a configuration item has status of on a given shot, and vice-versa a shot is on a given status for a configuration item

## Consequences
* Our use case/domain is best represented with a relational data model
