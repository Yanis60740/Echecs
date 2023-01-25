package chessproject;
import java.util.EventObject;

public class AutreEvent extends EventObject {
    private Object donnee;
    public AutreEvent(Object source, Object donnee) {
        super(source);
        this.donnee = donnee;
    }
    public Object getDonnee() {
        return this.donnee;
    }
}