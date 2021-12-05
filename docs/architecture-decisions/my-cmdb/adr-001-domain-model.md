# ADR 001: Domain model
The scope of the domain model discussion here, essentially is about how fancy we want to make the mycmdb-app. 
Since this is a utility service to showcase our main application (compliance-bar), the answer is not much. And this will 
reflect on the model.

## Decision
Keep domain model minimal, just enough to support the use case. Fields we need
* Availability, confidentiality and integrity ratings (based on these applications will apply for the shot or not)
* email (because notification mechanism wll be in place)
* system owner (not really needed but I feel like adding it)
* identifier for the application
* name for the application (this will be unique, this is how it would be easiest to search an application outside the compliance-shots context)

## Rationale
* Let's not lose focus on our intention, it is not about the business logic of this app. We don't care about business logic complexity

## Consequences
* Business logic implementation will be a bit naive. But the implementation and operability aspects will be made more interesting 
