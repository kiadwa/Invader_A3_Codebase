package invaders.mementoUndo;

public class TimeObserverMemento {
    private int millis;
    private int second;
    private int minute;

    public TimeObserverMemento(int minute, int second, int millis){
        this.minute = minute;
        this.second = second;
        this.millis = millis;
    }
    public int getSecond(){return this.second;}
    public int getMillis(){return this.millis;}

    public int getMinute() {
        return minute;
    }
}
