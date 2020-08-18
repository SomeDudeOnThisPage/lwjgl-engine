package engine.platform.window;

import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public interface Window
{
  enum State
  {
    HIDDEN,
    WINDOWED,
    FULLSCREEN,
    BORDERLESS_FULLSCREEN
  }

  /**
   * Polls any events that have occurred since the last poll.
   */
  void poll();

  /**
   * Swaps the {@link Window}s' internal buffers after rendering has succeeded.
   */
  void swap();

  /**
   * Returns whether the window should close or not.
   * @return Whether the window should close or not.
   */
  boolean exit();

  /**
   * Sets whether the window should exit or not.
   * @param exit Whether the window should exit or not.
   */
  void exit(boolean exit);

  void clear();

  long handle();

  /**
   * Returns the size of this {@link Window} as a {@link Vector2i}, with the {@link Vector2i#x} field representing
   * the width, and the {@link Vector2i#y} field representing the height of this {@link Window} in pixels.
   * @return The size of this {@link Window} as a {@link Vector2i}.
   */
  @NotNull
  Vector2i size();

  /**
   * Returns the current {@link Window.State} of this {@link Window}.
   * @return The current {@link Window.State} of this {@link Window}.
   */
  @NotNull
  Window.State mode();
}
