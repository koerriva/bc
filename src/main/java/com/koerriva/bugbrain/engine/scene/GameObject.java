package com.koerriva.bugbrain.engine.scene;

import com.koerriva.bugbrain.core.brain.Cell;
import com.koerriva.bugbrain.engine.graphics.Window;
import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import com.koerriva.bugbrain.engine.graphics.Material;
import com.koerriva.bugbrain.engine.graphics.Mesh;
import org.joml.Vector2f;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GameObject {
    protected static final Map<Integer, GameObject> objects = new HashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    protected final Vector2f position;
    protected final Vector2f size;
    protected final float rotation = 0f;

    protected boolean isInstance = false;
    protected Mesh mesh;
    public final Material material;
    public final Integer id;

    public GameObject(Vector2f position, Vector2f size, Material material) {
        this.mesh = Mesh.QUAD("quad");
        this.position = position;
        this.size = size;
        this.material = material;
        this.id = counter.incrementAndGet();
        objects.put(this.id,this);
    }

    public abstract Transform getWorldTransform();

    public abstract void input(Window window);

    public abstract void update(float deltaTime);

    public abstract void draw(Camera2D camera);

    public abstract void cleanup();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameObject that = (GameObject) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash("GameObject:"+id);
    }
}
