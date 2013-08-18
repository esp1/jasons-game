var core = jasons_game.core;
var ui = jasons_game.ui;

var z = 0;
var inc = 20;

void setup() {
  size(window.innerWidth, window.innerHeight * 0.8, P3D);
}

void draw() {
	background(200);
	lights();
	
	drawActor();
}

void drawActor() {
	pushMatrix();
	
	setStyle("actor");

	translate(mouseX, mouseY, z);
	sphere(100);
	
	popMatrix();
}

void setStyle(name) {
	var c = ui.color(name); if (c) color(c[0], c[1], c[2], c[3]);
	var s = ui.stroke(name); if (s) stroke(s[0], s[1], s[2], s[3]); else noStroke();
	var f = ui.fill(name); if (f) fill(f[0], f[1], f[2], f[3]); else noFill();
}

void mousePressed() {
	ui.mousePressed(this, mouseButton, mouseX, mouseY, pmouseX, pmouseY);
}

void mouseMoved() {
	ui.mouseMoved(this, mouseButton, mouseX, mouseY, pmouseX, pmouseY);
}

void keyPressed() {
	if (key == CODED) {
		switch (keyCode) {
		case UP: z += 20; break;
		case DOWN: z -= 20; break;
		}
	}
}
