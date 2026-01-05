package rtpsuite;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;

/**
 *
 * @author OverComer
 */
public class PatientIdDialog extends Dialog<String>{
    public PatientIdDialog(){
        setTitle("Enter PatientID");
        setHeaderText("Please enter the Patient ID : ");
        
        TextField idField = new TextField();
        idField.setPromptText("Patient ID");
        
        ButtonType submitBtn = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(submitBtn, ButtonType.CANCEL);
        
        getDialogPane().setContent(idField);
        
        setResultConverter(dialogButton -> {
            if(dialogButton == submitBtn){
                return idField.getText();
            }
            return null;
        });
        
    }
}
