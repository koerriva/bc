package com.koerriva.project002.core.game.game.brain;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Cell{
    protected static final Map<Integer,Cell> cells = new HashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    public final int id;
    protected final Vector2f position;
    protected final Vector2f size;
    public final Vector4f color;
    public final Matrix4f transform = new Matrix4f();

    protected float ttl = 0;
    protected boolean isActive = false;
    protected float activeKeepTime = 0.08f;

    public Cell(Vector2f position, Vector2f size, Vector4f color) {
        this.position = position;
        this.size = size;
        this.color = color;
        this.id = counter.incrementAndGet();
        this.transform.identity().translate(position.x,position.y,0f)
                .scale(size.x,size.y,0f);
    }

    private void updateTransform(){
        this.transform.identity().translate(position.x,position.y,0f)
                .scale(size.x,size.y,0f);
    }

    public void setPosition(Vector2f position){
        this.position.set(position);
        updateTransform();
    }

    public void translate(Vector2f offset){
        this.position.add(offset);
        updateTransform();
    }

    private final Vector2f offset = new Vector2f();
    public boolean isInSide(Vector2f pos){
        pos.sub(position,offset);
        return offset.lengthSquared()<size.lengthSquared();
    }

    public abstract void update(float deltaTime);

    public static Cell get(Integer id) {
        return cells.get(id);
    }

    public static void remove(Cell cell){
        cells.remove(cell.id);
    }

    public static void removeAll(Set<Integer> ids){
        ids.forEach(cells::remove);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                '}';
    }
}
