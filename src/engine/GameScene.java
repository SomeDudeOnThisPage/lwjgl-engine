package engine;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.*;
import engine.core.entity.Entity;
import engine.core.entity.component.*;
import engine.core.entity.component.lighting.DirectionalLightSourceComponent;
import engine.core.entity.component.lighting.FlickerComponent;
import engine.core.entity.component.lighting.PointLightSourceComponent;
import engine.core.entity.component.physics.CollisionShapeComponent;
import engine.core.entity.component.shadow.ShadowCasterComponent;
import engine.core.entity.component.shadow.ShadowSourceComponent;
import engine.core.entity.system.*;
import engine.core.entity.system.rendering.DirectionalLightingSystem;
import engine.core.entity.system.rendering.debug.JBulletDebugRenderingSystem;
import engine.core.entity.system.rendering.PointLightSystem;
import engine.core.entity.system.rendering.shadow.ShadowCasterCollection;
import engine.core.entity.system.rendering.shadow.ShadowMapSystem;
import engine.core.gfx.batching.AssetManager;
import engine.core.gfx.material.PBRMaterialFlat;
import engine.core.rendering.DeferredRenderer;
import engine.core.scene.Player;
import engine.core.scene.Scene;
import engine.util.Assimp;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.lang.System;

import static org.lwjgl.opengl.GL11C.GL_FRONT;

public class GameScene extends Scene
{
  @Override
  public synchronized void onInit()
  {
    // add and init systems
    this.add(new CameraMovementSystem());
    this.add(new SkyboxSystem());
    this.add(new PhysicsSystem());

    // bleep bloop there goes the performance
    this.add(new JBulletDebugRenderingSystem(this.physics()));

    // add rendering systems
    this.add(new DeferredMeshRenderingSystem());
    this.add(new DirectionalLightingSystem());
    this.add(new PointLightSystem());
    this.add(new ShadowMapSystem());
    this.add(new ShadowCasterCollection());

    // todo: make renderer setup n i c e r
    this.renderer = new DeferredRenderer();
    this.player = new Player(); // player entity
    this.player.add(this.player.getCamera());

    // add sun directional light
    new Entity()
      .add(new TransformComponent(
        new Vector3f(0.0f, 200.0f, 200.0f),
        new Quaternionf(),
        1.0f
      ))
      .add(new DirectionalLightSourceComponent(
        new Vector3f(-0.2f, -0.5f, -0.3f),
        new Vector3f(0.6f, 0.6f, 0.45f),
        new Vector3f(1.0f, 0.1f, 0.1f)      // todo: remove clq attenuation from directional light sources
      ))
      .add(new ShadowSourceComponent(1000000.0f));

    // add sky box
    new Entity()
      .add(new SkyboxComponent("blue"));

    //
    // Testing: Create PBRMaterialFlat test materials manually.
    //
    PBRMaterialFlat white = (PBRMaterialFlat) AssetManager.loadMaterial("white", new PBRMaterialFlat(
      new Vector3f(1.0f, 1.0f, 1.0f),
      0.2f,   // ao
      0.99f,   // roughness
      0.01f, // metallic
      0.0f    // emissive
    ));
    PBRMaterialFlat yellow = (PBRMaterialFlat) AssetManager.loadMaterial("yellow", new PBRMaterialFlat(
      new Vector3f(0.5f, 0.5f, 0.245f),
      0.2f,   // ao
      0.99f,   // roughness
      0.01f, // metallic
      0.2f    // emissive
    ));
    PBRMaterialFlat brown = (PBRMaterialFlat) AssetManager.loadMaterial("brown", new PBRMaterialFlat(
      new Vector3f(74.0f / 255.0f, 62.0f / 255.0f, 33.0f / 255.0f),
      0.2f,   // ao
      0.99f,   // roughness
      0.01f, // metallic
      0.0f    // emissive
    ));
    PBRMaterialFlat grey = (PBRMaterialFlat) AssetManager.loadMaterial("grey", new PBRMaterialFlat(
      new Vector3f(74.0f / 255.0f, 74.0f / 255.0f, 74.0f / 255.0f),
      0.2f,   // ao
      0.99f,   // roughness
      0.01f, // metallic
      0.0f    // emissive
    ));

    Entity map = new Entity()
      .add(new MeshComponent(AssetManager.getMesh("terrain")))
      .add(new TransformComponent(
        new Vector3f(0.0f, 0.0f, 0.0f),
        new Quaternionf(),
        1.0f
      ))
      .add(new ShadowCasterComponent());

    map.add(new CollisionShapeComponent(map, new btBvhTriangleMeshShape(Assimp.load_collision_mesh_tri("terrain"), true)));
    map.get(MeshComponent.class).material[0] = brown;
    //map.get(MeshComponent.class).material[1] = grey;
    map.get(MeshComponent.class).culling = GL_FRONT;

    this.player.add(new MeshComponent(AssetManager.getMesh("capsule1x2")))
      .add(new TransformComponent(
        new Vector3f(24.316f, 2.5685f, -10.485f),
        new Quaternionf(),
        0.75f
      ))
      .add(new CharacterControllerComponent())
      .add(new ShadowCasterComponent());

    //
    // Create lantern entity.
    //
    Entity lantern = new Entity()
      .add(new MeshComponent(AssetManager.getMesh("lantern")))
      .add(new TransformComponent(
        new Vector3f(24.316f, 1.8685f, -10.485f),
        new Quaternionf(),
        1.0f
      ))
      .add(new ShadowCasterComponent());

    //
    // Testing: Set materials manually. Todo: Load this from files.
    //
    lantern.get(MeshComponent.class).material[0] = white;
    lantern.get(MeshComponent.class).material[1] = yellow;
    lantern.add(new CollisionShapeComponent(lantern, new btBvhTriangleMeshShape(Assimp.load_collision_mesh_tri("lantern"), true)));
    lantern.add(new ShadowCasterComponent());

    //
    // Add light source component child entity to the lantern.
    //
    lantern.add(new Entity()
      .add(new TransformComponent(
        new Vector3f(0.0f, 4.0f, 0.0f), // offset
        new Quaternionf(),
        1.0f
      ))
      .add(new PointLightSourceComponent(
        new Vector3f(0.5f, 0.5f, 0.245f),
        new Vector3f(8f, 0.07f, 0.01f)
      ))
      .add(new FlickerComponent())
    );

    // cabin
    Entity cabin = new Entity("cabin")
      .add(new MeshComponent(AssetManager.getMesh("cabin")))
      .add(new TransformComponent(
        new Vector3f(-4.5758f, 2.0781f, -101.04f),
        new Quaternionf().rotationY(15.0f),
        1.0f))
      .add(new ShadowCasterComponent());

    cabin.add(new CollisionShapeComponent(cabin, new btBvhTriangleMeshShape(Assimp.load_collision_mesh_tri("cabin"), true)));

    Entity handheld = new Entity("hl")
        .add(new MeshComponent(AssetManager.getMesh("handheld_lantern")))
        .add(new TransformComponent());

    handheld.get(TransformComponent.class).position.x = 15.0f;

    handheld.add(new Entity()
      .add(new TransformComponent(
        new Vector3f(0.0f, 0.1f, 0.0f), // offset
        new Quaternionf(),
        1.0f
      ))
      .add(new PointLightSourceComponent(
        new Vector3f(0.5f, 0.5f, 0.245f),
        new Vector3f(8f, 0.07f, 0.01f)
      ))
      .add(new FlickerComponent())
    );

    handheld.add(new CollisionShapeComponent(this.get("hl"), 1.0f, new btBoxShape(new Vector3(0.15f, 0.15f, 0.15f))));
    handheld.get(CollisionShapeComponent.class).body.setCollisionFlags(btCollisionObject.CollisionFlags.CF_NO_CONTACT_RESPONSE);
    handheld.get(CollisionShapeComponent.class).body.setAngularFactor(0.9f);
    handheld.get(CollisionShapeComponent.class).body.setDamping(0.9995f, 0.9995f);
    handheld.get(CollisionShapeComponent.class).body.setInvInertiaDiagLocal(new Vector3(0.3f, 1.0f, 0.3f));
    handheld.get(CollisionShapeComponent.class).body.updateInertiaTensor();

    handheld.get(MeshComponent.class).material[1] = yellow;

    Matrix4 frameA = new Matrix4();
    Matrix4 frameB = new Matrix4();

    frameB.setTranslation(-0.5f, -0.5f, -1.2f);
    frameA.setTranslation(0.0f, 0.35f, 0.0f);

    btGeneric6DofConstraint constraint = new btGeneric6DofConstraint(
      handheld.get(CollisionShapeComponent.class).body,
      this.player.get(CharacterControllerComponent.class).body,
      frameA,
      frameB,
      true
    );

    constraint.setLinearLowerLimit(new Vector3(0.0f, 0.0f, 0.0f));
    constraint.setLinearUpperLimit(new Vector3(0.0f, 0.0f, 0.0f));
    constraint.setAngularLowerLimit(new Vector3((float) -Math.PI * 0.05f, (float) -Math.PI * 0.05f, (float) -Math.PI * 0.05f));
    constraint.setAngularUpperLimit(new Vector3((float) Math.PI * 0.05f, (float) Math.PI * 0.05f, (float) Math.PI * 0.05f));

    btTranslationalLimitMotor motor = constraint.getTranslationalLimitMotor();
    motor.setDamping(0.0f);
    motor.setLimitSoftness(0.0f);
    motor.setRestitution(0.0f);

    //constraint.getTranslationalLimitMotor().setCurrentLimitError(new btVector3(0.0f, 0.0f, 0.0f));

    this.physics().world().addConstraint(constraint);
  }

  @Override
  public void onEnter()
  {
    System.out.println("Hello World!");
  }

  @Override
  public void onExit()
  {
    this.physics().terminate();
    System.out.println("Goodbye World!");
  }
}