package server.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class MyEditCell<S, T> extends TextFieldTableCell<S, T> {

    private TextField textField;

    public MyEditCell(final StringConverter<T> converter) {
        super(converter);
    }

    public static <S, T> Callback<TableColumn<S, T>, TableCell<S, T>> forTableColumn(
            final StringConverter<T> converter) {
        return list -> new MyEditCell<S, T>(converter);
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getTableView().isEditable()
                || !getTableColumn().isEditable()) {
            return;
        }
        super.startEdit();

        if (isEditing()) {
            if (textField == null) {
                textField = createTextField();
            }
            startEdit(textField);
        }
    }

    private TextField createTextField() {
        final TextField textField = new TextField(getItemText());
        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField.setOnAction(event -> {
            if (getConverter() == null) {
                throw new IllegalStateException(
                        "Attempting to convert text input into Object, but provided "
                                + "StringConverter is null. Be sure to set a StringConverter "
                                + "in your cell factory.");
            }
            this.commitEdit(getConverter().fromString(textField.getText()));
            event.consume();
        });
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                this.cancelEdit();
                t.consume();
            }
        });
        //Use focus property
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable,
                                Boolean oldValue, Boolean newValue) {
                if (!newValue) {
                    commitEdit(getConverter().fromString(textField.getText()));
                }
            }
        });

        return textField;
    }

    private String getItemText() {
        return getConverter() == null ? getItem() == null ? ""
                : getItem().toString() : getConverter().toString(getItem());
    }

    private void startEdit(final TextField textField) {
        if (textField != null) {
            textField.setText(getItemText());
        }
        setText(null);
        setGraphic(textField);
        textField.selectAll();

        // requesting focus so that key input can immediately go into the
        // TextField (see RT-28132)
        textField.requestFocus();
    }
}
