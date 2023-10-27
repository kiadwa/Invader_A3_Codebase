package invaders.mementoUndo;

public interface Originator {
    GameMemento save();
    void restore(GameMemento memento);
}
