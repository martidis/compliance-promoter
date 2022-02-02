FROM prom/prometheus
ADD prometheus.yml /etc/prometheus/prometheus.yml
COPY ./certs/prometheus.crt /data/prometheus.crt
COPY ./certs/prometheus.key /data/prometheus.key
