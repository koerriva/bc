package com.koerriva.bugbrain.engine.scene;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform {
    private final Vector3f translation;
    private final Quaternionf rotation;
    private final Vector3f scaling;

    private final Matrix4f worldMatrix = new Matrix4f();
    private final Matrix4f modelMatrix = new Matrix4f();

    public Transform(Vector3f translation, Quaternionf rotation, Vector3f scaling) {
        this.translation = translation;
        this.rotation = rotation;
        this.scaling = scaling;
    }

    public Transform(){
        this.translation = new Vector3f(0);
        this.rotation = new Quaternionf().identity();
        this.scaling = new Vector3f(1);
    }

    public Matrix4f getWorldMatrix(){
        return worldMatrix.identity()
                .translate(translation)
                .rotate(rotation)
                .scale(scaling);
    }

    public Matrix4f getModelMatrix(){
        return modelMatrix.identity()
                .rotate(rotation);
    }

    public void setTranslation(Vector2f position){
        translation.set(position.x,position.y,0);
    }

    public void setScaling(Vector2f size){
        scaling.set(size.x,size.y,1f);
    }

    /*
     * @Param angle 弧度值
     */
    public void setRotation(float xAngle,float yAngle,float zAngle){
        rotation.rotateX(xAngle);
        rotation.rotateY(yAngle);
        rotation.rotateZ(zAngle);
    }
}
