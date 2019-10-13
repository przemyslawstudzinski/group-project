package server.validator;

import com.pixelduke.javafx.validation.ValidatorBase;
import javafx.scene.control.TextInputControl;

import java.util.List;

public class ExistNameValidator extends ValidatorBase {

    private List<String> collection;

    private int attempt = 0;
    boolean withAttempt = false;

    public void setWithAttempt(boolean withAttempt) {
        this.withAttempt = withAttempt;
    }

    public ExistNameValidator() {
    }

    public void setCollection(List<String> collection) {
        this.collection = collection;
    }

    public List<String> getCollection() {
        return collection;
    }

    @Override
    public void eval() {
        this.hasErrors.set(false);
        if (collection != null) {
            if (withAttempt) {
                attempt++;
            }
            TextInputControl textField = (TextInputControl) this.srcControl.get();
            if (collection.contains(textField.getText())) {
                this.hasErrors.set(true);
                if (withAttempt && attempt > 1) {
                    this.hasErrors.set(false);
                    attempt = 0;
                }
            } else {
                this.hasErrors.set(false);
            }
            this.onEval();
        }
    }
}
