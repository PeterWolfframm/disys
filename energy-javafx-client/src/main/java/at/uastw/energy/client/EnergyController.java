package at.uastw.energy.client;

import at.uastw.energy.client.model.CurrentPercentage;
import at.uastw.energy.client.model.HistoricalUsage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EnergyController {

    @FXML
    private Label lblCommunityPool;
    @FXML
    private Label lblGridPortion;
    @FXML
    private Button btnRefresh;
    @FXML
    private DatePicker dpStartDate;
    @FXML
    private DatePicker dpEndDate;
    @FXML
    private Button btnShowData;
    @FXML
    private TableView<HistoricalUsage> tableHistoricalUsage;
    @FXML
    private TableColumn<HistoricalUsage, LocalDateTime> colHour;
    @FXML
    private TableColumn<HistoricalUsage, Double> colCommunityProduced;
    @FXML
    private TableColumn<HistoricalUsage, Double> colCommunityUsed;
    @FXML
    private TableColumn<HistoricalUsage, Double> colGridUsed;
    @FXML
    private Label lblStatus;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String API_BASE_URL = "http://localhost:8080/energy";


    @FXML
    public void initialize() {
        setupTable();
        btnRefresh.setOnAction(event -> fetchCurrentData());
        btnShowData.setOnAction(event -> fetchHistoricalData());
        fetchCurrentData(); // Fetch on startup
    }

    private void setupTable() {
        colHour.setCellValueFactory(new PropertyValueFactory<>("hour"));
        colHour.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        colCommunityProduced.setCellValueFactory(new PropertyValueFactory<>("communityProduced"));
        colCommunityUsed.setCellValueFactory(new PropertyValueFactory<>("communityUsed"));
        colGridUsed.setCellValueFactory(new PropertyValueFactory<>("gridUsed"));
    }

    private void fetchCurrentData() {
        updateStatus("Fetching current data...");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + "/current"))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        CurrentPercentage data = objectMapper.readValue(body, CurrentPercentage.class);
                        Platform.runLater(() -> {
                            lblCommunityPool.setText(String.format("%.2f %%", data.getCommunityDepleted()));
                            lblGridPortion.setText(String.format("%.2f %%", data.getGridPortion()));
                            updateStatus("Current data updated.");
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> updateStatus("Error parsing current data: " + e.getMessage()));
                    }
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> updateStatus("Failed to fetch current data: " + e.getMessage()));
                    return null;
                });
    }

    private void fetchHistoricalData() {
        LocalDate start = dpStartDate.getValue();
        LocalDate end = dpEndDate.getValue();

        if (start == null || end == null) {
            updateStatus("Please select both start and end dates.");
            return;
        }
        if (start.isAfter(end)) {
            updateStatus("Start date must be before end date.");
            return;
        }

        updateStatus("Fetching historical data...");

        DateTimeFormatter paramFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String startStr = start.atStartOfDay().format(paramFormatter);
        String endStr = end.atTime(23, 59, 59).format(paramFormatter);

        String url = API_BASE_URL + "/historical?start=" + startStr + "&end=" + endStr;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> {
                    try {
                        List<HistoricalUsage> data = objectMapper.readValue(body, new TypeReference<>() {});
                        Platform.runLater(() -> {
                            tableHistoricalUsage.setItems(FXCollections.observableArrayList(data));
                            updateStatus(String.format("Fetched %d records.", data.size()));
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> updateStatus("Error parsing historical data: " + e.getMessage()));
                    }
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> updateStatus("Failed to fetch historical data: " + e.getMessage()));
                    return null;
                });
    }

    private void updateStatus(String message) {
        lblStatus.setText(message);
    }
} 