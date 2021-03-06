/**
 *  This example show how to define your own custom Zone. It will
 *  draw whatever is placed in the drawCustom() method, and the
 *  touchCustom() method defines what happens when it is touched.
 *
 *  These methods are based of the name given to the Zone, so
 *  your Zone "Test123" would use the methods: drawTest123() and
 *  touchTest123()
 */

import vialab.SMT.*;
void setup() {
  size(400, 400, P3D);
  TouchClient.init(this, TouchSource.MOUSE);
  TouchClient.add(new Zone("Custom",0,0,200,200));
}
void draw() {
  background(79, 129, 189);
}
void drawCustom(){
  fill(0);
  rect(0,0,200,200);
  fill(255);
  ellipse(100,100,100,100); 
}
void touchCustom(Zone z){
  z.rst();
}
