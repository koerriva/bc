package com.koerriva.bugbrain.core.graphic;

import java.util.List;

public interface IParticleEmitter {
    Particle getBaseParticle();
    List<Particle> getParticles();
    void cleanup();
}
