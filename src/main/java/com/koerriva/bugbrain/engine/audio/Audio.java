package com.koerriva.bugbrain.engine.audio;

import java.util.HashMap;

public class Audio {
    private static final HashMap<String,Audio> resource = new HashMap<>();
    private final SoundBuffer buffer;
    private final SoundSource source;

    private Audio(SoundBuffer buffer,SoundSource source){
        this.buffer = buffer;
        this.source = source;

        source.setBuffer(this.buffer.id);
    }

    public void play(){
        this.source.play();
    }

    public void setGain(float gain){
        this.source.setGain(gain);
    }

    public void setLoop(boolean loop){
        this.source.setLoop(loop);
    }

    public void pause(){
        this.source.pause();
    }

    public void stop(){
        this.source.stop();
    }

    public boolean isPlaying(){
        return this.source.isPlaying();
    }

    public static Audio load(String filename){
        if(resource.containsKey(filename)){
            return resource.get(filename);
        }
        SoundBuffer buffer = null;
        try {
            buffer = SoundBuffer.load(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SoundSource source = new SoundSource(false,true);
        Audio audio = new Audio(buffer,source);
        resource.put(filename,audio);
        return audio;
    }
}
