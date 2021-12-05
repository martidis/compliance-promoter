# ADR 004: Requests validation
The requirements for valid requests from our consumers are simple and will only focus on aic ratings and email values.

## Decision
* Validate that availability, integrity and confidentiality ratings are within range 1-3
* Validate the email value
* Validate as early as possible (on controller but also have validations on entity in case controller level checks fail and test for these flows)

## Rationale
* AIC and email checks only cos they are the only interesting fields to validate against
* The checks both on controller and on entity level to be safe, I feel both that are unnecessary and that they don't hurt. I prefer to have both

## Consequences
* Code provides both value and liability. The extra annotations in entity and the extra tests if controllers checks were missed might be considered unnecessary or confusing
