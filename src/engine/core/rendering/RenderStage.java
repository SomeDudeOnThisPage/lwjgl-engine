package engine.core.rendering;

public enum RenderStage
{
  /**
   * Any operations required for systems down the line to work should be performed here.
   * Implemented by every rendering pipeline by default.
   */
  BEFORE,

  /**
   * Any operations required for systems after all the scene was fully rendered should be performed here (cleanup).
   * Implemented by every rendering pipeline by default.
   */
  AFTER,

  /**
   * Any rendering operation done will be written to the geometry buffer (g-buffer).
   * Implemented in the deferred rendering pipeline.
   */
  DEFERRED_GEOMETRY_PASS,

  /**
   * Any rendering operation done will be written to the shadow buffer (s-buffer).
   * Implemented in the deferred rendering pipeline.
   */
  DEFERRED_SHADOW_PASS,

  /**
   * Any rendering operation done will be written to the light buffer (l-buffer).
   * Implemented in the deferred rendering pipeline.
   */
  DEFERRED_LIGHTING_PASS,

  /**
   *
   */
  SCREEN_PASS,

  /**
   *
   */
  FILTER_PASS,

  /**
   * Any rendering operation done will be written to the post-processing buffer (p-buffer).
   * For default shader inputs, please refer to LINK_HERE.
   */
  POST_PROCESSING_PASS,
}
