package biblioFX;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MediaController {

    @FXML
    private MediaView mediaView;
    @FXML
    private MediaPlayer mediaPlayer;
    @FXML
    private Label fileNameLabel;
    private double originalWidth;
    private double originalHeight;
    private boolean isMinimized = false; // Indica si el MediaView está minimizado

    @FXML
    private Label titleLabel;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private Slider progressSlider;

    private List<File> mediaFiles = new ArrayList<>();
    private Stage primaryStage;

    @FXML
    private ImageView imageView; // Nueva referencia al ImageView para mostrar la imagen

    private static final String AUDIO_PLACEHOLDER_PATH = "file:resources/audio2.png"; // Cambia la ruta si es
                                                                                     // necesario

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

    // Métodos existentes...

    @FXML
    public void handlePlay() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    @FXML
    public void handlePause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @FXML
    public void handleStop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            progressSlider.setValue(0); // Reiniciar la barra de progreso
        }

        fileNameLabel.setText("No hay archivo seleccionado");

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
    public void handleSpeedChange() {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(1.5);
        }
    }

    @FXML
    public void handleSpeedChange2() {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(1);
        }
    }

    @FXML
    public void openFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos Multimedia", "*.mp4", "*.mp3"));
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            mediaFiles.add(file);
            fileListView.getItems().add(file.getName());
        }
    }

    @FXML
    public void handleFileSelection() {
        int selectedIndex = fileListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            File selectedFile = mediaFiles.get(selectedIndex);

            boolean isAudio = selectedFile.getName().endsWith(".mp3");

            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }

            Media media = new Media(selectedFile.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

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
    }

    // Nueva funcionalidad para "Biblioteca"
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

    // Nueva funcionalidad para "Ver"
    @FXML
    public void toggleFullScreen() {
        if (primaryStage != null) {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
        }
    }

    // Nueva funcionalidad para "Acerca"
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
    private VBox videoEditorPanel; 

    @FXML
    private VBox libraryPanel; 

    @FXML
    private Button toggleLeftPanelButton; 

    @FXML
    private Button toggleRightPanelButton; 

    private boolean isLeftPanelVisible = true;
    private boolean isRightPanelVisible = true;

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

}
