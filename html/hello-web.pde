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
	translate(mouseX, mouseY, z);
//	pushStyle();
	noStroke();
	fill(255);
	sphere(100);
//	popStyle();
}

void doThing() {
	alert("pow");
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
		case UP: z += 20; println("z = " + z); break;
		case DOWN: z -= 20; println("z = " + z); break;
		}
	}
}
