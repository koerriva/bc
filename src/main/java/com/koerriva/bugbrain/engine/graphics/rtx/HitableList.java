package com.koerriva.bugbrain.engine.graphics.rtx;

import java.util.ArrayList;

public class HitableList extends Hitable{
    private final ArrayList<Hitable> list = new ArrayList<>();
    @Override
    public HitInfo hit(Ray ray, float min_t, float max_t) {
        HitInfo hitInfo = new HitInfo();
        float closestSoFar = max_t;
        for (Hitable hitable : list) {
            HitInfo tmp = hitable.hit(ray, min_t, closestSoFar);
            if (tmp.hit) {
                closestSoFar = tmp.t;
                hitInfo = tmp;
            }
        }
        return hitInfo;
    }

    public void add(Hitable hitable){
        list.add(hitable);
    }

    public void clear(){
        list.clear();
    }
}
