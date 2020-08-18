package engine.render;

public enum RenderStage
{
  BEFORE,
  AFTER,
  FORWARD_PASS,
  DEBUG_GUI,

  // deferred stages
  DEFERRED_GEOMETRY_PASS,
  DEFERRED_LIGHT_PASS,

  DEFERRED_POST_PROCESS_PASS
}
