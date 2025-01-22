package biblioFX;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
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
    private MediaPlayer mediaPlayer;

    @FXML
    private Label titleLabel;

    @FXML
    private ListView<String> fileListView;

    @FXML
    private Slider progressSlider;

    private List<File> mediaFiles = new ArrayList<>();
    private Stage primaryStage;

    public void setStage(Stage stage) {
        this.primaryStage = stage;
    }

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
    }

    @FXML
    public void handleResize() {
        // Lógica para cambiar tamaño manteniendo el ratio
        if (mediaView != null && mediaView.getMediaPlayer() != null) {
            mediaView.setFitWidth(400); // Ejemplo de ajuste manual
        }
    }

    @FXML
    public void handleSpeedChange() {
        if (mediaPlayer != null) {
            mediaPlayer.setRate(1.5); // Cambiar velocidad de reproducción
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
            Media media = new Media(selectedFile.toURI().toString());
            if (mediaPlayer != null) {
                mediaPlayer.dispose();
            }
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            titleLabel.setText(selectedFile.getName());

            // Sincronizar barra de progreso
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                progressSlider.setValue(newTime.toSeconds());
            });

            mediaPlayer.setOnReady(() -> {
                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
            });

            // Actualizar tiempo desde el slider
            progressSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (progressSlider.isValueChanging()) {
                    mediaPlayer.seek(javafx.util.Duration.seconds(newValue.doubleValue()));
                }
            });
        }
    }

}
