FROM postgres:13.1-alpine

ENV POSTGRES_PASSWORD admin
ENV POSTGRES_USER compliance-bar
ENV POSTGRES_DB compliance-bar

COPY ./config.sql /docker-entrypoint-initdb.d/config.sql
COPY ./certs/compliance-bar-db.crt var/lib/postgresql/certs/compliance-bar-db.crt
COPY ./certs/compliance-bar-db.key var/lib/postgresql/certs/compliance-bar-db.key

RUN chmod 600 var/lib/postgresql/certs/compliance-bar-db.key
RUN chown postgres:postgres var/lib/postgresql/certs/compliance-bar-db.key
