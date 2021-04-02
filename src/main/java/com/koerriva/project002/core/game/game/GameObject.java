package com.koerriva.project002.core.game.game;

import com.koerriva.project002.core.game.graphic.Camera2D;
import com.koerriva.project002.core.game.graphic.Material;
import com.koerriva.project002.core.game.graphic.Mesh;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public abstract class GameObject {
    public final Vector2f position;
    public final Vector2f size;
    public final Vector2f velocity = new Vector2f(1f);
    public final Matrix4f transform = new Matrix4f().identity();
    public final Material material;

    protected float rotation = 0f;
    protected boolean isSolid = false;
    protected boolean destroyed = false;
    protected boolean isInstance = false;
    protected Mesh mesh;

    public GameObject(Vector2f position, Vector2f size, Material material) {
        this.mesh = Mesh.QUAD("quad");
        this.position = position;
        this.size = size;
        this.material = material;
        this.transform
                .translate(position.x,position.y,0.0f)
                .rotateZ(rotation)
                .scale(size.x,size.y,0f);
    }

    public abstract void input(Window window);

    public abstract void update(float deltaTime);

    public abstract void draw(Camera2D camera);

    public abstract void cleanup();
}
