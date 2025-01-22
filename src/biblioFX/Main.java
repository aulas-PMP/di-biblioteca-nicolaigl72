package biblioFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Cargar la interfaz desde el archivo FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("interfaz.fxml"));
        BorderPane root = loader.load();

        // Crear la escena principal
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // Configurar el escenario principal
        stage.setTitle("Biblioteca Multimedia");
        stage.setScene(scene);
        stage.show();

        // Obtener el controlador y configurar el Stage
        MediaController controller = loader.getController();
        controller.setStage(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}



