package engine.core.entity.component.debug;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import engine.core.Input;
import engine.core.entity.Entity;
import engine.core.entity.component.Behaviour;
import engine.core.entity.component.MeshComponent;
import engine.core.entity.component.TransformComponent;
import engine.core.entity.component.lighting.PointLightSourceComponent;
import engine.core.entity.component.physics.CollisionShapeComponent;
import engine.core.entity.component.shadow.ShadowCasterComponent;
import engine.core.gfx.batching.AssetManager;
import engine.core.scene.Player;
import engine.core.scene.Scene;
import engine.core.scene.SceneGraph;
import engine.util.Utils;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class LightShooterComponent extends Behaviour
{
  @Override
  public void update(Scene scene)
  {
    if (Input.keyDown(GLFW_KEY_X))
    {
      // assume entity is a player
      Quaternionf rotation = new Quaternionf(SceneGraph.constructTransform(((Player) this.entity).getCamera()).getRotation(new AxisAngle4f()));
      Vector3f direction = new Vector3f(0.0f, 0.0f, 1.0f);
      direction.rotate(rotation).negate();

      Vector3f position = ((Player) this.entity).getPosition();

      float size = (float) Math.min(Math.max(Math.random() / 2.0f, 0.25f), 0.5f);

      Entity light = new Entity()
        .add(new TransformComponent(
          new Vector3f(position.add(0.0f, 5.0f, 0.0f)),
          new Quaternionf(),
          size
        ))
        .add(new MeshComponent(AssetManager.getMesh("sphere")))
        .add(new PointLightSourceComponent(
          new Vector3f((float) Math.random(), (float) Math.random(), (float) Math.random()),
          new Vector3f(25f, 0.07f, 0.01f)
        ))
        .add(new ShadowCasterComponent());

      light.get(MeshComponent.class).material[0] = AssetManager.getMaterial("pbr-flat-white");

      light.add(new CollisionShapeComponent(light, 1.0f, new btSphereShape(size)));
      light.get(CollisionShapeComponent.class).body.setInvInertiaDiagLocal(new Vector3(0.8f, 0.8f, 0.8f));
      light.get(CollisionShapeComponent.class).body.updateInertiaTensor();
      light.get(CollisionShapeComponent.class).body.applyCentralImpulse(Utils.convert(direction.normalize().mul(10.0f)));
    }
  }
}