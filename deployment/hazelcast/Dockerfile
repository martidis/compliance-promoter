FROM hazelcast/hazelcast
ENV HZ_NETWORK_RESTAPI_ENABLED=true

# took insight from this nice repo: https://github.com/fsamir/hazelcast-bucket4j
# and made it a bit worse

ADD ./jars/bucket4j-core-6.4.1.jar ${HZ_HOME}/lib
ADD ./jars/bucket4j-jcache-6.4.1.jar ${HZ_HOME}/lib
ADD ./jars/bucket4j-hazelcast-6.4.1.jar ${HZ_HOME}/lib
ADD ./jars/cache-api-1.1.1.jar ${HZ_HOME}/lib
