# ADR 003: AIC representation
AIC rating is the meatiest part of this utility application. This is the main way our compliance-bar application will 
access mycmdb. It will query applications with a given aic, so it can serve the compliance shots which are applicable.

## Decision
* Have a numeric representation for the rating, 3=high, 2=medium, 1=low


## Rationale
* My experience with aic rating, using a numeric system of representation. I was thinking if I can use more descriptive values such as high, medium and low, so everyone without context could understand the meaning of the rating. However when using the rating to refer to an application, numeric values are more convenient. E.g., 332 vs High-High-Medium or even HHM

## Consequences
* That I am overthinking :)
