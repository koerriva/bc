package com.koerriva.project002.core.game.game.brain;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.game.GameObject;
import com.koerriva.project002.core.game.graphic.Camera2D;
import com.koerriva.project002.core.game.graphic.Material;
import com.koerriva.project002.core.game.graphic.Texture;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.joml.Math.PI;

public class Brain extends GameObject {
    private final LinkedHashMap<Integer,Neural> neurals = new LinkedHashMap<>();
    private final LinkedHashMap<Integer,Vision> visions = new LinkedHashMap<>();
    private final LinkedHashMap<Integer,Muscle> muscles = new LinkedHashMap<>();
    private final LinkedHashMap<Integer,Synapse> synapses = new LinkedHashMap<>();
    private final HashSet<LinkLine> linkLines = new HashSet<>();

    private final Matrix4f transform = new Matrix4f().identity();

    private final Material cellMaterial = new Material();

    public Brain(Vector2f position,Vector2f size) {
        super(position, size, Material.from(Texture.background(new Vector2f(4096,4096))));
        this.isInstance = true;
        this.transform.translate(position.x,position.y,0f)
                .scale(size.x,size.y,0f);
    }

    @Override
    public void input(Window window) {

    }

    public void add(Neural cell){
        neurals.put(cell.id,cell);
    }

    public void add(Vision cell){
        visions.put(cell.id,cell);
    }

    public void add(Muscle cell){
        muscles.put(cell.id,cell);
    }

    public void link(Vision input,Neural neural){
        Synapse synapse = new Synapse(getCirclePos(neural.position,32f,-160),new Vector2f(16f));
        synapse.attach(neural.id);
        synapse.link(input.id);
        synapses.put(synapse.id,synapse);

        linkLines.add(new LinkLine(input.id,synapse.id));
    }

    public void link(Neural from,Neural to){
        Synapse synapse = new Synapse(getCirclePos(to.position,32f,-160),new Vector2f(16f));
        synapse.attach(to.id);
        synapse.link(from.id);
        synapses.put(synapse.id,synapse);

        linkLines.add(new LinkLine(from.id,synapse.id));
    }

    public void link(Neural neural,Muscle output){
        linkLines.add(new LinkLine(neural.id,output.id));
    }

    @Override
    public void update(float deltaTime){

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

        int batchSize = neurals.size()+visions.size()+muscles.size()+synapses.size();
        FloatBuffer colorData = MemoryUtil.memAllocFloat(batchSize*4);
        FloatBuffer modelData = MemoryUtil.memAllocFloat(batchSize*16);

        int idx = 0;
        for (Map.Entry<Integer, Neural> entry:neurals.entrySet()){
            Neural e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.getTransform().get(idx*16,modelData);
            idx++;
        }

        for (Map.Entry<Integer, Vision> entry:visions.entrySet()){
            Vision e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.getTransform().get(idx*16,modelData);
            idx++;
        }

        for (Map.Entry<Integer, Muscle> entry:muscles.entrySet()){
            Muscle e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.getTransform().get(idx*16,modelData);
            idx++;
        }

        for (Map.Entry<Integer, Synapse> entry:synapses.entrySet()){
            Synapse e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.getTransform().get(idx*16,modelData);
            idx++;
        }

        cellMaterial.use()
                .setProjection(camera.getProjectionMatrix())
                .setView(camera.getViewMatrix())
                .setInstance(1)
                .setTexture();
        mesh.drawBatch(batchSize,colorData,modelData);

        MemoryUtil.memFree(colorData);
        MemoryUtil.memFree(modelData);
    }

    @Override
    public void cleanup() {

    }

    private Vector2f getCirclePos(Vector2f r0,float r,float angle){
        float x0 = r0.x;
        float y0 = r0.y;
        float x1 = (float) (x0 + r * Math.cos(angle * PI / 180));

        float y1 = (float) (y0 + r * Math.sin(angle * PI /180));
        return new Vector2f(x1,y1);
    }
}
