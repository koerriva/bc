package com.koerriva.bugbrain.engine.graphics;

import org.joml.Matrix4f;

public abstract class Camera {
    public abstract Matrix4f getView();
    public abstract Matrix4f getProjection();
}
