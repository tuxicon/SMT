/**
 * This shows a TextZone, any keyboard input will show up on
 * the Zone since the 5th parameter turns on listening to the
 * applet keyboard input
 */

import vialab.SMT.*;
void setup() {
  size(400, 400, P3D);
  TouchClient.init(this, TouchSource.MOUSE);
  TouchClient.add(new TextZone(0,0,200,200,true));
}
void draw() {
  background(79, 129, 189);
}
