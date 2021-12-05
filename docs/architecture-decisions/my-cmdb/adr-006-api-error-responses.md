# ADR 006: API error responses

## Decision
Error responses to provide only HTTP response and message until need to do otherwise comes

## Rationale
* Error codes would allow consumers to behave differently in cases that require different flow under the same http code, but I don't think it will in our demo use case so I keep it simple

## Consequences
* Might require changes later 
