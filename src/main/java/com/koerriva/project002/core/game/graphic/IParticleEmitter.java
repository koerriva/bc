package com.koerriva.project002.core.game.graphic;

import java.util.List;

public interface IParticleEmitter {
    Particle getBaseParticle();
    List<Particle> getParticles();
    void cleanup();
}
