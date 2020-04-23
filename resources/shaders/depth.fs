#include "shared.h"

uniform int u_layer;

void main()
{
  /*
    As of now, use RGBA-Values instead of just depth.
    // gl_FragData[u_layer].a = gl_FragCoord.z;
  */
  float depth = gl_FragCoord.z;

  float dx = dFdx(depth);
  float dy = dFdy(depth);
  float moment = depth * depth + 0.25f * (dx * dx + dy * dy);

  //gl_FragColor = vec4(depth, moment, 1.0f, 1.0f);
  gl_FragData[u_layer].rgba = vec4(depth, moment, 1.0f, 1.0f);
}