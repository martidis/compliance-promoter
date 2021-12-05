# ADR 005: API query patterns
Compliance-bar will consume mycmdb by querying applications for which compliance shots apply. 

## Decision
Query values for aic will be the minimum rating for each attribute for which it will apply. All those with higher are also in scope.
For example minIntegrity=2 will include all applications with I equal to 2 or 3. Any rating type (aic) not specified takes default min value of 1

## Rationale

## Consequences
