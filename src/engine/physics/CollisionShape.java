package engine.physics;

import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import engine.asset.BaseAsset;

public class CollisionShape extends BaseAsset
{
  private final btCollisionShape shape;

  public btCollisionShape internal()
  {
    return this.shape;
  }

  @Override
  public void dispose()
  {
    this.shape.dispose();
  }

  public CollisionShape(btCollisionShape shape)
  {
    this.shape = shape;
  }
}
