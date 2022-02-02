# ADR 008: informing system owners about compliance shots

## Discussion
When a new shot is created, we need to notify the system owners of the applicable CIs. There is no reason to do this 
real-time, or wait for the notification to complete in order to create the shot. 

## Decision
When a shot is created and we persist the shot details and the applicable configuration items, we will inform the system 
owners asynchronously. We will publish on a topic in a queue the details of the CI system owners and a different application
will subscribe to the topic, and notify the system owners.

## Consequences
