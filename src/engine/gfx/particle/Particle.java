package engine.gfx.particle;

public interface Particle
{
  float lifetime();
  boolean animated();
  boolean update();
}
