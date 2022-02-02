# ADR 005: securing connections

## Discussion
We want of course to have secure and robust application. But the goal of this project is a PoC. We want people to be able 
to start it up locally and play with it. 

This brings a challenge. If I require secure channel and use a self-signed certificate, they would need to make changes in 
their browser to trust our certificate. I wouldn't do that for someone else's cert, why would they?

So I will disable secure channel for the GUI and for consumers of our endpoints like prometheus. For connection to mycmdb
we can configure communication over mTLS with no problem

## Decision
Allow insecure connections for demo purposes. Specify where in the system design

## Consequences
