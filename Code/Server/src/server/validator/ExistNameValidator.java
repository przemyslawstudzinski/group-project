package server.validator;

import com.pixelduke.javafx.validation.ValidatorBase;
import javafx.scene.control.TextInputControl;

import java.util.List;

public class ExistNameValidator extends ValidatorBase {

    List<String> colection;

    public ExistNameValidator() {
    }

    public void setColection(List<String> colection) {
        this.colection = colection;
    }

    public List<String> getColection() {
        return colection;
    }

    @Override
    public void eval() {
        if (colection != null) {
            TextInputControl textField = (TextInputControl) this.srcControl.get();
            if (colection.contains(textField.getText())) {
                this.hasErrors.set(true);
            } else {
                this.hasErrors.set(false);
            }
            this.onEval();
        }
    }
}
