package sample;


import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class NumFieldFX {
    /**
     * This handles number fields in the GUI
     * @param tf
     */
    public void numField(TextField tf){
        tf.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
            public void handle( KeyEvent t ) {
                char ar[] = t.getCharacter().toCharArray();
                char ch = ar[t.getCharacter().toCharArray().length - 1];
                if (!(ch >= '0' && ch <= '9' || ch == '.' || ch == '-')) {
                    ///   System.out.println("The char you entered is not a number");
                    t.consume();
                }
            }
        });
    }
}