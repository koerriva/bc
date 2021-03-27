package com.koerriva.project002.core.game.game.brain;

import com.koerriva.project002.core.game.Window;
import com.koerriva.project002.core.game.game.Direction;
import com.koerriva.project002.core.game.game.GameObject;
import com.koerriva.project002.core.game.graphic.*;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.*;

import static org.joml.Math.PI;

public class Brain extends GameObject {
    private final LinkedHashMap<Integer,Neural> neurals = new LinkedHashMap<>();
    private final LinkedHashMap<Integer,Vision> visions = new LinkedHashMap<>();
    private final LinkedHashMap<Integer,Muscle> muscles = new LinkedHashMap<>();
    private final LinkedHashMap<Integer,Synapse> synapses = new LinkedHashMap<>();
    private final HashSet<LinkLine> linkLines = new HashSet<>();
    private final HashMap<Integer,HashSet<Integer>> linkTree = new HashMap<>();

    private final Matrix4f transform = new Matrix4f().identity();

    private final Material cellMaterial = new Material();
    private final Mesh cellMesh = Mesh.QUAD("cell");

    private final FloatBuffer colorData = MemoryUtil.memAllocFloat(10000*4);
    private final FloatBuffer modelData = MemoryUtil.memAllocFloat(10000*16);

    public Brain(Vector2f position,Vector2f size) {
        super(position, size, Material.from(Texture.background(new Vector2f(1024,1024))));
        this.isInstance = true;
        this.transform.translate(position.x,position.y,0f)
                .scale(size.x,size.y,0f);
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
        Direction dir = Direction.to(input.position,neural.position);
        int angel = dir.getAngle();
        if(neural.isUsed(angel)){
            throw new RuntimeException("hold used!");
        }

        Synapse synapse = new Synapse(getCirclePos(neural.position,16f,angel),new Vector2f(8f));
        synapse.attach(neural.id);
        synapse.link(input.id);
        synapses.put(synapse.id,synapse);

        neural.useSynapse(synapse.id,angel);

        linkLines.add(new LinkLine(input.id,synapse.id,1));
        var tree = linkTree.getOrDefault(input.id,new HashSet<>());
        tree.add(synapse.id);
    }

    public void link(Neural from,Neural to){
        Direction dir = Direction.to(from.position,to.position);
        int angel = dir.getAngle();
        if(to.isUsed(angel)){
            throw new RuntimeException("hold used!");
        }

        Synapse synapse = new Synapse(getCirclePos(to.position,16f,angel),new Vector2f(8f));
        synapse.attach(to.id);
        synapse.link(from.id);
        synapses.put(synapse.id,synapse);
        to.useSynapse(synapse.id,angel);

        linkLines.add(new LinkLine(from.id,synapse.id,2));
        var tree = linkTree.getOrDefault(from.id,new HashSet<>());
        tree.add(synapse.id);
    }

    public void link(Neural neural,Muscle output){
        linkLines.add(new LinkLine(neural.id,output.id,3));
        var tree = linkTree.getOrDefault(neural.id,new HashSet<>());
        tree.add(output.id);
    }

    @Override
    public void input(Window window) {

    }

    @Override
    public void update(float deltaTime){
        visions.forEach((id,cell)-> {
            cell.update(deltaTime);
            if(cell.isActive){
                linkLines.forEach(linkLine -> {
                    if(linkLine.type==1&& linkLine.from.equals(id)){
                        synapses.get(linkLine.to).active();
                    }
                });
            }
        });

        synapses.forEach((id,cell)->{
            if(cell.isActive){
                neurals.get(cell.getTo()).active();
            }
        });

        neurals.forEach((id,cell)->{
            if(cell.isActive){
                linkLines.forEach(linkLine -> {
                    if(linkLine.type==2&& linkLine.from.equals(id)){
                        synapses.get(linkLine.to).active();
                    }
                    if(linkLine.type==3&& linkLine.from.equals(id)){
                        muscles.get(linkLine.to).active();
                    }
                });
            }
        });

        synapses.forEach((id,cell)->cell.update(deltaTime));
        neurals.forEach((id,cell)->cell.update(deltaTime));
        muscles.forEach((id,cell)->cell.update(deltaTime));
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

        for (LinkLine link:linkLines){
            Vector2f from,to;
            if(link.type==1){
                from = visions.get(link.from).position;
                to = synapses.get(link.to).position;
            }else if(link.type==2){
                from = neurals.get(link.from).position;
                to = synapses.get(link.to).position;
            }else if(link.type==3){
                from = neurals.get(link.from).position;
                to = muscles.get(link.to).position;
            }else {
                break;
            }

            Line line = new Line(from,to,5, new Vector4f(0.8f,0.8f,0.8f,1f));
            line.draw(camera);
            line.cleanup();
        }

        int batchSize = neurals.size()+visions.size()+muscles.size()+synapses.size();
        colorData.clear();
        modelData.clear();

        int idx = 0;
        for (Map.Entry<Integer, Neural> entry:neurals.entrySet()){
            Neural e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.transform.get(idx*16,modelData);
            idx++;
        }

        for (Map.Entry<Integer, Vision> entry:visions.entrySet()){
            Vision e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.transform.get(idx*16,modelData);
            idx++;
        }

        for (Map.Entry<Integer, Muscle> entry:muscles.entrySet()){
            Muscle e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.transform.get(idx*16,modelData);
            idx++;
        }

        for (Map.Entry<Integer, Synapse> entry:synapses.entrySet()){
            Synapse e = entry.getValue();
            e.color.get(idx*4,colorData);
            e.transform.get(idx*16,modelData);
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
