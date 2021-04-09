package com.koerriva.bugbrain.engine.audio;

import com.koerriva.bugbrain.engine.graphics.g2d.Camera2D;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.openal.AL10.alDistanceModel;

final class SoundManager {
    private SoundListener listener;
    private final List<SoundBuffer> soundBufferList;
    private final Map<String, SoundSource> soundSourceMap;
    private final Matrix4f cameraMatrix;

    public SoundManager() {
        soundBufferList = new ArrayList<>();
        soundSourceMap = new HashMap<>();
        cameraMatrix = new Matrix4f();
    }

    public void init() throws Exception {

    }


    public SoundListener getListener() {
        return this.listener;
    }

    public void updateListenerPosition(Camera2D camera) {
        // Update camera matrix with camera data
        cameraMatrix.set(camera.getView());

        listener.setPosition(camera.getPosition());
        Vector3f at = new Vector3f();
        cameraMatrix.positiveZ(at).negate();
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        listener.setOrientation(at, up);
    }

    public void setAttenuationModel(int model) {
        alDistanceModel(model);
    }

    public void cleanup() {
        for (SoundSource soundSource : soundSourceMap.values()) {
            soundSource.cleanup();
        }
        soundSourceMap.clear();
        soundBufferList.clear();
    }
}
