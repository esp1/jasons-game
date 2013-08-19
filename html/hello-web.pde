/**
 * Processing code for UI rendering within the canvas.
 * Everything else (model operations, DOM manipulation, various calculations) is done by delegating to ClojureScript.
 */

var z = 0;
var inc = 20;

void setup() {
  size(window.innerWidth, window.innerHeight * 0.8, P3D);
}

void draw() {
	background(200);
	lights();
	
	drawActor(mouseX, mouseY, z);
}

// Draw objects

void drawActor(int x, int y, int z) {
	pushMatrix();
	
	setStyle("actor");

	translate(x, y, z);
	sphere(100);
	
	popMatrix();
}

/**
 * Draws a word bubble over the target point
 */
void drawWordBubble(targetPoint) {
	pushMatrix();
	popMatrix();
}

// Styles

void setStyle(String name) {
	var c = ui.color_for(name);
	if (c != null) color(c[0], c[1], c[2], c[3]);
	
	var s = ui.stroke_for(name);
	if (s != null) stroke(s[0], s[1], s[2], s[3]);
	else noStroke();
	
	var f = ui.fill_for(name);
	if (f != null) fill(f[0], f[1], f[2], f[3]);
	else noFill();
}

// Event handling

void mousePressed() {
}

void mouseMoved() {
	ui.show_mouse_coords(mouseButton, mouseX, mouseY);
}

void keyPressed() {
	if (key == CODED) {
		switch (keyCode) {
		case UP: z += 20; break;
		case DOWN: z -= 20; break;
		}
	}
}
