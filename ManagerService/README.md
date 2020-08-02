Deployment:

1.Change DB path on application.properties, use unique schema name for each service;

2.Run maven clean install;

3.run the jar using: 
java -jar path/to/your/jarfile.jar com.intuit.interview.AggregationService 


For Care AGents:

1.Go to http://localhost:8080/swagger-ui.html#/data-aggregation-controller/getMyAggregatedHubUsingGET

And execute the API for product search