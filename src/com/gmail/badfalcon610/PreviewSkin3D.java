package com.gmail.badfalcon610;

import static javax.media.opengl.GL.*;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.*;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class PreviewSkin3D implements GLEventListener {

	private GL2 gl;
	FPSAnimator animator;

	int prevMouseX;
	int prevMouseY;

	private float scale = 0.2f;

	float angleX = 0.0f;
	float angleY = 0.0f;

	boolean moveStop = false;
	private static final float MAX_ANGLE = 30.0f;
	private float limbAngle;
	private boolean limbMode;
	private static final double MAX_Z_ANGLE = 1.0;
	private float limbzAngle;
	private boolean limbzMode;
	private double rad;

	float zoom;

	boolean hideHead = false;
	boolean hideBody = false;
	boolean hideArm = false;
	boolean hideLeg = false;
	boolean hideOuter = false;
	boolean hideInner = false;

	private Texture groundTex;
	private Texture texture;
	TextureData newtex = null;

	private final String grassPass = "img/grass.png";

	// TEXTURE START POSITION
	// HEAD,BODY,RARM,RLEG,LARM,LLEG
	int[] OUT = { 32, 48, 16, 16, 0, 16, 0, 0, 40, 16, 48, 0 };
	int[] IN = { 0, 48, 16, 32, 0, 32, 16, 0, 40, 32, 32, 0 };

	// TEXTURE SIZE
	// WIDTH,HEIGHT,DEPTH
	int[] HEAD = { 8, 8, 8 };
	int[] BODY = { 8, 12, 4 };
	int[] LIMB = { 4, 12, 4 };
	int[] SLIMB = { 3, 12, 4 };

	public PreviewSkin3D(GLAutoDrawable canvas) {
		canvas.addGLEventListener(this);

		canvas.getAnimator();
	}

	public void retex() {
		BufferedImage source = Canvas.change;
		newtex = AWTTextureIO.newTextureData(GLProfile.getDefault(), source,
				true);

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		if (newtex != null) {
			texture = TextureIO.newTexture(newtex);
			texture.enable(gl);
			texture.bind(gl);
			newtex = null;
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glDepthFunc(GL.GL_LEQUAL);
		}
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glPushMatrix();

		gl.glTranslatef(0.0f, 0.0f, scale * zoom);
		drawAll();

		gl.glPopMatrix();

	}

	void drawAll() {

		// grass
		if (!(20.0 * Math.sin(Math.toRadians(angleX)) < -scale
				* (LIMB[1] + BODY[1] / 2))) {
			groundTex.enable(gl);
			groundTex.bind(gl);

			gl.glPushMatrix();
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S,
					GL.GL_REPEAT);
			gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T,
					GL.GL_REPEAT);

			gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);

			drawGrass(30);

			gl.glPopMatrix();
		}

		// playerpreset

		// player
		texture.enable(gl);
		texture.bind(gl);

		gl.glPushMatrix();
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

		gl.glRotatef(angleX, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(angleY, 0.0f, 1.0f, 0.0f);

		if (!hideInner) {
			gl.glDisable(GL_BLEND);
			drawPlayer(1.0f, IN);
		}
		if (!hideOuter) {
			gl.glEnable(GL_BLEND);
			drawPlayer(1.1f, OUT);
		}

		gl.glPopMatrix();

		if (!moveStop) {
			if (limbMode) {
				limbAngle -= 1.0f;
				if (limbAngle <= -MAX_ANGLE) {
					limbMode = false;
				}
			} else {
				limbAngle += 1.0f;
				if (limbAngle >= MAX_ANGLE) {
					limbMode = true;
				}
			}
		}
		if (limbzMode) {
			rad -= 1.0 / 90.0;
			limbzAngle = (float) ((rad * Math.PI + Math.PI) + 1);
			if (rad <= -MAX_Z_ANGLE) {
				limbzMode = false;
			}
		} else {
			rad += 1.0 / 2.0 / 90.0;
			limbzAngle = (float) ((rad * Math.PI + Math.PI) + 1);
			if (rad >= MAX_Z_ANGLE) {
				limbzMode = true;
			}
		}


	}

	void drawGrass(int n) {
		gl.glPushMatrix();

		gl.glTranslatef(0.0f, scale * -(LIMB[1] + BODY[1] / 2), 0.0f);
		gl.glRotatef(270f, 1.0f, 0.0f, 0.0f);

		gl.glBegin(GL2.GL_POLYGON);

		// 左下
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(-n, -n, 0);
		// 右下
		gl.glTexCoord2f(0, n);
		gl.glVertex3f(-n, n, 0);
		// 右上
		gl.glTexCoord2f(n, n);
		gl.glVertex3f(n, n, 0);
		// 左上
		gl.glTexCoord2f(n, 0);
		gl.glVertex3f(n, -n, 0);

		gl.glEnd();

		gl.glPopMatrix();
	}

	void drawPlayer(float r, int[] offset) {
		// 頭
		if (!hideHead) {

			gl.glPushMatrix();

			gl.glTranslatef(0.0f, scale * (HEAD[1] + BODY[1]) / 2, 0.0f);
			drawBox(gl, r, offset[0], offset[1], HEAD[0], HEAD[1], HEAD[2], false);

			gl.glPopMatrix();
		}
		if (!offset.equals(OUT) || !SkinEditor.isOld()) {

			// 体
			if (!hideBody) {
				gl.glPushMatrix();

				drawBox(gl, r, offset[2], offset[3], BODY[0], BODY[1], BODY[2], false);

				gl.glPopMatrix();
			}

			if (SkinEditor.isOld()) {
				// 右足
				if (!hideLeg) {
					drawLimbs(gl, r-0.01f, offset[4], offset[5], LIMB[0], LIMB[1], LIMB[2],
							-LIMB[0] / 2, -BODY[1] / 2, limbAngle, 0, 0, false);
				}
				// 右手
				if (!hideArm) {
					drawLimbs(gl, r, offset[8], offset[9], LIMB[0], LIMB[1], LIMB[2],
							-(LIMB[0] + BODY[0]) / 2, LIMB[1] / 2, -limbAngle, -limbzAngle, 2, false);
				}
				// 左足
				if (!hideLeg) {
					drawLimbs(gl, r-0.01f, offset[4], offset[5], LIMB[0], LIMB[1], LIMB[2],
							LIMB[0] / 2, -BODY[1] / 2, -limbAngle, 0, 0, true);
				}
				// 左手
				if (!hideArm) {
					drawLimbs(gl, r, offset[8], offset[9], LIMB[0], LIMB[1], LIMB[2],
							(LIMB[0] + BODY[0]) / 2, LIMB[1] / 2, limbAngle, limbzAngle, 2, true);
				}
			} else {
				// 右足
				if (!hideLeg) {
					drawLimbs(gl, r-0.01f, offset[4], offset[5], LIMB[0], LIMB[1], LIMB[2],
							-LIMB[0] / 2, -BODY[1] / 2, limbAngle, 0, 0, false);
				}
				// 左足
				if (!hideLeg) {
					drawLimbs(gl, r-0.01f, offset[6], offset[7], LIMB[0], LIMB[1], LIMB[2],
							LIMB[0] / 2, -BODY[1] / 2, -limbAngle, 0, 0, false);
				}
				if (SkinEditor.isSlim()) {
					// 右手
					if (!hideArm) {
						drawLimbs(gl, r, offset[8], offset[9], SLIMB[0], SLIMB[1], SLIMB[2],
								-(SLIMB[0] + BODY[0]) / 2.0f, SLIMB[1] / 2, -limbAngle, -limbzAngle, 2, false);
					}
					// 左手
					if (!hideArm) {
						drawLimbs(gl, r, offset[10], offset[11], SLIMB[0], SLIMB[1], SLIMB[2],
								(SLIMB[0] + BODY[0]) / 2.0f, SLIMB[1] / 2, limbAngle, limbzAngle, 2, false);
					}
				} else {
					// 右手
					if (!hideArm) {
						drawLimbs(gl, r, offset[8], offset[9], LIMB[0], LIMB[1], LIMB[2],
								-(LIMB[0] + BODY[0]) / 2, LIMB[1] / 2, -limbAngle, -limbzAngle, 2, false);
					}
					// 左手
					if (!hideArm) {
						drawLimbs(gl, r, offset[10], offset[11], LIMB[0], LIMB[1], LIMB[2],
								(LIMB[0] + BODY[0]) / 2, LIMB[1] / 2, limbAngle, limbzAngle, 2, false);
					}
				}
			}
		}

	}

	void drawLimbs(GL2 gl, float r, int textx, int texty, int width,
			int height, int depth, float x, float y, float angle, float zangle, int joint, boolean reverse) {
		gl.glPushMatrix();
		gl.glTranslatef(scale * x, scale * y - scale * joint, 0.0f);
		gl.glRotatef(angle, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(zangle, 0.0f, 0.0f, 1.0f);
		drawParts(gl, r, textx, texty, width, height, depth, joint, reverse);
		gl.glPopMatrix();
	}

	void drawParts(GL2 gl, float r, int textx, int texty, int width,
			int height, int depth, int joint, boolean reverse) {
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -scale * height / 2 + scale * joint, 0.0f);
		drawBox(gl, r, textx, texty, width, height, depth, reverse);
		gl.glPopMatrix();
	}

	void drawBox(GL2 gl, float r, int textx, int texty, int width, int height,
			int depth, boolean reverse) {

		// 背面
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -(scale * depth / 2 * r));
		gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		drawFace(gl, r, textx + (depth * 2) + width, texty, width, height, reverse);
		gl.glPopMatrix();

		// 下面
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -(scale * height / 2 * r), 0.0f);
		gl.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		drawFace(gl, r, textx + depth + width, texty + height, width, depth, reverse);
		gl.glPopMatrix();

		// 右面
		gl.glPushMatrix();
		if (reverse) {
			gl.glTranslatef((scale * width / 2 * r), 0.0f, 0.0f);
			gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		} else {
			gl.glTranslatef(-(scale * width / 2 * r), 0.0f, 0.0f);
			gl.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		}
		drawFace(gl, r, textx, texty, depth, height, reverse);
		gl.glPopMatrix();

		// 左面
		gl.glPushMatrix();
		if (reverse) {
			gl.glTranslatef(-(scale * width / 2 * r), 0.0f, 0.0f);
			gl.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		} else {
			gl.glTranslatef((scale * width / 2 * r), 0.0f, 0.0f);
			gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		}
		drawFace(gl, r, textx + depth + width, texty, depth, height, reverse);
		gl.glPopMatrix();

		// 上面
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, scale * height / 2 * r, 0.0f);
		gl.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		drawFace(gl, r, textx + depth, texty + height, width, depth, reverse);
		gl.glPopMatrix();

		// 正面
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, scale * depth / 2 * r);
		gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		drawFace(gl, r, textx + depth, texty, width, height, reverse);
		gl.glPopMatrix();

	}

	void drawFace(GL2 gl, float r, int x, int y, int w, int h, boolean reverse) {
		gl.glPushMatrix();
		if (SkinEditor.isOld() && reverse) {
			gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		}
		gl.glBegin(GL2.GL_POLYGON);

		// 左上
		gl.glTexCoord2f(1.0f / 64 * (x + w), 1.0f / 64 * (64 - y));
		gl.glVertex3f(-scale * w / 2 * r, -scale * h / 2 * r, 0.0f);
		// 右上
		gl.glTexCoord2f(1.0f / 64 * x, 1.0f / 64 * (64 - y));
		gl.glVertex3f(scale * w / 2 * r, -scale * h / 2 * r, 0.0f);
		// 右下
		gl.glTexCoord2f(1.0f / 64 * x, 1.0f / 64 * (64 - (y + h)));
		gl.glVertex3f(scale * w / 2 * r, scale * h / 2 * r, 0.0f);
		// 左下
		gl.glTexCoord2f(1.0f / 64 * (x + w), 1.0f / 64 * (64 - (y + h)));
		gl.glVertex3f(-scale * w / 2 * r, scale * h / 2 * r, 0.0f);

		gl.glEnd();
		gl.glPopMatrix();
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

	@Override
	public void init(GLAutoDrawable drawable) {
		zoom = 0.0f;
		gl = drawable.getGL().getGL2();

		try {
			groundTex = TextureIO.newTexture(getClass().getResource(grassPass),
					true, "PNG");
		} catch (GLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedImage source = SkinEditor.source;
		texture = AWTTextureIO.newTexture(GLProfile.getDefault(), source, true);

		gl.glClearColor(0.0f, 0.5f, 1.0f, 0.0f);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);

		gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		float ratio = (float) height / (float) width;
		gl.glViewport(0, 0, width, height);

		// 定数はGLではなく、GL2にあります。
		// （正確にはjavax.media.opengl.fixedfunc.GLMatrixFuncみたい）
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustum(-1.0f, 1.0f, -ratio, ratio, 5.0f, 40.0f);

		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -20.0f);

	}

}
