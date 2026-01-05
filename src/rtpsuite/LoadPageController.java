package rtpsuite;

import java.io.IOException;
import static rtpsuite.CalculationMethods.EquivalentFieldSize;
import static rtpsuite.CalculationMethods.ReducedFieldSize;
import static rtpsuite.CalculationMethods.calculate_dmax;
import static rtpsuite.CalculationMethods.calculate_time;
import static rtpsuite.CalculationMethods.lookupValue;
import static rtpsuite.CalculationMethods.scatterValue;
import static rtpsuite.CalculationMethods.scpValue;
import static rtpsuite.CalculationMethods.toDP;

import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import static rtpsuite.CalculationMethods.checkDepth;
import static rtpsuite.CalculationMethods.checkFieldSize;
import static rtpsuite.FunctionalMethods.fetchUserLogs;
import static rtpsuite.FunctionalMethods.showAlert;
import static rtpsuite.FunctionalMethods.printData;
import java.time.LocalDate;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class
 *
 * @author Michael Okyere
 */
public class LoadPageController implements Initializable {

    // Datasets
    public HashMap<Integer, Double> decay_factors;
    public HashMap<String, double[]> TMR_SHEET;
    public HashMap<String, double[]> SCP_SHEET;
    public HashMap<String, double[]> PDD_SHEET;
    public double SADBFACTOR = 1;
    public double SSDBFACTOR = 1;
    public double SADWEDGE = 1;
    public double SSDWEDGE = 1;
    public StringBuilder CalculationValues;
    public User user;

    @FXML
    private RadioButton sadnb_wnone1;
    @FXML
    private Label welcomeUserLabel;
    @FXML
    private TextField sadnb_patientId;
    @FXML
    private TextField sadb_patientId;
    @FXML
    private TextField sadnbw_patientId;
    @FXML
    private TextField ssdnb_patientId;

    @FXML
    private TextField ssdb_patientId;
    @FXML
    private TextField qaDateField;

    @FXML
    private ToggleGroup sadbFactors;
    @FXML
    private ToggleGroup ssdFactors;
    @FXML
    private ToggleGroup wedgeFactors;
    @FXML
    private TableView<DataLog> CalculationHistoryTable;
    @FXML
    private TextField historySearchField;
    @FXML
    private TextField historyDateField;
    @FXML
    private TextField historyPatientSearchField;
    @FXML
    private TextField historyCalcTypeField;
    @FXML
    private Tab myClacsTab;

    private ObservableList<DataLog> dataLogs;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ...
        // TODO : Print user selected data/calculations
        // Initialize stuff
        TMR_SHEET = DataSheet.TMR_SHEET;
        SCP_SHEET = DataSheet.SCP_SHEET;
        PDD_SHEET = DataSheet.PDD_SHEET;
        CalculationValues = new StringBuilder();
        user = Session.getUser();
        if (welcomeUserLabel != null && user != null) {
            welcomeUserLabel.setText(user.getUsername());
        }
        qaDateField.setText(LocalDate.now().toString());
        setUpDataLogTable();

        MainPane.getTabs().clear();
        System.out.println("[-] Tabs : "+MainPane.getTabs().size());
        MainPane.setMaxSize(0, 8);
        MainPane.getTabs().add(Home);
        MainPane.getTabs().add(myClacsTab);
        System.out.println("[+] Tabs : "+MainPane.getTabs().size());
        MainPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                fadeInContent(newTab.getContent());
            }
        });
        // === TOOLTIPS FOR ALL CALCULATION TABS ===

        // SAD Open Field
        installTooltip(sadnb_x, "Field size along X-axis (jaw) at isocenter (100 cm SAD) in cm");
        installTooltip(sadnb_y, "Field size along Y-axis (jaw) at isocenter (100 cm SAD) in cm");
        installTooltip(sadnb_depth, "Depth of prescription point below surface in cm");
        installTooltip(sadnb_dose, "Total prescribed dose to the calculation point in cGy");

        // SAD Blocked Field
        installTooltip(sadb_x, "Field size along X-axis at isocenter in cm");
        installTooltip(sadb_y, "Field size along Y-axis at isocenter in cm");
        installTooltip(sadb_depth, "Depth of prescription point in cm");
        installTooltip(sadb_dose, "Prescribed dose in cGy");
        installTooltip(sadb_trayfac, "Standard blocking tray transmission factor applied");
        installTooltip(sadb_belly, "Custom factor (e.g., belly board or special tray)");
        installTooltip(sadb_nofac, "No tray factor — treat as open field");

        // SAD Wedged Field
        installTooltip(sadnbw_x, "Field size along X-axis at isocenter in cm");
        installTooltip(sadnbw_y, "Field size along Y-axis at isocenter in cm");
        installTooltip(sadnbw_depth, "Depth of prescription point in cm");
        installTooltip(sadnbw_dose, "Prescribed dose in cGy");
        installTooltip(sadnb_w151, "15° physical wedge factor applied");
        installTooltip(sadnb_w301, "30° physical wedge factor applied");
        installTooltip(sadnb_w451, "45° physical wedge factor applied");
        installTooltip(sadnb_w601, "60° physical wedge factor applied");

        // SSD Open Field
        installTooltip(ssdnb_x, "Field size along X-axis at surface (SSD) in cm");
        installTooltip(ssdnb_y, "Field size along Y-axis at surface (SSD) in cm");
        installTooltip(ssdnb_depth, "Depth of prescription point in cm");
        installTooltip(ssdnb_dose, "Prescribed dose in cGy");

        // SSD Blocked Field
        installTooltip(ssdb_x, "Field size along X-axis at surface in cm");
        installTooltip(ssdb_y, "Field size along Y-axis at surface in cm");
        installTooltip(ssdb_depth, "Depth of prescription point in cm");
        installTooltip(ssdb_dose, "Prescribed dose in cGy");
        installTooltip(ssdb_trayfac, "Standard blocking tray factor");
        installTooltip(ssdb_belly, "Custom tray or belly board factor");
        installTooltip(ssdb_nofac, "No tray factor applied");

        // LINAC QA Tab
        installTooltip(T1, "First temperature reading in °C");
        installTooltip(T2, "Second temperature reading in °C");
        installTooltip(P1, "First barometric pressure in mmHg");
        installTooltip(P2, "Second barometric pressure in mmHg");
        installTooltip(neg31, "Chamber reading at -3 cm off-axis (Gantry 180°)");
        installTooltip(neg32, "Repeat reading at -3 cm");
        installTooltip(zero1, "Central axis reading (Reading 1)");
        installTooltip(zero2, "Central axis reading (Reading 2)");
        installTooltip(pos31, "Chamber reading at +3 cm off-axis (Gantry 0°)");
        installTooltip(pos32, "Repeat reading at +3 cm");
        installTooltip(negC, "Negative crossplane symmetry reading");
        installTooltip(c0, "Central axis reference reading");
        installTooltip(c3, "Positive crossplane symmetry reading");

        // Patient ID fields (optional but helpful)
        installTooltip(sadnb_patientId, "Optional: Enter patient ID for logging and audit trail");
        installTooltip(sadb_patientId, "Optional: Enter patient ID for logging and audit trail");
        installTooltip(sadnbw_patientId, "Optional: Enter patient ID for logging and audit trail");
        installTooltip(ssdnb_patientId, "Optional: Enter patient ID for logging and audit trail");
        installTooltip(ssdb_patientId, "Optional: Enter patient ID for logging and audit trail");

        // QA Date
        installTooltip(qaDateField, "Date of QA measurement (format: YYYY-MM-DD)");

    }

    private void setUpDataLogTable() {

        TableColumn<DataLog, String> nameColumn = new TableColumn<>("UserName");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<DataLog, String> dateColumn = new TableColumn<>("Calculation Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("calculation_date"));

        TableColumn<DataLog, String> descriptionColumn = new TableColumn<>("Calculation Values");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("calculation_values"));

        TableColumn<DataLog, String> patientColumn = new TableColumn<>("Patient ID");
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient_id"));

        TableColumn<DataLog, String> calcTypeColumn = new TableColumn<>("Calculation Type");
        calcTypeColumn.setCellValueFactory(new PropertyValueFactory<>("calculation_type"));

        dataLogs = FXCollections.observableArrayList(fetchUserLogs(user.getUsername()));

        // Custom cell factory for the description column
        descriptionColumn.setCellFactory(col -> new TableCell<DataLog, String>() {
            private final TextArea textArea = new TextArea();

            {
                textArea.setWrapText(true);
                textArea.setEditable(false); // Make it read-only
                textArea.setPrefHeight(100); // Set a preferred height
                textArea.setVisible(false); // Initially hidden
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    textArea.setText(item);
                    setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 15px;");
                    setGraphic(textArea);
                    textArea.setVisible(true);
                }
            }
        });
        
        historySearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterHistoryLogTable();
        });
        historyDateField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterHistoryLogTable();
        });
        historyPatientSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterHistoryLogTable();
        });

        historyCalcTypeField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterHistoryLogTable();
        });

        CalculationHistoryTable.getColumns().addAll(nameColumn, patientColumn, dateColumn, calcTypeColumn, descriptionColumn);
        CalculationHistoryTable.setItems(dataLogs);
    }

    @FXML
    private void printSADOpenField(ActionEvent event) {
        VBox content = new VBox(10);
        content.setStyle("-fx-font-size: 16;");

        content.getChildren().addAll(
                createPrintRow("Calculation Type", "SAD Open Field"),
                createPrintRow("Patient ID", getTextOrNA(sadnb_patientId)),
                createPrintRow("Field Size X (cm)", getTextOrNA(sadnb_x)),
                createPrintRow("Field Size Y (cm)", getTextOrNA(sadnb_y)),
                createPrintRow("Depth (cm)", getTextOrNA(sadnb_depth)),
                createPrintRow("Prescribed Dose (cGy)", getTextOrNA(sadnb_dose)),
                new Label(""),
                createPrintRow("Equivalent Square (cm)", getTextOrNA(sadnb_eqfs)),
                createPrintRow("Sc,p", getTextOrNA(sadnb_scp)),
                createPrintRow("TMR", getTextOrNA(sadnb_tmr)),
                createPrintRow("Dose at dmax (cGy/MU)", getTextOrNA(sadnb_dmax)),
                createPrintRowBold("Monitor Units (MU)", getTextOrNA(sadnb_time))
        );

        printNode(content, "SAD Open Field Calculation Report");
    }

// todo : Repeat similar methods for other tabs... to print calculated values
    private String getTextOrNA(TextField field) {
        return field != null && !field.getText().trim().isEmpty() ? field.getText().trim() : "N/A";
    }

    private Node createPrintRow(String label, String value) {
        Label row = new Label(label + ": " + value);
        row.setFont(Font.font("System", 16));
        return row;
    }

    private Node createPrintRowBold(String label, String value) {
        Label row = new Label(label + ": " + value);
        row.setFont(Font.font("System", FontWeight.BOLD, 24));
        row.setStyle("-fx-text-fill: #003087;");
        return row;
    }

    private void printNode(Node content, String title) {
        // Create printable report
        VBox report = new VBox(15);
        report.setStyle("-fx-padding: 40; -fx-background-color: white;");

        // Header
        Label header = new Label(title);
        header.setFont(Font.font("System", FontWeight.BOLD, 28));
        header.setStyle("-fx-text-fill: #003087;");

        Label dateLabel = new Label("Printed on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        dateLabel.setFont(Font.font("System", 14));

        Label userLabel = new Label("User: " + user.getUsername());
        userLabel.setFont(Font.font("System", 14));

        report.getChildren().addAll(header, dateLabel, userLabel, new Label("")); // spacer

        // Add content (we'll pass formatted VBox)
        report.getChildren().add(content);

        // Print
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(null)) {
            boolean success = job.printPage(report);
            if (success) {
                job.endJob();
            }
        }
    }

    // Add tooltip
    private void installTooltip(Node node, String text) {
        if (node != null && text != null && !text.isEmpty()) {
            Tooltip tooltip = new Tooltip(text);
            tooltip.setStyle("-fx-font-size: 14; -fx-text-fill: #2D3748; -fx-background-color: #FEFCBF; -fx-background-radius: 8; -fx-padding: 10;");
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(300);
            Tooltip.install(node, tooltip);
        }
    }

    //filter data log table
    private void filterHistoryLogTable() {
        String searchText = historySearchField.getText().toLowerCase();
        String searchPatient = historyPatientSearchField.getText().toLowerCase();
        String calcType = historyCalcTypeField.getText().toLowerCase();
        String searchYear = historyDateField.getText().toLowerCase();

        ObservableList<DataLog> filteredList = FXCollections.observableArrayList();

        for (DataLog data : dataLogs) {
            boolean matchesSearch = data.getUsername().toLowerCase().contains(searchText);
            boolean matchesYearFilter = data.getCalculation_date().toLowerCase().contains(searchYear);
            boolean matchesPatient = data.getPatient_id().toLowerCase().contains(searchPatient);
            boolean matchesType = data.getCalculation_type().toLowerCase().contains(calcType);

            if (matchesSearch && matchesYearFilter && matchesType && matchesPatient) {
                filteredList.add(data);
            }
        }

        CalculationHistoryTable.setItems(filteredList);
    }

    private void backToHome(ActionEvent event) {
        SceneManager.getInstance().showHome();
    }

    @FXML
    private void reloadCalculations(ActionEvent event) {
        dataLogs = FXCollections.observableArrayList(fetchUserLogs(user.getUsername()));
        CalculationHistoryTable.setItems(dataLogs);
        

    }

    //Animation methods
    private void fadeInContent(Node node) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), node);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void animate(Node node) {
        MainPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                fadeInContent(newTab.getContent());
            }
        });
    }

    @FXML
    private void performQACalculation(ActionEvent event) {
        // Quality Assurance Calculations
        try {
            final double PR = 101.32;
            final double TR = 20;
            final double CR = 12.32;

            // getting user inputs
            // temperature
            double temp1 = Double.parseDouble(T1.getText());
            double temp2 = Double.parseDouble(T2.getText());
            // pressure
            double pressure1 = Double.parseDouble(P1.getText());
            double pressure2 = Double.parseDouble(P2.getText());
            // neg3 values
            double Neg31 = Double.parseDouble(neg31.getText());
            double Neg32 = Double.parseDouble(neg32.getText());
            // Zero values
            double Zero_1 = Double.parseDouble(zero1.getText());
            double Zero_2 = Double.parseDouble(zero2.getText());
            //pos3 values
            double Pos3_1 = Double.parseDouble(pos31.getText());
            double Pos3_2 = Double.parseDouble(pos32.getText());

            // calculations
            double Temp_Avg = (temp1 + temp2) / 2;
            double Pres_Avg = (pressure1 + pressure2) / 2;
            double Neg3_Avg = (Neg31 + Neg32) / 2;
            double Zero_Avg = (Zero_1 + Zero_2) / 2;
            double Pos3_Avg = (Pos3_1 + Pos3_2) / 2;
            double calc1 = (273.2 + Temp_Avg) / (273.2 + TR);
            double calc2 = (PR / Pres_Avg);
            double calc3 = calc1 * calc2;
            double C3 = (calc3 * Pos3_Avg);
            double Co = (calc3 * Zero_Avg);
            double C_o = Double.parseDouble(String.format("%.2f", Co));
            double CNeg = (calc3 * Neg3_Avg);
            double PE = ((CR - C_o) / CR) * 100;

            // Final answers
            String tempValues = String.valueOf(temp1) + "," + String.valueOf(temp2);
            String pressureValues = String.valueOf(pressure1) + "," + String.valueOf(pressure2);
            String neg3Values = String.valueOf(Neg31) + "," + String.valueOf(Neg32);
            String zeroValues = String.valueOf(Zero_1) + "," + String.valueOf(Zero_2);
            String pos3Values = String.valueOf(Pos3_1) + "," + String.valueOf(Pos3_2);
            String negC3Value = String.format("%.2f", C3);
            String c_zeroValues = String.format("%.2f", Co);
            String posC3Values = String.format("%.2f", CNeg);
            String errorValue = String.format("%.2f", PE);
            // Error results styling

            if (Math.abs(PE) <= 3.0) {
                ERROR.getStyleClass().remove("qa-result-fail");
                ERROR.getStyleClass().add("qa-result-pass");
            } else {
                ERROR.getStyleClass().remove("qa-result-pass");
                ERROR.getStyleClass().add("qa-result-fail");
            }

            // log LINAC data
            LinacData linacData = new LinacData(this.user.getUsername(), tempValues, pressureValues,
                    neg3Values, zeroValues,
                    pos3Values, negC3Value,
                    c_zeroValues, posC3Values, errorValue, qaDateField.getText());

            DataLogger dataLog = new DataLogger(linacData);
            dataLog.LogQA();
            c3.setText(String.format("%.2f", C3));
            c0.setText(String.format("%.2f", Co));
            negC.setText(String.format("%.2f", CNeg));
            ERROR.setText(String.format("%.2f", PE) + "%");
        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Input Error", "An error ocurred!\n Make sure all inputs are numerical values and not null.");

        }

    }

    @FXML
    private void printQA(ActionEvent event) {
        VBox content = new VBox(12);
        content.setStyle("-fx-font-size: 16;");

        content.getChildren().addAll(
                createPrintRow("QA Date", getTextOrNA(qaDateField)),
                createPrintRow("Temperature 1 (°C)", getTextOrNA(T1)),
                createPrintRow("Temperature 2 (°C)", getTextOrNA(T2)),
                createPrintRow("Pressure 1 (mmHg)", getTextOrNA(P1)),
                createPrintRow("Pressure 2 (mmHg)", getTextOrNA(P2)),
                new Label(""),
                createPrintRow("Output -3 cm (1)", getTextOrNA(neg31)),
                createPrintRow("Output -3 cm (2)", getTextOrNA(neg32)),
                createPrintRow("Central Axis (1)", getTextOrNA(zero1)),
                createPrintRow("Central Axis (2)", getTextOrNA(zero2)),
                createPrintRow("Output +3 cm (1)", getTextOrNA(pos31)),
                createPrintRow("Output +3 cm (2)", getTextOrNA(pos32)),
                new Label(""),
                createPrintRow("Crossplane Negative", getTextOrNA(negC)),
                createPrintRow("Crossplane Central", getTextOrNA(c0)),
                createPrintRow("Crossplane Positive", getTextOrNA(c3)),
                new Label(""),
                createPrintRowBold("Daily Output Error", getTextOrNA(ERROR))
        );

        printNode(content, "LINAC Daily QA Report");
    }

    public void showPatientIdDialog() {
        PatientIdDialog dialog = new PatientIdDialog();
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(patientId -> {
            Session.setPatientId(patientId);
        });

    }

    // Method for calculating treatment time SAD Open field
    @FXML
    private void sadOpenFieldCalculation(ActionEvent event) {
        try {

            // Get values
            double X = Double.parseDouble(sadnb_x.getText());
            double Y = Double.parseDouble(sadnb_y.getText());
            double dose = Double.parseDouble(sadnb_dose.getText());
            // TODO :  Check if depth is not a decimal value
            String sadnbDepth = sadnb_depth.getText();
            if (!checkDepth(sadnbDepth)) {
                return;
            }

            // Calculate equivalent field size
            double equivalent_field_size = EquivalentFieldSize(X, Y);
            if (!checkFieldSize(equivalent_field_size)) {
                return;
            }

            // GET SCP
            double sadnbSCP = scpValue(equivalent_field_size, SCP_SHEET);

            // GET TMR
            double sadnbTMR;
            sadnbTMR = Double.parseDouble(toDP(lookupValue(equivalent_field_size, sadnbDepth, TMR_SHEET), "4"));

            //Calculate DMAX
            double sadnbDmax = Double.parseDouble(toDP(calculate_dmax((float) dose, (float) sadnbTMR), "4"));

            //Calculate TIME
            double sadnbTime = calculate_time(sadnbDmax, sadnbSCP);

            //showPatientIdDialog();
            CalculationValues.append("X = ").append(X).append("\n");
            CalculationValues.append("Y = ").append(Y).append("\n");
            CalculationValues.append("Dose = ").append(dose).append("\n");
            CalculationValues.append("Depth = ").append(sadnbDepth).append("\n");
            CalculationValues.append("Equivalent Field Size = ").append(toDP(equivalent_field_size, "1")).append("\n");
            CalculationValues.append("SCP = ").append(toDP(sadnbSCP, "4")).append("\n");
            CalculationValues.append("TMR = ").append(toDP(sadnbTMR, "4")).append("\n");
            CalculationValues.append("Dmax = ").append(toDP(sadnbDmax, "4")).append("\n");
            CalculationValues.append("Time = ").append(toDP(sadnbTime, "4")).append("\n");

            // Create table for LINAC
            DataLogger dataLog = new DataLogger(user.username, Session.getPatientId(), "Surface Axis Distance No Block  Calculation", CalculationValues.toString());
            dataLog.LogData();
            CalculationValues = new StringBuilder();

            // TODO: display results
            sadnb_eqfs.setText(toDP(equivalent_field_size, "1"));
            sadnb_scp.setText(toDP(sadnbSCP, "4"));
            sadnb_tmr.setText(String.valueOf(sadnbTMR));
            sadnb_dmax.setText(toDP(sadnbDmax, "4"));
            sadnb_time.setText(toDP(sadnbTime, "2"));

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Input Error", "An error ocurred!\n Make sure all inputs are numerical values and not null.");

        }

    }

    // Method for calculating treatment time SAD blocked field
    @FXML
    private void sadBlockedFieldCalculation(ActionEvent event) {
        try {

            // TODO: Work on input validations for all methods
            if (sadbFactors.getSelectedToggle() == null) {
                showAlert(AlertType.WARNING, "No Factor Selected", "Please select Block Factor.");
                return;
            }
            // set block factor
            Node blockBtn = (Node) sadbFactors.getSelectedToggle();
            String block = blockBtn.getId();

            if (block.equals("sadb_trayfac")) {
                SADBFACTOR = 0.957;
            } else if (block.equals("sadb_belly ")) {
                SADBFACTOR = 0.978;
            } else {
                SADBFACTOR = 1;
            }

            // Get values
            double X = Double.parseDouble(sadb_x.getText());
            double Y = Double.parseDouble(sadb_y.getText());
            double dose = Double.parseDouble(sadb_dose.getText());
            double area = Double.parseDouble(sadb_area.getText());

            // TODO :  Check if depth is not a decimal value
            String sadbDepth = sadb_depth.getText();
            if (!checkDepth(sadbDepth)) {
                return;
            }

            // Calculate equivalent field size
            double equivalent_field_size = EquivalentFieldSize(X, Y);
            if (!checkFieldSize(equivalent_field_size)) {
                return;
            }
            double reducedfield = ReducedFieldSize(X, Y, area);
            // GET SCP
            //get sc and sp and multiply
            double sadbSCP = 0;
            // Scatter from collimator
            double sadbSC = scatterValue(equivalent_field_size, SCP_SHEET, "sc");
            double sadbSP = scatterValue(equivalent_field_size, SCP_SHEET, "sp");
            // Calculate SCP
            sadbSCP = sadbSC * sadbSP;

            // TODO : GET TMR
            double sadbTMR;
            sadbTMR = Double.parseDouble(toDP(lookupValue(reducedfield, sadbDepth, TMR_SHEET), "4"));

            // TODO: Calculate DMAX
            double sadbDmax = Double.parseDouble(toDP(calculate_dmax((float) dose, (float) sadbTMR), "4"));

            // TODO : Calculate TIME
            double sadbTime = calculate_time(sadbDmax, (sadbSCP * SADBFACTOR));

            // log results
            //showPatientIdDialog();
            CalculationValues.append("X = ").append(X).append("\n");
            CalculationValues.append("Y = ").append(Y).append("\n");
            CalculationValues.append("Dose = ").append(dose).append("\n");
            CalculationValues.append("Depth = ").append(area).append("\n");
            CalculationValues.append("Area = ").append(sadbDepth).append("\n");
            CalculationValues.append("Reduced Field = ").append(reducedfield).append("\n");
            CalculationValues.append("Equivalent Field Size = ").append(toDP(equivalent_field_size, "1")).append("\n");
            CalculationValues.append("SADBFACTOR = ").append(SADBFACTOR).append("\n");
            CalculationValues.append("SCP = ").append(toDP(sadbSCP, "4")).append("\n");
            CalculationValues.append("TMR = ").append(toDP(sadbTMR, "4")).append("\n");
            CalculationValues.append("Dmax = ").append(toDP(sadbDmax, "4")).append("\n");
            CalculationValues.append("Time = ").append(toDP(sadbTime, "4")).append("\n");

            DataLogger dataLog = new DataLogger(user.username, Session.getPatientId(), "Surface Axis Distance With Block  Calculation", CalculationValues.toString());
            dataLog.LogData();
            CalculationValues = new StringBuilder();
            Session.setPatientId("None");

            // Display result
            sadb_eqfs.setText(toDP(equivalent_field_size, "1"));
            sadb_reduced.setText(toDP(reducedfield, "1"));
            sadb_scp.setText(toDP(sadbSCP, "4"));
            sadb_tmr.setText(String.valueOf(sadbTMR));
            sadb_dmax.setText(toDP(sadbDmax, "4"));
            sadb_time.setText(toDP(sadbTime, "2"));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Input Error", "An error ocurred!\n Make sure all inputs are numerical values and not null.");

        }
    }

    // Method for calculating treatment time SSD Blocked field
    @FXML
    private void ssdBlockedFieldCalculation(ActionEvent event) {

        try {

            if (ssdFactors.getSelectedToggle() == null) {
                showAlert(AlertType.WARNING, "No Factor Selected", "Please select Block Factor.");
                return;
            }

            // set block factor
            Node blockBtn = (Node) ssdFactors.getSelectedToggle();
            String block = blockBtn.getId();

            if (block.equals("ssdb_trayfac")) {
                SSDBFACTOR = 0.957;
            } else if (block.equals("ssdb_belly ")) {
                SSDBFACTOR = 0.978;
            } else {
                SSDBFACTOR = 1;
            }

            // Get values
            double X = Double.parseDouble(ssdb_x.getText());
            double Y = Double.parseDouble(ssdb_y.getText());
            double dose = Double.parseDouble(ssdb_dose.getText());
            double area = Double.parseDouble(ssdb_area.getText());

            // TODO :  Check if depth is not a decimal value
            String ssdbDepth = ssdb_depth.getText();
            if (!checkDepth(ssdbDepth)) {
                return;
            }
            // Calculate equivalent field size
            double equivalent_field_size = EquivalentFieldSize(X, Y);
            if (!checkFieldSize(equivalent_field_size)) {
                return;
            }

            double reducedfield = ReducedFieldSize(X, Y, area);

            // GET SCP
            //get sc and sp and multiply
            double ssdbSCP = 0;
            // Scatter from collimator
            double ssdbSC = scatterValue(equivalent_field_size, SCP_SHEET, "sc");
            double ssdbSP = scatterValue(reducedfield, SCP_SHEET, "sp");
            //Scatter from patient
            // Calculate SCP
            ssdbSCP = ssdbSC * ssdbSP;

            // TODO : GET TMR
            double ssdbPDD;
            ssdbPDD = Double.parseDouble(toDP(lookupValue(reducedfield, ssdbDepth, PDD_SHEET), "4"));

            // TODO: Calculate DMAX
            double ssdbDmax = Double.parseDouble(toDP(calculate_dmax((float) dose, (float) ssdbPDD), "4")) * 100;

            // TODO : Calculate TIME
            double ssdbTime = calculate_time(ssdbDmax, (ssdbSCP * SSDBFACTOR));

            // log results
            //showPatientIdDialog();
            CalculationValues.append("X = ").append(X).append("\n");
            CalculationValues.append("Y = ").append(Y).append("\n");
            CalculationValues.append("Dose = ").append(dose).append("\n");
            CalculationValues.append("Depth = ").append(area).append("\n");
            CalculationValues.append("Area = ").append(ssdbDepth).append("\n");
            CalculationValues.append("Reduced Field = ").append(reducedfield).append("\n");
            CalculationValues.append("Equivalent Field Size = ").append(toDP(equivalent_field_size, "1")).append("\n");
            CalculationValues.append("SCP = ").append(toDP(ssdbSCP, "4")).append("\n");
            CalculationValues.append("SSDBFACTOR = ").append(SSDBFACTOR).append("\n");
            CalculationValues.append("PDD = ").append(toDP(ssdbPDD, "4")).append("\n");
            CalculationValues.append("Dmax = ").append(toDP(ssdbDmax, "4")).append("\n");
            CalculationValues.append("Time = ").append(toDP(ssdbTime, "4")).append("\n");

            DataLogger dataLog = new DataLogger(user.username, Session.getPatientId(), "Surface Skin Distance Block  Calculation", CalculationValues.toString());
            dataLog.LogData();
            CalculationValues = new StringBuilder();
            Session.setPatientId("None");

            // display results
            ssdb_eqfs.setText(String.valueOf(toDP(equivalent_field_size, "1")));
            ssdb_reduced.setText(String.valueOf(toDP(reducedfield, "1")));
            ssdb_scp.setText(String.valueOf(toDP(ssdbSCP, "4")));
            ssdb_pdd.setText(String.valueOf(toDP(ssdbPDD, "4")));
            ssdb_dmax.setText(String.valueOf(toDP(ssdbDmax, "4")));
            ssdb_time.setText(String.valueOf(toDP(ssdbTime, "2")));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Input Error", "An error ocurred!\n Make sure all inputs are numerical values and not null.");

        }
    }

    // Method for calculating treatment time SSD Open field
    @FXML
    private void ssdOpenFieldCalculation(ActionEvent event) {
        try {

            // Get values
            double X = Double.parseDouble(ssdnb_x.getText());
            double Y = Double.parseDouble(ssdnb_y.getText());
            double dose = Double.parseDouble(ssdnb_dose.getText());
            // TODO :  Check if depth is not a decimal value
            String ssdnbDepth = ssdnb_depth.getText();
            if (!checkDepth(ssdnbDepth)) {
                return;
            }
            // Calculate equivalent field size
            double equivalent_field_size = EquivalentFieldSize(X, Y);
            if (!checkFieldSize(equivalent_field_size)) {
                return;
            }

            // GET SCP
            double ssdnbSCP = scpValue(equivalent_field_size, SCP_SHEET);

            // TODO : GET PDD
            double ssdnbPDD;
            double pdd = lookupValue(equivalent_field_size, ssdnbDepth, PDD_SHEET);
            ssdnbPDD = Double.parseDouble(toDP(pdd, "4"));

            // TODO: Calculate DMAX
            double ssdnbDmax = Double.parseDouble(toDP(calculate_dmax((float) dose, (float) ssdnbPDD), "4")) * 100;

            // TODO : Calculate TIME
            double ssdnbTime = calculate_time(ssdnbDmax, ssdnbSCP);

            // log results
            //showPatientIdDialog();
            CalculationValues.append("X = ").append(X).append("\n");
            CalculationValues.append("Y = ").append(Y).append("\n");
            CalculationValues.append("Dose = ").append(dose).append("\n");
            CalculationValues.append("Depth = ").append(ssdnbDepth).append("\n");
            CalculationValues.append("Equivalent Field Size = ").append(toDP(equivalent_field_size, "1")).append("\n");
            CalculationValues.append("SCP = ").append(toDP(ssdnbSCP, "4")).append("\n");
            CalculationValues.append("PDD = ").append(toDP(ssdnbPDD, "4")).append("\n");
            CalculationValues.append("Dmax = ").append(toDP(ssdnbDmax, "4")).append("\n");
            CalculationValues.append("Time = ").append(toDP(ssdnbTime, "4")).append("\n");

            DataLogger dataLog = new DataLogger(user.username, Session.getPatientId(), "Surface Skin Distance No Block  Calculation", CalculationValues.toString());
            dataLog.LogData();
            CalculationValues = new StringBuilder();
            Session.setPatientId("None");

            // TODO: display results
            ssdnb_eqfs.setText(String.valueOf(toDP(equivalent_field_size, "1")));
            ssdnb_scp.setText(String.valueOf(ssdnbSCP));
            ssdnb_pdd.setText(String.valueOf(ssdnbPDD));
            ssdnb_dmax.setText(String.valueOf(toDP(ssdnbDmax, "4")));
            ssdnb_time.setText(toDP(ssdnbTime, "2"));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Input Error", "An error ocurred!\n Make sure all inputs are numerical values and not null.");

        }

    }

    // Method for calculating treatment time SAD WEDGE field
    @FXML
    private void sadOpenWedgeFieldCalculation(ActionEvent event) {
        try {
            if (wedgeFactors.getSelectedToggle() == null) {
                showAlert(AlertType.WARNING, "No Wedge Selected", "Please select Wedge");
                return;
            }
            // set wedge factor
            Node wedgeBtn = (Node) wedgeFactors.getSelectedToggle();
            String wedge = wedgeBtn.getId();

            if (wedge.equals("sadnb_w151")) {
                SADWEDGE = 0.767;
            } else if (wedge.equals("sadnb_w301")) {
                SADWEDGE = 0.636;
            } else if (wedge.equals("sadnb_w451")) {
                SADWEDGE = 0.487;
            } else if (wedge.equals("sadnb_w601")) {
                SADWEDGE = 0.261;
            } else {
                SADWEDGE = 1;
            }

            // Get values
            double X = Double.parseDouble(sadnbw_x.getText());
            double Y = Double.parseDouble(sadnbw_y.getText());
            double dose = Double.parseDouble(sadnbw_dose.getText());
            // TODO :  Check if depth is not a decimal value
            String sadnbwDepth = sadnbw_depth.getText();
            if (!checkDepth(sadnbwDepth)) {
                return;
            }
            // Calculate equivalent field size
            double equivalent_field_size = EquivalentFieldSize(X, Y);
            if (!checkFieldSize(equivalent_field_size)) {
                return;
            }
            // GET SCP

            double sadnbwSCP = scpValue(equivalent_field_size, SCP_SHEET);

            // TODO : GET TMR
            double sadnbwTMR;
            sadnbwTMR = Double.parseDouble(toDP(lookupValue(equivalent_field_size, sadnbwDepth, TMR_SHEET), "4"));

            // TODO: Calculate DMAX
            double sadnbwDmax = Double.parseDouble(toDP(calculate_dmax((float) dose, (float) sadnbwTMR), "4"));

            // TODO : Calculate TIME
            double sadnbwTime = calculate_time(sadnbwDmax, (sadnbwSCP * SADWEDGE));

            // log results
            //showPatientIdDialog();
            CalculationValues.append("X = ").append(X).append("\n");
            CalculationValues.append("Y = ").append(Y).append("\n");
            CalculationValues.append("Dose = ").append(dose).append("\n");
            CalculationValues.append("Depth = ").append(sadnbwDepth).append("\n");
            CalculationValues.append("Equivalent Field Size = ").append(toDP(equivalent_field_size, "1")).append("\n");
            CalculationValues.append("SCP = ").append(toDP(sadnbwSCP, "4")).append("\n");
            CalculationValues.append("Wedge value = ").append(SADWEDGE).append("\n");
            CalculationValues.append("TMR = ").append(toDP(sadnbwTMR, "4")).append("\n");
            CalculationValues.append("Dmax = ").append(toDP(sadnbwDmax, "4")).append("\n");
            CalculationValues.append("Time = ").append(toDP(sadnbwTime, "4")).append("\n");

            DataLogger dataLog = new DataLogger(user.username, Session.getPatientId(), "Surface Axis Distance Wedge Calculation", CalculationValues.toString());
            dataLog.LogData();
            CalculationValues = new StringBuilder();

            // TODO: display results
            sadnbw_eqfs.setText(String.valueOf(toDP(equivalent_field_size, "1")));
            sadnbw_scp.setText(String.valueOf(sadnbwSCP));
            sadnbw_tmr.setText(String.valueOf(sadnbwTMR));
            sadnbw_dmax.setText(String.valueOf(sadnbwDmax));
            sadnbw_time.setText(toDP(sadnbwTime, "2"));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Input Error", "An error ocurred!\n Make sure all inputs are numerical values and not null.");
        }
    }

    @FXML
    private void logOutUser(ActionEvent event) {
        //Todo : Clear all session data and load home page
        Session.setUser(null);
        Session.setPatientId(null);
        backToHome(event);
    }

    @FXML
    private void clearQATab(ActionEvent event) {
        T1.clear();
        T2.clear();
        P1.clear();
        P2.clear();
        c0.clear();
        c3.clear();
        negC.clear();
        neg31.clear();
        neg32.clear();
        pos31.clear();
        pos32.clear();
        ERROR.clear();
        zero1.clear();
        zero2.clear();
    }

    @FXML
    private void clearSADNB(ActionEvent event) {
        sadnb_x.clear();
        sadnb_y.clear();
        sadnb_eqfs.clear();
        sadnb_scp.clear();
        sadnb_depth.clear();
        sadnb_dmax.clear();
        sadnb_dose.clear();
        sadnb_tmr.clear();
        sadnb_time.clear();
    }

    @FXML
    private void clearSADBlock(ActionEvent event) {
        sadb_area.clear();
        sadb_reduced.clear();
        sadb_belly.setSelected(false);
        sadb_nofac.setSelected(false);
        sadb_trayfac.setSelected(false);
        SADBFACTOR = 1;
        sadb_y.clear();
        sadb_x.clear();
        sadb_eqfs.clear();
        sadb_scp.clear();
        sadb_depth.clear();
        sadb_dmax.clear();
        sadb_dose.clear();
        sadb_tmr.clear();
        sadb_time.clear();
    }

    @FXML
    private void clearSSDNB(ActionEvent event) {
        ssdnb_x.clear();
        ssdnb_y.clear();
        ssdnb_eqfs.clear();
        ssdnb_scp.clear();
        ssdnb_depth.clear();
        ssdnb_dmax.clear();
        ssdnb_dose.clear();
        ssdnb_pdd.clear();
        ssdnb_time.clear();
    }

    @FXML
    private void clearSSDB(ActionEvent event) {
        ssdb_area.clear();
        ssdb_reduced.clear();
        ssdb_belly.setSelected(false);
        ssdb_nofac.setSelected(false);
        ssdb_trayfac.setSelected(false);
        SSDBFACTOR = 1;
        ssdb_y.clear();
        ssdb_x.clear();
        ssdb_eqfs.clear();
        ssdb_scp.clear();
        ssdb_depth.clear();
        ssdb_dmax.clear();
        ssdb_dose.clear();
        ssdb_pdd.clear();
        ssdb_time.clear();
    }

    @FXML
    private void clearSADNNBW(ActionEvent event) {
        sadnbw_y.clear();
        sadnbw_x.clear();
        sadnbw_eqfs.clear();
        sadnbw_scp.clear();
        sadnbw_depth.clear();
        sadnbw_dmax.clear();
        sadnbw_dose.clear();
        sadnbw_tmr.clear();
        sadnbw_time.clear();
    }

    @FXML
    private void addLinac(MouseEvent event) {
        if (MainPane.getTabs().contains(qaTab) == false) {
            Platform.runLater(() -> {
                System.out.println("Linac tab isnt present, adding it ...");
            qaDateField.setText(LocalDate.now().toString());
            MainPane.getTabs().add(qaTab);
            qaTab.getContent().setFocusTraversable(true);
            });
            
        } else {
            MainPane.getSelectionModel().select(qaTab);
            qaTab.getContent().setFocusTraversable(true);
        }
    }

    @FXML
    private void addSADNB(MouseEvent event) {
        if (MainPane.getTabs().contains(SADNB) == false) {
            MainPane.getTabs().add(SADNB);
            SADNB.getContent().setFocusTraversable(true);
        } else {
            MainPane.getSelectionModel().select(SADNB);
            SADNB.getContent().setFocusTraversable(true);

        }
    }

    @FXML
    private void addSADB(MouseEvent event) {
        if (MainPane.getTabs().contains(SADB) == false) {
            MainPane.getTabs().add(SADB);
            SADB.getContent().setFocusTraversable(true);
        } else {
            MainPane.getSelectionModel().select(SADB);
            SADB.getContent().setFocusTraversable(true);
        }
    }

    @FXML
    private void addSSDNB(MouseEvent event) {
        if (event.getClickCount() == 2) {
            System.out.println("Clicked twice ..");
        }
        if (MainPane.getTabs().contains(SSDNB) == false) {
            MainPane.getTabs().add(SSDNB);
            SSDNB.getContent().setFocusTraversable(true);
        } else {
            MainPane.getSelectionModel().select(SSDNB);
            SSDNB.getContent().setFocusTraversable(true);
        }
    }

    @FXML
    private void addSSDB(MouseEvent event) {
        if (MainPane.getTabs().contains(SSDB) == false) {
            MainPane.getTabs().add(SSDB);
            SSDB.getContent().setFocusTraversable(true);
        } else {
            MainPane.getSelectionModel().select(SSDB);
            SSDB.getContent().setFocusTraversable(true);
        }
    }

    @FXML
    private void addSADNBW(MouseEvent event) {
        if (MainPane.getTabs().contains(SADNBW) == false) {
            MainPane.getTabs().add(SADNBW);
            SADNBW.getContent().setFocusTraversable(true);
        } else {
            MainPane.getSelectionModel().select(SADNBW);
            SADNBW.getContent().setFocusTraversable(true);
        }
    }

    //VARIABLES
    // Main Pane
    @FXML
    private TabPane MainPane;

    // HOME TAB
    @FXML
    private Tab Home;

    // QA TAB
    @FXML
    private Tab qaTab;
    @FXML
    private TextField T1;
    @FXML
    private TextField T2;
    @FXML
    private TextField P1;
    @FXML
    private TextField P2;
    @FXML
    private TextField neg31;
    @FXML
    private TextField neg32;
    @FXML
    private TextField zero1;
    @FXML
    private TextField zero2;
    @FXML
    private TextField pos31;
    @FXML
    private TextField pos32;
    @FXML
    private TextField negC;
    @FXML
    private TextField c0;
    @FXML
    private TextField c3;
    @FXML
    private TextField ERROR;

    // SAD NO BLOCK TAB
    @FXML
    private Tab SADNB;
    @FXML
    private TextField sadnb_x;
    @FXML
    private TextField sadnb_y;
    @FXML
    private TextField sadnb_depth;
    @FXML
    private TextField sadnb_dose;
    @FXML
    private TextField sadnb_eqfs;
    @FXML
    private TextField sadnb_scp;
    @FXML
    private TextField sadnb_tmr;
    @FXML
    private TextField sadnb_dmax;
    @FXML
    private TextField sadnb_time;

    // SAD BLOCK TAB
    @FXML
    private Tab SADB;

    @FXML
    private TextField sadb_x;
    @FXML
    private TextField sadb_y;
    @FXML
    private TextField sadb_depth;
    @FXML
    private TextField sadb_dose;
    @FXML
    private TextField sadb_eqfs;
    @FXML
    private TextField sadb_scp;
    @FXML
    private TextField sadb_tmr;
    @FXML
    private TextField sadb_dmax;
    @FXML
    private TextField sadb_time;
    @FXML
    private TextField sadb_reduced;
    @FXML
    private TextField sadb_area;
    @FXML
    private RadioButton sadb_trayfac;
    @FXML
    private RadioButton sadb_belly;
    @FXML
    private RadioButton sadb_nofac;

    // SSD BLOCK TAB
    @FXML
    private Tab SSDB;
    @FXML
    private TextField ssdb_x;
    @FXML
    private TextField ssdb_y;
    @FXML
    private TextField ssdb_depth;
    @FXML
    private TextField ssdb_dose;
    @FXML
    private TextField ssdb_eqfs;
    @FXML
    private TextField ssdb_scp;
    @FXML
    private TextField ssdb_pdd;
    @FXML
    private TextField ssdb_dmax;
    @FXML
    private TextField ssdb_time;
    @FXML
    private TextField ssdb_reduced;
    @FXML
    private TextField ssdb_area;
    @FXML
    private RadioButton ssdb_trayfac;
    @FXML
    private RadioButton ssdb_belly;
    @FXML
    private RadioButton ssdb_nofac;

    // SSD NO BLOCK TAB
    @FXML
    private Tab SSDNB;
    @FXML
    private TextField ssdnb_x;
    @FXML
    private TextField ssdnb_y;
    @FXML
    private TextField ssdnb_depth;
    @FXML
    private TextField ssdnb_dose;
    @FXML
    private TextField ssdnb_eqfs;
    @FXML
    private TextField ssdnb_scp;
    @FXML
    private TextField ssdnb_pdd;
    @FXML
    private TextField ssdnb_dmax;
    @FXML
    private TextField ssdnb_time;

    // SAD NO BLOCK WEDGE
    @FXML
    private Tab SADNBW;
    @FXML
    private TextField sadnbw_x;
    @FXML
    private TextField sadnbw_y;
    @FXML
    private TextField sadnbw_depth;
    @FXML
    private TextField sadnbw_dose;
    @FXML
    private TextField sadnbw_eqfs;
    @FXML
    private TextField sadnbw_scp;
    @FXML
    private TextField sadnbw_tmr;
    @FXML
    private TextField sadnbw_dmax;
    @FXML
    private TextField sadnbw_time;

    @FXML
    private RadioButton sadnb_w301;
    @FXML
    private RadioButton sadnb_w151;
    @FXML
    private RadioButton sadnb_w451;
    @FXML
    private RadioButton sadnb_w601;

}
