# spring-otelapp

## Build Java source code

`./gradelew build`

This will create jar file under build/libs directory

## Build docker container and run
`docker build --no-cache -t  jhooq-job .`

`docker run -p 8300:8080 --network "opentelemetry-demo" jhooq-job  -e JAVA_TOOL_OPTIONS="-javaagent:/opentelemetry-javaagent.jar" -e OTEL_EXPORTER_OTLP_ENDPOINT="http://otelcol:4317" -e OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE="cumulative" -e OTEL_RESOURCE_ATTRIBUTES="service.namespace=opentelemetry-demo" -e OTEL_LOGS_EXPORTER="otlp" -e OTEL_SERVICE_NAME="jhooqotel"  -e OTEL_JAVAAGENT_DEBUG="true"`