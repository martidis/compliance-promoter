ALTER SYSTEM SET ssl_cert_file TO '/var/lib/postgresql/certs/compliance-bar-db.crt';
ALTER SYSTEM SET ssl_key_file TO '/var/lib/postgresql/certs/compliance-bar-db.key';
ALTER SYSTEM SET ssl TO 'ON';
