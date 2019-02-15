# Spring Boot with Prometheus and Grafana Sample

This project shows a sample integration with Prometheus and Grafana.

To see a demo running, you will need:
- a running app 
- a running Prometheus server
- a running Grafana server

## Installation

This project uses the spring-boot actuator, which can be configured to provide an endpoint for prometheus to consume.

Run the app and go to the following url to see the Prometheus metrics.

http://localhost:8080/actuator/prometheus

The app itself has an endpoint that will trigger various http status codes:

GET http://localhost:8080/api/echo/{httpStatusCode}

Call the endpoint a few times with 2xx / 4xx / 5xx error codes and refresh the Prometheus endpoint.

You should see new metrics with a `http_server_requests_seconds` prefix, indicating the response time of the server.

You should also see new metrics with a `http_client_requests_seconds` prefix, indicating the response time of 
the endpoints we are calling.

Spring Boot 2.0 apps use [Micrometer](https://micrometer.io/) as a vendor-neutral application metrics facade. 
Since we want to use Prometheus to scrape our metrics, the app has been setup with the 
[Micrometer Prometheus](https://micrometer.io/docs/registry/prometheus) library.
You can see `io.micrometer:micrometer-registry-prometheus` in the `build.gradle` file.

Now let's setup Prometheus to consume the endpoint.

### Install Prometheus

Prometheus will be run on a docker image.

Install and run docker.

This project has a sample Prometheus config file, `prometheus.yml`, which must be edited with your IP address. 
Since Prometheus will be run on a Docker container, it won't have access to `localhost`.

Then run the command below to setup the Prometheus server on http://localhost:9090.
Make sure to replace `{repoFilePath}` with the path to your local copy of this repo.

```
docker run -d --name=prometheus -p 9090:9090 -v {repoFilePath}/prometheus.yml:/etc/prometheus/prometheus.yml prom/prometheus --config.file=/etc/prometheus/prometheus.yml
```

Once it's running, check it can access the metrics from the api.

Go to http://localhost:9090/graph, and in the Expression field, type `up` and click `execute`.

You should see
```
Element                                                 Value
-------                                                 -----
up{instance="{local ip}:8080",job="spring-actuator"}    1	
```

If the value is not `1`, make sure you have the right IP address. 
You might need to update your `prometheus.yml`, delete the docker image and container and try again.

Once set up, let's set up Grafana to visualize the Prometheus data.

### Install Grafana

Grafana can be installed to a local instance:

http://docs.grafana.org/installation/mac/

Once installed, open http://localhost:3000 and log in.

Create a Prometheus data source to consume the Prometheus data.
Give it a name and set the url field to your Prometheus endpoint.
Click `Save and Test` and make sure it says 

>Data source is working

You can create a new dashboard to visualize the data.
We'll be importing [this](https://grafana.com/dashboards/4701) existing dashboard for Micrometer instrumented applications

Go to to http://localhost:3000/dashboard/import and put `4701` in the
`Grafana.com Dashboard` field. Select the Prometheus data source you selected.  

Once you press `Import`, Grafana should create a new dashboard that
graphs the data scraped by Prometheus from the Spring boot app.

You can create additional graphs to track your api calls using the `http_client_requests` metrics. 
