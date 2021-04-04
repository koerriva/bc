package com.koerriva.bugbrain.core.audio;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class AudioManager {
    private static SoundManager soundManager;
    private static SoundListener defaultSoundListener;
    private static long device;
    private static long context;

    public static void init() throws Exception {
        device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);

        soundManager = new SoundManager();
        soundManager.init();
        defaultSoundListener = new SoundListener();
    }

    public static void play(Audio audio) {
        if(!audio.isPlaying()){
            audio.play();
        }
    }

    public static void stop(Audio audio) {
        if(audio.isPlaying()){
            audio.stop();
        }
    }

    public static void update(){

    }

    public static void cleanup(){
        if (context != NULL) {
            alcDestroyContext(context);
        }
        if (device != NULL) {
            alcCloseDevice(device);
        }
    }
}
