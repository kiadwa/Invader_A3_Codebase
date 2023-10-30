package invaders.entities;

import invaders.prototype.EntvPrototype;
import javafx.scene.Node;
import invaders.rendering.Renderable;

public interface EntityView extends EntvPrototype {
    void update(double xViewportOffset, double yViewportOffset);

    boolean matchesEntity(Renderable entity);

    void markForDelete();

    Node getNode();

    boolean isMarkedForDelete();
    Renderable getEntity();
}
