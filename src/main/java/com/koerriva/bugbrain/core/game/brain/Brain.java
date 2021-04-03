package com.koerriva.bugbrain.core.game.brain;

import com.koerriva.bugbrain.core.game.InputManager;
import com.koerriva.bugbrain.core.game.Window;
import com.koerriva.bugbrain.core.game.Direction;
import com.koerriva.bugbrain.core.game.GameObject;
import com.koerriva.bugbrain.core.graphic.g2d.Camera2D;
import com.koerriva.bugbrain.core.graphic.g2d.Line2D;
import com.koerriva.bugbrain.core.graphic.Material;
import com.koerriva.bugbrain.core.graphic.Mesh;
import com.koerriva.bugbrain.core.graphic.Texture;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

import static org.joml.Math.PI;

public class Brain extends GameObject {
    private final Map<Integer,Vision> visions = new HashMap<>();
    private final Map<Integer,Neural> neurals = new HashMap<>();
    private final Map<Integer,Muscle> muscles = new HashMap<>();
    private final Map<Integer,Synapse> synapses = new HashMap<>();
    private final LinkNode root = LinkNode.root();

    private final Matrix4f transform = new Matrix4f().identity();

    private final Material cellMaterial = new Material();
    private final Mesh cellMesh = Mesh.QUAD("cell");

    private final FloatBuffer colorData = MemoryUtil.memAllocFloat(10000*4);
    private final FloatBuffer modelData = MemoryUtil.memAllocFloat(10000*16);

    public Brain(Vector2f position,Vector2f size) {
        super(position, size, Material.from(Texture.background(new Vector2f(4096,4096))));
        this.isInstance = true;
        this.transform.translate(position.x,position.y,0f)
                .scale(size.x,size.y,0f);
    }

    public void add(Cell cell){
        if(cell instanceof Neural){
            neurals.put(cell.id, (Neural) cell);
        }
        if(cell instanceof Vision){
            visions.put(cell.id, (Vision) cell);
        }
        if(cell instanceof Muscle){
            muscles.put(cell.id, (Muscle) cell);
        }
    }

    public void remove(Cell cell){
        if(cell instanceof Neural){
            neurals.remove(cell.id);
        }
        if(cell instanceof Vision){
            visions.remove(cell.id);
        }
        if(cell instanceof Muscle){
            muscles.remove(cell.id);
        }
        Set<Integer> cells = LinkNode.remove(cell);
        Cell.removeAll(cells);
    }

    public void link(Vision input,Neural neural){
        Direction dir = Direction.to(input.position,neural.position);
        int angel = dir.getAngle();
        if(neural.isUsed(angel)){
            throw new RuntimeException("hold used!");
        }

        Synapse synapse = new Synapse(neural,getCirclePos(neural.position,16f,angel),new Vector2f(8f));
        synapses.put(synapse.id,synapse);
        neural.useSynapse(synapse.id,angel);

        LinkNode.get(input).link(LinkNode.get(synapse));
        LinkNode.get(synapse).link(LinkNode.get(neural));

        root.link(LinkNode.get(input));
    }

    public void link(Neural from,Neural to){
        Direction dir = Direction.to(from.position,to.position);
        int angel = dir.getAngle();
        if(to.isUsed(angel)){
            throw new RuntimeException("hold used!");
        }

        Synapse synapse = new Synapse(to,getCirclePos(to.position,16f,angel),new Vector2f(8f));
        synapses.put(synapse.id,synapse);
        to.useSynapse(synapse.id,angel);

        LinkNode.get(from).link(LinkNode.get(synapse));
        LinkNode.get(synapse).link(LinkNode.get(to));
    }

    public void link(Neural neural,Muscle output){
        LinkNode.get(neural).link(LinkNode.get(output));
    }

    @Override
    public void input(Window window) {
        Vector2f mWPos = InputManager.mouse.getWorld();
        Vector2f offset = InputManager.mouse.getWorldOffset();
        for (Map.Entry<Integer, Cell> entry:Cell.cells.entrySet()){
            Cell e = entry.getValue();
            if(e.isInSide(mWPos)){
                if(InputManager.isDrag()){
                    e.setPosition(mWPos);
                    InputManager.dragHandled();
                }
            }
        }

        if(InputManager.isKeyPress(GLFW.GLFW_KEY_DELETE)){
            Neural n = neurals.get(3);
            if(n!=null){
                remove(n);
            }
        }
    }

    @Override
    public void update(float deltaTime){
        LinkNode.update(deltaTime);
        Cell.cells.forEach((id,cell)->cell.update(deltaTime));
    }

    private List<Line2D> render(LinkNode root){
        ArrayList<Line2D> lines = new ArrayList<>();
        for (Map.Entry<Integer, LinkNode> entry : root) {
            LinkNode n = entry.getValue();
            if(n.getType()==0)continue;
            Cell from = Cell.get(n.getId());
            for(LinkNode output:n.getOutput()){
                Cell to = Cell.get(output.getId());
                Line2D line = new Line2D(from.position, to.position, 5, new Vector4f(0.8f, 0.8f, 0.8f, 1f));
                lines.add(line);
            }
        }
        return lines;
    }

    @Override
    public void draw(Camera2D camera) {
        material.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setModel(transform)
                .setInstance(0)
                .setColor()
                .setTexture();
        mesh.draw();

        List<Line2D> lines = render(root);
        for (Line2D line:lines){
            line.draw(camera);
            line.cleanup();
        }

        int batchSize = Cell.cells.size();
        colorData.clear();
        modelData.clear();

        int idx = 0;
        for (Map.Entry<Integer, Cell> entry:Cell.cells.entrySet()){
            Cell e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.global.get(idx*16,modelData);
            idx++;
        }

        cellMaterial.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setInstance(1)
                .setTexture();
        cellMesh.drawBatch(batchSize,colorData,modelData);
    }

    @Override
    public void cleanup() {
        MemoryUtil.memFree(colorData);
        MemoryUtil.memFree(modelData);
    }

    private Vector2f getCirclePos(Vector2f r0,float r,float angle){
        float x0 = r0.x;
        float y0 = r0.y;
        float x1 = (float) (x0 + r * Math.cos(angle * PI / 180));

        float y1 = (float) (y0 + r * Math.sin(angle * PI /180));
        return new Vector2f(x1,y1);
    }
}
