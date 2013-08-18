var z = 0;

void setup() {
  size(window.innerWidth, window.innerHeight, P3D);
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

void mousePressed() {
	println("yo")
}

void keyPressed() {
	if (key == CODED) {
		switch (keyCode) {
		case UP: z += 20; break;
		case DOWN: z -= 20; break;
		}
	}
}