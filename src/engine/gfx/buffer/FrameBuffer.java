package engine.gfx.buffer;

import engine.asset.Asset;
import engine.gfx.Bindable;
import engine.gfx.Texture2D;

public interface FrameBuffer extends Asset, Bindable
{
  enum BIT_FLAGS // todo: some better name for this
  {
    COLOR,
    DEPTH,
    STENCIL
  }

  void clear(BIT_FLAGS... flags);

  /**
   * Adds a {@link Texture2D} to this {@link FrameBuffer}.
   * <p>
   *   Note that the {@link Texture2D}s' {@link Texture2D#getTextureSlot()} determines the attachment slot of the
   *   texture!
   * </p>
   * @param texture
   */
  void addTexture2D(Texture2D texture);

  void addTexture2D(Texture2D texture, int attachment);
}
