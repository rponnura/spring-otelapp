FROM openjdk:11
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
RUN echo "$PWD"
WORKDIR /
## Added for Otel
COPY opentelemetry-javaagent.jar opentelemetry-javaagent.jar 
ENV JAVA_TOOL_OPTIONS=-javaagent:/opentelemetry-javaagent.jar
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://otelcol:4317"
ENV OTEL_EXPORTER_OTLP_METRICS_TEMPORALITY_PREFERENCE="cumulative"
ENV OTEL_RESOURCE_ATTRIBUTES="service.namespace=opentelemetry-demo"
ENV OTEL_LOGS_EXPORTER="otlp"
ENV OTEL_SERVICE_NAME="jhooqotel"
## End of Otel
ENTRYPOINT ["java","-jar","/app.jar"]
