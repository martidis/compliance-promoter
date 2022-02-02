FROM postgres:13.1-alpine

ENV POSTGRES_PASSWORD admin
ENV POSTGRES_USER mycmdb
ENV POSTGRES_DB mycmdb

COPY ./config.sql /docker-entrypoint-initdb.d/config.sql
COPY ./certs/mycmdb-db.crt var/lib/postgresql/certs/mycmdb-db.crt
COPY ./certs/mycmdb-db.key var/lib/postgresql/certs/mycmdb-db.key

RUN chmod 600 var/lib/postgresql/certs/mycmdb-db.key
RUN chown postgres:postgres var/lib/postgresql/certs/mycmdb-db.key
