package com.teamDevfuse.hbdonf.apigateway.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger2.web.Swagger2Controller;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Primary
@Component
public class SwaggerResourcesProvider implements springfox.documentation.swagger.web.SwaggerResourcesProvider {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public List<SwaggerResource> get() {
        List<String> serviceIds = discoveryClient.getServices();

        return getSwaggerResources(serviceIds);
    }

    private List<SwaggerResource> getSwaggerResources(List<String> serviceIds) {
        ConcurrentHashMap<String, SwaggerResource> serviceDescriptions = new ConcurrentHashMap<>();
        serviceIds.stream().forEach(serviceId -> {

            SwaggerResource resource = new SwaggerResource();
            if (applicationName.equals(serviceId)) {
                resource.setLocation(Swagger2Controller.DEFAULT_URL);

            } else {
                resource.setLocation("/" + serviceId + Swagger2Controller.DEFAULT_URL);

            }
            resource.setName(serviceId);
            resource.setSwaggerVersion("2.9.2");

            serviceDescriptions.put(serviceId, resource);
        });

        return serviceDescriptions.entrySet().stream().map(serviceDefinition -> {
            return serviceDefinition.getValue();
        }).collect(Collectors.toList());
    }
}
