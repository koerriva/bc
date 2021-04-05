package com.koerriva.bugbrain.core.brain;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LinkNode implements Iterable<Map.Entry<Integer,LinkNode>>{
    private static final Map<Integer,LinkNode> nodes = new HashMap<>();
    private static LinkNode root;
    private static Random random = new Random(1234);

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

    public void link(LinkNode out){
        if(!this.output.contains(out)){
            this.output.add(out);
            out.input.add(this);
        }
    }

    private void active(){
        if(type==1){
            for (LinkNode out:output){
                out.active();
            }
        }else if(type>1){
            Integer signals = input.stream().map(node -> Cell.get(node.id).isActive?1:0).reduce(0, Integer::sum);
            Cell.get(id).isActive = signals==input.size();

            for (LinkNode out:output){
                out.active();
            }
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

    public static Set<Integer> remove(Cell cell){
        Set<Integer> cells = new HashSet<>();
        if(cell==null)return cells;
        cells.add(cell.id);
        if(nodes.containsKey(cell.id)){
            LinkNode me = nodes.get(cell.id);
            if(cell instanceof Neural){
                //移除 synapse
                for (LinkNode synapseNode : me.input) {
                    synapseNode.input.forEach(node -> node.output.remove(synapseNode));
                    nodes.remove(synapseNode.id);
                    cells.add(synapseNode.id);
                }

                for (LinkNode node : me.output){
                    if(node.type==2){
                        node.output.forEach(outNode -> outNode.input.remove(node));
                        nodes.remove(node.id);
                        cells.add(node.id);
                    }else{
                        node.input.remove(me);
                    }
                }
                nodes.remove(me.id);
                cells.add(me.id);
            }
        }
        return cells;
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
        String i = Arrays.toString(input.stream().map(node -> node.id).toArray());
        String o = Arrays.toString(output.stream().map(node -> node.id).toArray());
        return "LinkNode{" +
                "id=" + id +
                ", input=" + i +
                ", output=" + o +
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
