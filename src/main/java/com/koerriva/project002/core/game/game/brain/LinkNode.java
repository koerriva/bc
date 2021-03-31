package com.koerriva.project002.core.game.game.brain;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LinkNode implements Iterable<Map.Entry<Integer,LinkNode>>{
    private static final Map<Integer,LinkNode> nodes = new HashMap<>();
    private static LinkNode root;

    private final Integer id;
    private final ArrayList<LinkNode> input;
    private final ArrayList<LinkNode> output;
    private int type=0;

    private LinkNode(){
        this.id = null;
        this.input = new ArrayList<>();
        this.output = new ArrayList<>();
        root = this;
    }

    private LinkNode(Cell cell){
        this.id = cell.id;
        this.input = new ArrayList<>();
        this.output = new ArrayList<>();
        nodes.put(id,this);
        if (cell instanceof Vision){
            type = 1;
        }else if(cell instanceof Synapse){
            type = 2;
        }else if(cell instanceof Neural){
            type = 3;
        }else if(cell instanceof Muscle){
            type = 4;
        }else {
            type = 5;
        }
    }

    public void link(LinkNode child){
        if(!this.output.contains(child)){
            this.output.add(child);
            child.input.add(this);
        }
    }

    private void active(){
        if(type==1){
            for (LinkNode child:output){
                child.active();
            }
        }else if(type>1){
        }
    }

    public Integer getId() {
        return id;
    }

    public static boolean has(Cell cell){
        return nodes.containsKey(cell.id);
    }

    public static LinkNode root(){
        LinkNode node = new LinkNode();
        nodes.put(node.getId(),node);
        return node;
    }

    public static LinkNode get(Cell cell){
        if(nodes.containsKey(cell.id)){
            return nodes.get(cell.id);
        }
        LinkNode node = new LinkNode(cell);
        nodes.put(node.getId(),node);
        return node;
    }

    public static void remove(Cell cell){
        if(nodes.containsKey(cell.id)){
            LinkNode me = nodes.get(cell.id);
            me.input.forEach(node -> node.output.remove(me));
            me.output.forEach(node -> node.input.remove(me));
            nodes.remove(me.id);
        }
    }

    public static void update(float deltaTime){
        root.output.forEach((LinkNode::active));
    }

    public int getType() {
        return type;
    }

    public ArrayList<LinkNode> getInput() {
        return input;
    }

    public ArrayList<LinkNode> getOutput() {
        return output;
    }

    @Override
    public @NotNull Iterator<Map.Entry<Integer,LinkNode>> iterator() {
        return nodes.entrySet().iterator();
    }

    @Override
    public String toString() {
        return "LinkNode{" +
                "id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkNode linkNode = (LinkNode) o;
        return type == linkNode.type && Objects.equals(id, linkNode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}
