package com.koerriva.bugbrain.core.brain;

import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.joml.Math.PI;

public abstract class Cell{
    protected static final Map<Integer,Cell> cells = new HashMap<>();
    private static final AtomicInteger counter = new AtomicInteger(0);

    public final int id;
    protected final Vector2f position;
    protected final Vector2f size;
    protected final Vector4f color;

    protected float ttl = 0;
    protected boolean isActive = false;
    protected float activeKeepTime = 0.08f;

    public Cell(Vector2f position, Vector2f size, Vector4f color) {
        this.position = position;
        this.size = size;
        this.color = color;
        this.id = counter.incrementAndGet();
    }

    public void setPosition(Vector2f position){
        this.position.set(position);
    }

    private final Vector2f offset = new Vector2f();
    public boolean isInSide(Vector2f pos){
        pos.sub(position,offset);
        return offset.lengthSquared()<size.lengthSquared();
    }
    public boolean isInEdge(Vector2f pos){
        pos.sub(position,offset);
        float miniR = size.x/2 - 5;
        float maxR = size.x/2 + 5;
        float offsetLengthSquared = offset.lengthSquared();
        return miniR*miniR < offsetLengthSquared && offsetLengthSquared <= maxR*maxR;
    }

    public abstract void update(float deltaTime);
    public abstract Transform getWorldTransform();

    public static Cell get(Integer id) {
        return cells.get(id);
    }

    public static void remove(Cell cell){
        cells.remove(cell.id);
    }

    public static void removeAll(Set<Integer> ids){
        ids.forEach(cells::remove);
    }

    static Vector2f getCirclePos(Vector2f r0,float r,float angle){
        float x0 = r0.x;
        float y0 = r0.y;
        float x1 = (float) (x0 + r * Math.cos(angle * PI / 180));

        float y1 = (float) (y0 + r * Math.sin(angle * PI /180));
        return new Vector2f(x1,y1);
    }

    static Vector2f getCirclePos(float r, float angle){
        return  getCirclePos(new Vector2f(0),r,angle);
    }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                '}';
    }
}
