package at.uastw.energy.client;

import at.uastw.energy.client.model.EnergyData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EnergyController {
    @FXML
    private Button btnCurrentHour;

    @FXML
    private Button btnHistoricData;

    @FXML
    private TextArea taCurrentResult;

    @FXML
    private TableView<EnergyData> tableHistoric;

    @FXML
    private TableColumn<EnergyData, Instant> colTimestamp;

    @FXML
    private TableColumn<EnergyData, Double> colLeistung;

    @FXML
    private Label lblStatus;

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    @FXML
    public void initialize() {
        setupTable();
        btnCurrentHour.setOnAction(event -> fetchCurrentHourData());
        btnHistoricData.setOnAction(event -> fetchHistoricData());
    }

    private void setupTable() {
        colTimestamp.setCellValueFactory(new PropertyValueFactory<>("created"));
        colTimestamp.setCellFactory(column -> new javafx.scene.control.TableCell<EnergyData, Instant>() {
            @Override
            protected void updateItem(Instant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        
        colLeistung.setCellValueFactory(new PropertyValueFactory<>("leistung"));
        colLeistung.setCellFactory(column -> new javafx.scene.control.TableCell<EnergyData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f kW", item));
                }
            }
        });
    }

    private void fetchCurrentHourData() {
        lblStatus.setText("Fetching current data...");
        
        CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/energy/current"))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        double currentEnergy = Double.parseDouble(response.body());
                        String formattedResult = String.format(
                            "Current Energy Data\n" +
                            "==================\n" +
                            "Timestamp: %s\n" +
                            "Power: %.2f kW\n" +
                            "Status: %s",
                            formatter.format(Instant.now()),
                            currentEnergy,
                            getCurrentEnergyStatus(currentEnergy)
                        );
                        taCurrentResult.setText(formattedResult);
                        lblStatus.setText("Current data retrieved successfully");
                    } catch (NumberFormatException e) {
                        lblStatus.setText("Error parsing current data: " + e.getMessage());
                    }
                } else {
                    lblStatus.setText("Error: HTTP " + response.statusCode());
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                lblStatus.setText("Error: " + throwable.getMessage());
            });
            return null;
        });
    }

    private void fetchHistoricData() {
        lblStatus.setText("Fetching historic data...");
        
        CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:8080/energy/historic"))
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(response -> {
            Platform.runLater(() -> {
                if (response.statusCode() == 200) {
                    try {
                        List<EnergyData> energyDataList = objectMapper.readValue(
                            response.body(), 
                            new TypeReference<List<EnergyData>>() {}
                        );
                        
                        ObservableList<EnergyData> observableList = FXCollections.observableArrayList(energyDataList);
                        tableHistoric.setItems(observableList);
                        
                        lblStatus.setText(String.format("Historic data retrieved successfully (%d records)", energyDataList.size()));
                    } catch (Exception e) {
                        lblStatus.setText("Error parsing historic data: " + e.getMessage());
                    }
                } else {
                    lblStatus.setText("Error: HTTP " + response.statusCode());
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                lblStatus.setText("Error: " + throwable.getMessage());
            });
            return null;
        });
    }

    private String getCurrentEnergyStatus(double energy) {
        if (energy < 30) return "Low consumption";
        else if (energy < 70) return "Normal consumption";
        else return "High consumption";
    }
} 