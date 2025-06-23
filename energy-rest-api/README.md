# Energy REST API

This is the REST API server for the Energy Data system.

## Running the Application

To run the REST API server:

```bash
mvn spring-boot:run
```

Or compile and run:

```bash
mvn clean compile
mvn spring-boot:run
```

The server will start on port 8080.

## Available Endpoints

- `GET /energy/current` - Returns current energy data as a double value
- `GET /energy/historic` - Returns historic energy data as a JSON array

## Example Usage

```bash
curl http://localhost:8080/energy/current
curl http://localhost:8080/energy/historic
``` 