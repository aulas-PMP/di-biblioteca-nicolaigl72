package biblioFX;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.stage.Popup;
import javafx.util.Duration;

public class MediaController {

    @FXML
    private MediaView mediaView;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private Label fileNameLabel;
    private double originalWidth;
    private double originalHeight;
    private boolean isMinimized = false;

    @FXML
    private Button speedToggleButton;
    private boolean isFastSpeed = false;
    @FXML
    private Label titleLabel;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private Slider progressSlider;

    private List<File> mediaFiles = new ArrayList<>();
    private Stage primaryStage;

    @FXML
    private ImageView imageView;

    @FXML
    private VBox videoEditorPanel;

    @FXML
    private VBox libraryPanel;

    @FXML
    private Button toggleLeftPanelButton;

    @FXML
    private Button toggleRightPanelButton;

    private boolean isLeftPanelVisible = true;
    private boolean isRightPanelVisible = true;

    private static final String AUDIO_PLACEHOLDER_PATH = "file:resources/sound.gif";

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    public void handlePlay() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        } catch (Exception e) {
            showErrorAlert("Error de reproducción", "No se pudo reproducir el archivo.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePause() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            showErrorAlert("Error de pausa", "No se pudo pausar el archivo.");
            e.printStackTrace();
        }
    }

    @FXML
public void handleStop() {
    try {
        if (mediaPlayer != null) {
            progressSlider.setValue(0); 
            mediaPlayer.stop();
            mediaView.setVisible(false);
            imageView.setVisible(false);
        }
        fileNameLabel.setText("No hay archivo seleccionado");
    } catch (Exception e) {
        showErrorAlert("Error de detención", "No se pudo detener el archivo.");
        e.printStackTrace();
    }
}


    @FXML
    public void handleResize() {
        if (mediaView != null && mediaView.getMediaPlayer() != null) {
            if (isMinimized) {
                // Restaurar al tamaño original del video
                mediaView.setFitWidth(originalWidth);
                mediaView.setFitHeight(originalHeight);
                isMinimized = false;
            } else {
                // Minimizar el tamaño
                mediaView.setFitWidth(400);
                mediaView.setFitHeight(300);
                isMinimized = true;
            }
        }
    }

    @FXML
    public void openFile() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos Multimedia", "*.mp4", "*.mp3"));
            File file = fileChooser.showOpenDialog(primaryStage);

            if (file != null) {
                mediaFiles.add(file);
                fileListView.getItems().add(file.getName());

                showFileOpenedNotification(file.getName());
            }
        } catch (Exception e) {
            showErrorAlert("Error al abrir el archivo", "No se pudo abrir el archivo seleccionado.");
            e.printStackTrace();
        }
    }

    private void showFileOpenedNotification(String fileName) {
        try {
            Popup popup = new Popup();
            Label notificationLabel = new Label("📂 Archivo abierto: " + fileName);
            notificationLabel.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-padding: 10px 20px; "
                    + "-fx-font-size: 14px; -fx-border-radius: 10px; -fx-background-radius: 10px; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

            StackPane content = new StackPane(notificationLabel);
            content.setStyle("-fx-background-color: transparent;");
            popup.getContent().add(content);

            // Posicionar en el centro
            double centerX = primaryStage.getX() + primaryStage.getWidth() / 2 - 100;
            double centerY = primaryStage.getY() + primaryStage.getHeight() / 2 - 50;
            popup.setX(centerX);
            popup.setY(centerY);

            popup.show(primaryStage);

            // Agregar animación de desvanecimiento
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), content);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> popup.hide());

            fadeOut.play();
        } catch (Exception e) {
            showErrorAlert("Error de notificación", "No se pudo mostrar la notificación.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleFileSelection() {
        try {
            int selectedIndex = fileListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                File selectedFile = mediaFiles.get(selectedIndex);

                boolean isAudio = selectedFile.getName().endsWith(".mp3");

                if (mediaPlayer != null) {
                    mediaPlayer.dispose();
                }

                Media media = new Media(selectedFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                // Configurar el volumen inicial
                mediaPlayer.setVolume(volumeSlider.getValue());

                if (isAudio) {
                    imageView.setImage(new Image(AUDIO_PLACEHOLDER_PATH));
                    imageView.setVisible(true);
                    mediaView.setVisible(false);
                } else {
                    mediaView.setMediaPlayer(mediaPlayer);
                    mediaView.setVisible(true);
                    imageView.setVisible(false);
                }

                fileNameLabel.setText(selectedFile.getName());

                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!progressSlider.isValueChanging()) {
                        progressSlider.setValue(newTime.toSeconds());
                    }
                });

                progressSlider.setOnMouseReleased(event -> {
                    if (mediaPlayer != null) {
                        mediaPlayer.seek(javafx.util.Duration.seconds(progressSlider.getValue()));
                    }
                });

                mediaPlayer.setOnReady(() -> {
                    progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                });

                mediaPlayer.play();
            }
        } catch (Exception e) {
            showErrorAlert("Error al cargar el archivo", "No se pudo cargar el archivo multimedia.");
            e.printStackTrace();
        }
    }

    @FXML
    public void clearLibrary() {
        mediaFiles.clear();
        fileListView.getItems().clear();
        titleLabel.setText("Título del archivo");
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    @FXML
    public void toggleFullScreen() {
        if (primaryStage != null) {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
        }
    }

    @FXML
    public void toggleSpeed() {
        if (mediaPlayer != null) {
            if (isFastSpeed) {
                mediaPlayer.setRate(1.0); // Velocidad normal
                speedToggleButton.setText("Cambiar velocidad x2");
            } else {
                mediaPlayer.setRate(2.0); // Velocidad rápida
                speedToggleButton.setText("Cambiar velocidad x1");
            }
            isFastSpeed = !isFastSpeed; // Alternar el estado
        }
    }

    @FXML
    public void showAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Acerca de la aplicación");
        alert.setHeaderText("Reproductor Multimedia FX");
        alert.setContentText(
                "Desarrollado por Nico.\nVersión: 1.0\n\nEste reproductor permite gestionar y reproducir archivos multimedia.");
        alert.showAndWait();
    }

    @FXML
    public void toggleLeftPanel() {
        isLeftPanelVisible = !isLeftPanelVisible;
        videoEditorPanel.setVisible(isLeftPanelVisible);
        videoEditorPanel.setManaged(isLeftPanelVisible);
        toggleLeftPanelButton.setText(isLeftPanelVisible ? "⏴" : "⏵");
    }

    @FXML
    public void toggleRightPanel() {
        isRightPanelVisible = !isRightPanelVisible;
        libraryPanel.setVisible(isRightPanelVisible);
        libraryPanel.setManaged(isRightPanelVisible);
        toggleRightPanelButton.setText(isRightPanelVisible ? "⏵" : "⏴");
    }

    @FXML
    private Slider volumeSlider;

    @FXML
    public void initialize() {
        try {
            volumeSlider.setValue(0.5);
            volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(newValue.doubleValue());
                }
            });
        } catch (Exception e) {
            showErrorAlert("Error de inicialización", "No se pudo inicializar el control de volumen.");
            e.printStackTrace();
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
