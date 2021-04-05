package com.koerriva.bugbrain.core.brain;

import com.koerriva.bugbrain.core.Direction;
import com.koerriva.bugbrain.engine.audio.Audio;
import com.koerriva.bugbrain.engine.audio.AudioManager;
import com.koerriva.bugbrain.engine.graphics.*;
import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import com.koerriva.bugbrain.engine.graphics.g2d.Line2D;
import com.koerriva.bugbrain.engine.input.InputManager;
import com.koerriva.bugbrain.engine.scene.GameObject;
import com.koerriva.bugbrain.engine.scene.Transform;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

public class Brain extends GameObject {
    private final Map<Integer,Vision> visions = new HashMap<>();
    private final Map<Integer,Neural> neurals = new HashMap<>();
    private final Map<Integer,Muscle> muscles = new HashMap<>();
    private final Map<Integer,Synapse> synapses = new HashMap<>();
    private final CellLink root = CellLink.create();

    private final Transform transform = new Transform();

    private final Material cellMaterial = new Material();
    private final Mesh cellMesh = Mesh.QUAD("cell");

    private final FloatBuffer colorData = MemoryUtil.memAllocFloat(10000*4);
    private final FloatBuffer modelData = MemoryUtil.memAllocFloat(10000*16);

    private final Audio bgMusic;

    public Brain(Vector2f position,Vector2f size) {
        super(position, size, Material.from(Texture.background(new Vector2f(4096,4096)),
                Shader.load("bg-cell")));
        this.isInstance = true;

        this.transform.setTranslation(position);
        this.transform.setScaling(size);

        this.bgMusic = Audio.load("seansecret__harmonic-ambience.ogg");
        this.bgMusic.setGain(0.1f);
        this.bgMusic.setLoop(true);
        AudioManager.play(this.bgMusic);
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
        Set<Integer> cells = CellLink.remove(cell);
        Cell.removeAll(cells);
    }

    public void link(Vision input,Neural neural){
        Direction dir = Direction.to(input.position,neural.position);
        int angel = dir.getAngle();
        if(neural.isUsed(angel)){
            throw new RuntimeException("hold used!");
        }

        Synapse synapse = new Synapse(neural,new Vector2f(16f),angel);
        synapses.put(synapse.id,synapse);

        CellLink.get(input).link(CellLink.get(synapse));
        CellLink.get(synapse).link(CellLink.get(neural));

        root.link(CellLink.get(input));
    }

    public void link(Neural from,Neural to){
        Direction dir = Direction.to(from.position,to.position);
        int angel = dir.getAngle();
        if(to.isUsed(angel)){
            throw new RuntimeException("hold used!");
        }

        Synapse synapse = new Synapse(to,new Vector2f(16f),angel);
        synapses.put(synapse.id,synapse);

        CellLink.get(from).link(CellLink.get(synapse));
        CellLink.get(synapse).link(CellLink.get(to));
    }

    public void link(Neural neural,Muscle output){
        CellLink.get(neural).link(CellLink.get(output));
    }

    @Override
    public Transform getWorldTransform() {
        return transform;
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
        CellLink.update(deltaTime);
        Cell.cells.forEach((id,cell)->cell.update(deltaTime));
    }

    private List<Line2D> render(CellLink root){
        ArrayList<Line2D> lines = new ArrayList<>();
        for (Map.Entry<Integer, CellLink> entry : root) {
            CellLink n = entry.getValue();
            if(n.getType()==0)continue;
            Cell from = Cell.get(n.getId());
            for(CellLink output:n.getOutput()){
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
                .setModel(transform.getWorldMatrix())
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
            e.transform.getWorldMatrix().get(idx*16,modelData);
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
}
