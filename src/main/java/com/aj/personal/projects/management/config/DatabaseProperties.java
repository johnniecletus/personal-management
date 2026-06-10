package com.aj.personal.projects.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


/*

Dear Future AJ,

This class acts like the env.ts file used in Node.js.

Purpose:
- Read database configuration values from Spring Boot's configuration system.
- Group related configuration under the prefix "app.database".
- Validate that required configuration values are present before the application starts.
- Make the configuration available anywhere in the application through Dependency Injection.

Annotations Explained:

@Getter
- Lombok generates getter methods automatically.
- Example:
    getUrl()
    getUsername()
    getPassword()

@Setter
- Lombok generates setter methods automatically.
- Spring Boot uses these setters when binding configuration values.

@Component
- Registers this class as a Spring Bean.
- Allows Spring to create and manage an instance of this class.
- Enables us to inject it into services, controllers, etc.

@ConfigurationProperties(prefix = "app.database")
- Maps configuration values into this class.
- Example:

    app.database.url=${DB_URL}
    app.database.username=${DB_USERNAME}
    app.database.password=${DB_PASSWORD}

  becomes:

    url
    username
    password

- Think of this as Spring Boot's equivalent of grouping variables in a Node.js env object.

@Validated
- Enables validation for fields inside this class.
- Spring validates the configuration during application startup.
- If validation fails, the application will not start.

@NotBlank
- Ensures a value exists and is not empty.
- Prevents the application from running with missing configuration.

Node.js Comparison:

Node:
    process.env.DB_URL
    process.env.DB_USERNAME
    process.env.DB_PASSWORD

Spring:
    DatabaseProperties.getUrl()
    DatabaseProperties.getUsername()
    DatabaseProperties.getPassword()

This gives us a centralized, type-safe, and validated way of managing application configuration.
*/

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.database")
public class DatabaseProperties {

    @NotBlank(message = "Database URL is required")
    private String url;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;


}
