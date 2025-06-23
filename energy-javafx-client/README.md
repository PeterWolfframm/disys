# Energy JavaFX Client

This is the JavaFX client application for visualizing Energy Data from the REST API.

## Features

- **Current Energy Data**: Fetches and displays current energy consumption with formatted output
- **Historic Energy Data**: Fetches and displays historic energy data in a structured table format
- **Data Parsing**: Properly parses JSON responses and presents them in a user-friendly format
- **Async Operations**: Non-blocking UI with async data fetching

## Prerequisites

- Java 21 or higher
- Maven 3.6+
- The Energy REST API server running on http://localhost:8080

## Running the Application

To run the JavaFX client:

```bash
mvn clean javafx:run
```

Or compile and run:

```bash
mvn clean compile
mvn javafx:run
```

## Data Display Features

### Current Energy Data
- Shows formatted timestamp
- Displays power consumption in kW
- Provides consumption status (Low/Normal/High)

### Historic Energy Data
- Table view with sortable columns
- Formatted timestamps
- Power values displayed in kW units
- Easy to read tabular format

## Dependencies

- JavaFX 21 (Controls and FXML)
- Jackson (JSON parsing)
- Java HTTP Client (for REST API calls) 