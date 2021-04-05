package com.koerriva.bugbrain.engine.scene;

import com.koerriva.bugbrain.engine.graphics.Window;
import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import com.koerriva.bugbrain.engine.graphics.Material;
import com.koerriva.bugbrain.engine.graphics.Mesh;
import org.joml.Vector2f;

public abstract class GameObject {
    public final Vector2f position;
    public final Vector2f size;
    public final float rotation = 0f;
    public final Transform transform = new Transform();

    protected boolean isInstance = false;
    protected Mesh mesh;
    public final Material material;

    public GameObject(Vector2f position, Vector2f size, Material material) {
        this.mesh = Mesh.QUAD("quad");
        this.position = position;
        this.size = size;
        this.material = material;
        this.transform.setTranslation(position);
        this.transform.setScaling(size);
    }

    public abstract void input(Window window);

    public abstract void update(float deltaTime);

    public abstract void draw(Camera2D camera);

    public abstract void cleanup();
}
