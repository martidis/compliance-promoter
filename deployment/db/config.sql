ALTER SYSTEM SET ssl_cert_file TO '/var/lib/postgresql/certs/mycmdb-db.crt';
ALTER SYSTEM SET ssl_key_file TO '/var/lib/postgresql/certs/mycmdb-db.key';
ALTER SYSTEM SET ssl TO 'ON';
