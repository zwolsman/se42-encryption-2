package javaeo2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Marvin
 */
public class JavaEO2 extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(FXMLDocumentController.class.getResourceAsStream("FXMLDocument.fxml"));
        final FXMLDocumentController controller = (FXMLDocumentController) loader.getController();
        stage.addEventHandler(WindowEvent.WINDOW_SHOWN, (WindowEvent window) -> {
            //Generate random salt after startup
            controller.handleGenerateAction(null);
        });
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
//
//        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
//
//        Scene scene = new Scene(root);
//
//        stage.setScene(scene);
//        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
