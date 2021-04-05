package com.koerriva.bugbrain.engine.graphics;

import java.util.List;

public interface IParticleEmitter {
    Particle getBaseParticle();
    List<Particle> getParticles();
    void cleanup();
}
