#include "shared.h"

uniform int u_emissive;

void main()
{
  if (u_emissive == 1)
  {
    gl_FragData[0] = vec4(1.0f, 1.0f, 1.0f, 1.0f);
    return;
  }

  gl_FragData[0] = vec4(0.0f, 0.0f, 0.0f, 1.0f);
}