package javaeo2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import sun.misc.BASE64Encoder;

/**
 *
 * @author Marvin
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private TextField txtMessage;

    @FXML
    private Label lblSalt;

    private final byte[] salt = new byte[16];
    private SecureRandom random;

    @FXML
    private void handleSaveAction(ActionEvent event) {
        File saveFile = showDialog(true);
        if (saveFile == null) {
            return;
        }

        System.out.println(byteArrayToHex(txtMessage.getText().getBytes()));
        byte[] data = Aes.encrypt(txtPassword.getText().toCharArray(), salt, txtMessage.getText());
        System.out.println(byteArrayToHex(data));

        try (FileOutputStream fos = new FileOutputStream(saveFile)) {
            fos.write(salt);
            fos.write(data);
            fos.close();

            //Show the base64 encrypted message in the textbox
            txtMessage.setText(new BASE64Encoder().encode(data));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void handleOpenAction(ActionEvent event) {
        File openFile = showDialog(false);
        if (openFile == null) {
            return;
        }
        txtMessage.setText("");

        try (FileInputStream fin = new FileInputStream(openFile)) {

            //Read the salt (16 bytes)
            fin.read(salt);

            //Display the salt
            lblSalt.setText(byteArrayToHex(salt));

            //Read the data
            byte[] data = new byte[fin.available()];
            fin.read(data);

            byte[] decrypted = Aes.decrypt(txtPassword.getText().toCharArray(), salt, data);
            if (decrypted == null) {
                System.out.println("O o.. niet t goede wachtwoord");
                txtMessage.setText("<INVALID PASSWORD>");
                return;
            }

            txtMessage.setText(new String(decrypted, "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private File showDialog(boolean saveFile) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Binary File (*.bin)", "*.bin"));
        return saveFile ? chooser.showSaveDialog(lblSalt.getScene().getWindow()) : chooser.showOpenDialog(lblSalt.getScene().getWindow());
    }

    @FXML
    public void handleGenerateAction(ActionEvent event) {
        random.nextBytes(salt);
        lblSalt.setText(byteArrayToHex(salt));
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        random = new SecureRandom();
    }

}
