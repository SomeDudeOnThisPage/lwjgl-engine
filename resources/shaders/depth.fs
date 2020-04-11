#include "shared.h"

uniform int u_layer;

void main()
{
  /*
    As of now, use RGBA-Values instead of just depth.
    // gl_FragData[u_layer].a = gl_FragCoord.z;
  */

  gl_FragData[u_layer].rgba = vec4(gl_FragCoord.z, gl_FragCoord.z, gl_FragCoord.z, 1.0f);
}