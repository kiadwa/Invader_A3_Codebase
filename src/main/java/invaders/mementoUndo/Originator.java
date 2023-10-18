package invaders.mementoUndo;

public interface Originator {
    GameEngineMemento save();
    void restore(GameEngineMemento memento);
}
