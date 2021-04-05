package com.koerriva.bugbrain.engine.graphic;

import java.util.List;

public interface IParticleEmitter {
    Particle getBaseParticle();
    List<Particle> getParticles();
    void cleanup();
}
