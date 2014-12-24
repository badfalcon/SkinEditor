package com.gmail.badfalcon610;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RasterFormatException;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class Canvas extends JPanel implements ComponentListener {

	/*
	 * public static void main(String[] args) { //new TestFrame(new Canvas());
	 * new SkinEditor(); }
	 */

	Color backgroundcolor;
	Color foregroundcolor;

	static Point mousecurrent;
	static Point mouseprevious;
	static Point start;
	static Point finish;
	static Point dotcurrent;
	static Point dotprevious;

	BufferedImage layer1;
	static BufferedImage selectedImage;
	static BufferedImage copyOfSource;
	static BufferedImage change;
	static BufferedImage pasteImage;

	static Graphics2D glayer1;

	int editNum;

	UndoManager undoManager;

	private boolean slim;
	private boolean old;

	static boolean entered;
	static boolean selected;
	static boolean pressed;
	static boolean released;
	boolean changed;
	static boolean selectedinside;
	static boolean grab;
	static boolean grabbing;
	static boolean release;
	static boolean update;

	static Color primaryColor;

	final int MINIMUM_SCALE = 6;
	static int scale;

	Rectangle select;
	Rectangle mainbound;

	int width;
	int height;

	int dotheight;

	private int marginTop;
	private int marginBottom;
	private int marginLeft;
	private int marginRight;

	Point shapeStart;
	int shapeWidth;
	int shapeHeight;

	boolean debug;

	static Clipboard cb;

	public Canvas() {
		debug = true;

		scale = 10;
		dotheight = 64;
		selected = false;
		grab = false;
		grabbing = false;
		update = false;

		cb = new Clipboard("image");

		setMinimumSize(new Dimension(MINIMUM_SCALE * 72, MINIMUM_SCALE
				* (dotheight + 8)));
		setPreferredSize(new Dimension(scale * 72, scale * (dotheight + 8)));
		addComponentListener(this);

		CanvasMouseListener ml = new CanvasMouseListener();
		addMouseListener(ml);
		addMouseMotionListener(ml);
		addMouseWheelListener(ml);

		mousecurrent = new Point(0, 0);
		mouseprevious = new Point(0, 0);
		start = new Point(-1, -1);
		finish = new Point(0, 0);
		dotcurrent = new Point(0, 0);
		dotprevious = new Point(-1, -1);

		select = new Rectangle();

		selectedImage = new BufferedImage(64, 64,
				BufferedImage.TYPE_INT_ARGB_PRE);
		width = 72 * scale;
		height = (dotheight + 8) * scale;
	}

	public void getShapeBounds(boolean b) {
		shapeStart = new Point();
		if (b) {
			shapeWidth = Math.abs(finish.x - start.x);
			shapeHeight = Math.abs(finish.y - start.y);
		} else {
			shapeWidth = finish.x - start.x;
			shapeHeight = finish.y - start.y;
		}
		if (start.x > finish.x) {
			shapeStart.x = finish.x;
		} else {
			shapeStart.x = start.x;
		}
		if (start.y > finish.y) {
			shapeStart.y = finish.y;
		} else {
			shapeStart.y = start.y;
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		getStatus();
		rescale(getWidth(), getHeight());

		Graphics2D display = (Graphics2D) g.create();

		BufferedImage buffer = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D gbuffer = buffer.createGraphics();

		// 周りの背景
		gbuffer.setPaint(Color.LIGHT_GRAY);
		gbuffer.fill(new Rectangle(0, 0, width, height));

		// メインの背景
		gbuffer.setPaint(backgroundcolor);
		gbuffer.fill(mainbound);

		if (changed || update) {
			layer1 = deepCopy(SkinEditor.source);
			if (changed) {
				changed = false;
			}
		}

		// ???
		if (grab) {
			BufferedImage temp = deepCopy(SkinEditor.source);
			selectedImage = temp.getSubimage(select.x, select.y, select.width,
					select.height);
			clearRect(SkinEditor.source, select.x, select.y, select.width,
					select.height);
			grab = false;
			SkinEditor.editItemList[2].setEnabled(true);
			SkinEditor.editItemList[3].setEnabled(true);
			grabbing = true;
		}

		change = deepCopy(layer1);
		glayer1 = change.createGraphics();

		// ドラッグ中の図をglayerに適応
		try {
			if (pressed) {
				finish = new Point(dotcurrent);

				switch (SkinEditor.tool) {
				case 0:
					// brush
					glayer1.setPaint(primaryColor);
					if (dotprevious.x != -1 && dotprevious.y != -1) {
						glayer1.drawLine(dotprevious.x, dotprevious.y,
								dotcurrent.x, dotcurrent.y);
					} else if (layer1.getRGB(dotcurrent.x, dotcurrent.y) != primaryColor
							.getRGB()) {
						glayer1.drawLine(dotcurrent.x, dotcurrent.y,
								dotcurrent.x, dotcurrent.y);
					}
					break;
				case 1:
					// eraser
					if (dotprevious.x != -1 && dotprevious.y != -1) {
						clearLine(change, dotprevious.x, dotprevious.y,
								dotcurrent.x, dotcurrent.y);
					} else if (change.getRGB(dotcurrent.x, dotcurrent.y) != IU
							.argb(0, 0, 0, 0)) {
						clearLine(change, dotcurrent.x, dotcurrent.y,
								dotcurrent.x, dotcurrent.y);
					}
					break;
				case 2:
					// dropper
					break;
				case 3:
					// bucket
					break;
				case 4:
					// line
					if (start.x != -1) {
						glayer1.setPaint(primaryColor);
						glayer1.drawLine(start.x, start.y, finish.x, finish.y);
					}
					break;
				case 5:
					// square
					if (start.x != -1) {
						getShapeBounds(true);
						glayer1.setPaint(primaryColor);
						glayer1.drawRect(shapeStart.x, shapeStart.y,
								shapeWidth, shapeHeight);
					}
					break;
				case 6:
					// fill square
					if (start.x != -1) {
						getShapeBounds(true);
						glayer1.setPaint(primaryColor);
						glayer1.fillRect(shapeStart.x, shapeStart.y,
								shapeWidth + 1, shapeHeight + 1);
					}
					break;
				case 7:
					// ellipse
					if (start.x != -1) {
						getShapeBounds(true);
						glayer1.setPaint(primaryColor);
						glayer1.draw(new Ellipse2D.Double(shapeStart.x,
								shapeStart.y, shapeWidth, shapeHeight));
					}
					break;
				case 8:
					// fill ellipse
					if (start.x != -1) {
						getShapeBounds(true);
						glayer1.setPaint(primaryColor);
						glayer1.fill(new Ellipse2D.Double(shapeStart.x,
								shapeStart.y, shapeWidth, shapeHeight));
					}
					break;
				case 9:
					// fill eraser
					if (start.x != -1) {
						getShapeBounds(true);
						clearRect(change, shapeStart.x, shapeStart.y,
								shapeWidth + 1, shapeHeight + 1);
					}
					break;
				case 10:
					// selection
					break;
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {

		}

		Cursor c;
		if (grabbing) {
			// マウスを十字矢印に
			c = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
		} else {

			c = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		}
		setCursor(c);

		glayer1.drawImage(selectedImage, select.x, select.y, this);

		if (release) {

			BufferedImage change = deepCopy(SkinEditor.source);
			Graphics2D gchange = change.createGraphics();
			gchange.drawImage(selectedImage, select.x, select.y, this);
			gchange.dispose();
			selectedImage = new BufferedImage(64, 64,
					BufferedImage.TYPE_INT_ARGB_PRE);

			updateUndo(change);

			SkinEditor.editItemList[2].setEnabled(false);
			SkinEditor.editItemList[3].setEnabled(false);
			release = false;
			grabbing = false;
		}

		glayer1.dispose();

		if (selected) {
			// clearRect(layer1, select.x, select.y, select.width,
			// select.height);
			adaptSelect(layer1, change);
		} else {
			layer1 = deepCopy(change);
		}

		gbuffer.drawImage(layer1, mainbound.x, mainbound.y, mainbound.x + 64
				* scale, mainbound.y + dotheight * scale, 0, 0, 64, dotheight,
				this);
		/*
		 * gbuffer.drawImage(layer1, mainbound.x, mainbound.y, 64 * scale,
		 * dotheight * scale, this);
		 */
		if (entered) {
			// マウス位置に現在色を描画
			if (SkinEditor.tool != 1 && SkinEditor.tool != 2
					&& SkinEditor.tool != 9 && SkinEditor.tool != 10) {
				gbuffer.setPaint(primaryColor);
				gbuffer.fill(new Rectangle(mainbound.x + dotcurrent.x * scale,
						mainbound.y + dotcurrent.y * scale, scale, scale));
			}
			if (SkinEditor.tool != 10) {
				// マウス位置の周りに現在色の補色を描画
				Color reverse = new Color(255 - primaryColor.getRed(),
						255 - primaryColor.getGreen(),
						255 - primaryColor.getBlue(), primaryColor.getAlpha());
				gbuffer.setPaint(reverse);
				gbuffer.draw(new Rectangle(mainbound.x + dotcurrent.x * scale
						- 1, mainbound.y + dotcurrent.y * scale - 1, scale + 1,
						scale + 1));
			}
		}

		// selected area
		if (selected) {
			float[] dash = { 6.0f, 6.0f };
			BasicStroke dashStroke = new BasicStroke(2.0f,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash,
					0.0f);
			gbuffer.setPaint(Color.WHITE);
			gbuffer.setStroke(dashStroke);

			gbuffer.draw(new Rectangle(mainbound.x + select.x * scale,
					mainbound.y + select.y * scale, select.width * scale,
					select.height * scale));
		}

		// overlay template
		if (!SkinEditor.hideTemplate) {
			gbuffer.setPaint(foregroundcolor);

			drawTemplate(gbuffer);

		}

		gbuffer.dispose();

		// 画面に描画
		display.drawImage(buffer, 0, 0, this);
		display.dispose();
	}

	private void getStatus() {
		old = SkinEditor.isOld();
		slim = SkinEditor.isSlim();
	}

	private final int[] headRatio = new int[] { 8, 8, 8 };
	private final int[] bodyRatio = new int[] { 8, 12, 4 };
	private final int[] limbRatio = new int[] { 4, 12, 4 };
	private final int[] slimRatio = new int[] { 3, 12, 4 };

	String[] partsName = { "OUTER", "INNER", "HEAD", "BODY", "ARM(R)",
			"ARM(L)", "LEG(R)", "LEG(L)" };

	void drawTemplate(Graphics2D g2) {

		int[] arm;
		if (slim) {
			arm = slimRatio;
		} else {
			arm = limbRatio;
		}

		// inner head
		drawExpansion(g2, new Point(mainbound.x, mainbound.y), headRatio,
				partsName[1], partsName[2]);
		// inner body
		drawExpansion(g2, new Point(mainbound.x + 16 * scale, mainbound.y + 16
				* scale), bodyRatio, partsName[1], partsName[3]);
		// inner arm(R)
		drawExpansion(g2, new Point(mainbound.x + 40 * scale, mainbound.y + 16
				* scale), arm, partsName[1], partsName[4]);
		if (!old) {
			// inner arm(L)
			drawExpansion(g2, new Point(mainbound.x + 32 * scale, mainbound.y
					+ 48 * scale), arm, partsName[1], partsName[5]);
		}
		// inner leg(R)
		drawExpansion(g2, new Point(mainbound.x, mainbound.y + 16 * scale),
				limbRatio, partsName[1], partsName[6]);
		if (!old) {
			// inner leg(L)
			drawExpansion(g2, new Point(mainbound.x + 16 * scale, mainbound.y
					+ 48 * scale), limbRatio, partsName[1], partsName[7]);
		}

		// outer head
		drawExpansion(g2, new Point(mainbound.x + 32 * scale, mainbound.y),
				headRatio, partsName[0], partsName[2]);
		if (!old) {
			// outer body
			drawExpansion(g2, new Point(mainbound.x + 16 * scale, mainbound.y
					+ 32 * scale), bodyRatio, partsName[0], partsName[3]);
			// outer arm(R)
			drawExpansion(g2, new Point(mainbound.x + 40 * scale, mainbound.y
					+ 32 * scale), arm, partsName[0], partsName[4]);
			// outer arm(L)
			drawExpansion(g2, new Point(mainbound.x + 48 * scale, mainbound.y
					+ 48 * scale), arm, partsName[0], partsName[5]);
			// outer leg(R)
			drawExpansion(g2, new Point(mainbound.x, mainbound.y + 32 * scale),
					limbRatio, partsName[0], partsName[6]);
			// outer leg(L)
			drawExpansion(g2, new Point(mainbound.x, mainbound.y + 48 * scale),
					limbRatio, partsName[0], partsName[7]);
		}
	}

	void drawExpansion(Graphics2D g2, Point p, int[] size, String inout,
			String partName) {
		Point p2 = new Point(p.x + 1, p.y + 1);
		int width = size[0] * scale;
		int height = size[1] * scale;
		int depth = size[2] * scale;

		Font font = new Font(g2.getFont().getFamily(), Font.PLAIN,
				scale * 2 - 1);
		g2.setFont(font);
		drawWord(g2, inout, p2, size[2], size[2] + 2);
		drawWord(g2, partName, p2, size[2], size[2] + 4);

		g2.setStroke(new BasicStroke(2));
		font = new Font(g2.getFont().getFamily(), Font.PLAIN, scale * 2 - 3);
		g2.setFont(font);

		// up
		drawWord(g2, "u", p2, size[2], size[2]);
		g2.draw(new Rectangle(p2.x + depth, p2.y, width - 2, depth - 2));
		// down
		drawWord(g2, "d", p2, size[2] + size[0], size[2]);
		g2.draw(new Rectangle(p2.x + depth + width, p2.y, width - 2, depth - 2));
		// right
		drawWord(g2, "r", p2, 0, size[2] + size[1]);
		g2.draw(new Rectangle(p2.x, p2.y + depth, depth - 2, height - 2));
		// front
		drawWord(g2, "f", p2, size[2], size[2] + size[1]);
		g2.draw(new Rectangle(p2.x + depth, p2.y + depth, width - 2, height - 2));
		// left
		drawWord(g2, "l", p2, size[2] + size[0], size[2] + size[1]);
		g2.draw(new Rectangle(p2.x + depth + width, p2.y + depth, depth - 2,
				height - 2));
		// back
		drawWord(g2, "b", p2, size[2] * 2 + size[0], size[2] + size[1]);
		g2.draw(new Rectangle(p2.x + depth * 2 + width, p2.y + depth,
				width - 2, height - 2));

		// reset
		g2.setStroke(new BasicStroke());
	}

	void drawWord(Graphics2D g2, String str, Point p, int xadd, int yadd) {
		g2.drawString(str, (float) (p.x + (xadd + 0.5) * scale), (float) (p.y
				+ (yadd - 1.5) * scale + (g2.getFontMetrics().getAscent() / 2)));
	}

	@Override
	public void componentResized(ComponentEvent e) {
		width = e.getComponent().getWidth();
		height = e.getComponent().getHeight();

		rescale(width, height);
	}

	public void rescale(int width, int height) {
		double widthscale = width / 72;
		double heightscale = height / (dotheight + 8);

		if (widthscale < heightscale) {
			scale = (int) widthscale;
		} else {
			scale = (int) heightscale;
		}

		setPreferredSize(new Dimension(scale * 72, scale * (dotheight + 8)));

		repaint();
		marginTop = (height - scale * dotheight) / 2;
		marginBottom = (height + scale * dotheight) / 2;
		marginLeft = (width - scale * 64) / 2;
		marginRight = (width + scale * 64) / 2;

		mainbound = new Rectangle(marginLeft, marginTop, 64 * scale, dotheight
				* scale);
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	@Override
	public void componentHidden(ComponentEvent e) {
	}

	public void clearLine(BufferedImage image, int x1, int y1, int x2, int y2) {
		if ((x2 - x1) == 0 || Math.abs((y2 - y1) / (x2 - x1)) > 1) {
			if (Math.signum(y2 - y1) == -1) {
				for (int y = y1; y >= y2; y += Math.signum(y2 - y1)) {
					int x = (x2 - x1) * (y - y1) / (y2 - y1) + x1;
					image.setRGB(x, y, IU.argb(0, 0, 0, 0));
				}
			} else {
				for (int y = y1; y <= y2; y += Math.signum(y2 - y1)) {
					int x = (x2 - x1) * (y - y1) / (y2 - y1) + x1;
					image.setRGB(x, y, IU.argb(0, 0, 0, 0));
				}
			}
		} else {
			if (Math.signum(x2 - x1) == -1) {
				for (int x = x1; x >= x2; x += Math.signum(x2 - x1)) {
					int y = (y2 - y1) * (x - x1) / (x2 - x1) + y1;
					image.setRGB(x, y, IU.argb(0, 0, 0, 0));
				}
			} else {
				for (int x = x1; x <= x2; x += Math.signum(x2 - x1)) {
					int y = (y2 - y1) * (x - x1) / (x2 - x1) + y1;
					image.setRGB(x, y, IU.argb(0, 0, 0, 0));
				}
			}
		}
	}

	public class CanvasMouseListener extends MouseAdapter {

		void scanLineFill(int x, int y, Color c) {
			glayer1 = change.createGraphics();
			// Graphics2D gsource = SkinEditor.source.createGraphics();
			List<Point> buffer = new ArrayList<Point>();
			buffer.add(new Point(x, y));

			while (buffer.size() != 0) {
				Point point = buffer.get(0);
				buffer.remove(point);

				if (new Color(change.getRGB(point.x, point.y), true)
						.equals(primaryColor)) {
					continue;
				}

				int leftx = point.x;
				for (int i = point.x - 1; 0 <= i; i--) {
					if (!new Color(change.getRGB(i, point.y), true).equals(c)) {
						leftx = i + 1;
						break;
					} else {
						leftx = i;
					}
				}

				int rightx = point.x;
				for (int i = point.x + 1; i <= 64 - 1; i++) {
					if (!new Color(change.getRGB(i, point.y), true).equals(c)) {
						rightx = i - 1;
						break;
					} else {
						rightx = i;
					}
				}
				glayer1.setPaint(primaryColor);
				glayer1.drawLine(leftx, point.y, rightx, point.y);

				if (point.y + 1 < 64) {
					buffer = scanLine(leftx, rightx, point.y + 1, c, buffer);
				}
				if (0 <= point.y - 1) {
					buffer = scanLine(leftx, rightx, point.y - 1, c, buffer);
				}
			}
			glayer1.dispose();
		}

		List<Point> scanLine(int leftx, int rightx, int y, Color c,
				List<Point> buffer) {
			while (leftx <= rightx) {
				for (; leftx <= rightx; leftx++) {
					if (new Color(SkinEditor.source.getRGB(leftx, y), true)
							.equals(c)) {
						break;
					}
				}

				if (rightx < leftx) {
					break;
				}

				for (; leftx <= rightx; leftx++) {
					if (!new Color(SkinEditor.source.getRGB(leftx, y), true)
							.equals(c)) {
						break;
					}
				}
				buffer.add(new Point(leftx - 1, y));
			}
			return buffer;
		}

		public void mousePressed(MouseEvent e) {

			// copyOfSource = deepCopy(SkinEditor.source);
			pressed = true;

			dotprevious.x = -1;
			dotprevious.y = -1;

			shapeWidth = 0;
			shapeHeight = 0;

			shapeStart = new Point(-1, -1);

			mousecurrent.x = e.getX() - marginLeft;
			mousecurrent.y = e.getY() - marginTop;

			dotcurrent = new Point((int) (mousecurrent.x / scale),
					(int) (mousecurrent.y / scale));

			if (mousecurrent.x >= 0 && mousecurrent.x < 64 * scale
					&& mousecurrent.y >= 0 && mousecurrent.y < 64 * scale) {

				int btn = e.getButton();

				if (btn == MouseEvent.BUTTON1) {
					switch (SkinEditor.tool) {
					case 0:
						break;
					case 1:
						break;
					case 4:
					case 5:
					case 6:
					case 7:
					case 8:
					case 9:
						start.x = dotcurrent.x;
						start.y = dotcurrent.y;
						update = true;
						break;
					case 10:
						start.x = dotcurrent.x;
						start.y = dotcurrent.y;
						if (insideSelected()) {

							if (!grabbing) {

								grab = true;
							}
						} else {

							if (grabbing) {
								release = true;
							}
						}
						update = true;
						break;
					default:
						break;
					}
					repaint();
					SkinEditor.preview.retex();
				} else if (btn == MouseEvent.BUTTON2) {
				} else if (btn == MouseEvent.BUTTON3) {

					SkinEditor.togglebuttons[2].doClick();
					repaint();
				}
			}
		}

		public void mouseDragged(MouseEvent e) {

			mouseprevious.x = mousecurrent.x;
			mouseprevious.y = mousecurrent.y;

			boolean moved = false;
			if (updateMousePlace(e)) {
				moved = true;
				dotprevious.x = dotcurrent.x;
				dotprevious.y = dotcurrent.y;

				dotcurrent.x = (int) (mousecurrent.x / scale);
				dotcurrent.y = (int) (mousecurrent.y / scale);
			}
			if (mousecurrent.x >= 0 && mousecurrent.x < 64 * scale
					&& mousecurrent.y >= 0 && mousecurrent.y < 64 * scale) {

				switch (SkinEditor.tool) {
				case 0:
					SkinEditor.colorchooser.addToHistory(primaryColor);
					break;
				case 10:
					if (start.x != -1) {
						if (grabbing) {
							getShapeBounds(false);
							// inside
							// select.setLocation(dotcurrent);
							if (moved) {
								moveselect();
							}
						} else {
							getShapeBounds(true);
							// outside
							if (shapeWidth > 1 || shapeHeight > 1) {
								select = new Rectangle(shapeStart.x,
										shapeStart.y, shapeWidth + 1,
										shapeHeight + 1);
								SkinEditor.editItemList[2].setEnabled(true);
								SkinEditor.editItemList[3].setEnabled(true);
								selected = true;
							} else {
								SkinEditor.editItemList[2].setEnabled(false);
								SkinEditor.editItemList[3].setEnabled(false);
								selected = false;
							}
						}
					}
					break;
				default:
					break;
				}
				repaint();
				SkinEditor.preview.retex();
			}
		}

		public void mouseReleased(MouseEvent e) {

			mouseprevious.x = mousecurrent.x;
			mouseprevious.y = mousecurrent.y;

			if (updateMousePlace(e)) {
				dotprevious.x = dotcurrent.x;
				dotprevious.y = dotcurrent.y;

				dotcurrent.x = (int) (mousecurrent.x / scale);
				dotcurrent.y = (int) (mousecurrent.y / scale);
			}

			finish = new Point(dotcurrent);

			if (mousecurrent.x >= 0 && mousecurrent.x < 64 * scale
					&& mousecurrent.y >= 0 && mousecurrent.y < 64 * scale) {

				switch (SkinEditor.tool) {
				case 0:
					SkinEditor.colorchooser.addToHistory(primaryColor);
					break;
				case 1:
					break;
				case 2:
					if (change.getRGB(dotcurrent.x, dotcurrent.y) != 0) {
						ColorChooser.setMainColor(new Color(change.getRGB(
								dotcurrent.x, dotcurrent.y), true));
					}
					if (SkinEditor.previoustool != -1) {
						SkinEditor.togglebuttons[SkinEditor.previoustool]
								.doClick();
					} else {
						SkinEditor.togglebuttons[0].doClick();
					}
					break;
				case 3:
					Color current = new Color(change.getRGB(dotcurrent.x,
							dotcurrent.y), true);
					if (!current.equals(primaryColor)) {
						scanLineFill(dotcurrent.x, dotcurrent.y, current);
						SkinEditor.colorchooser.addToHistory(primaryColor);
					}
					break;
				case 4:
					// draw line
				case 5:
					// draw rectangle
				case 6:
					// draw filled rectangle
				case 7:
					// draw ellipse
				case 8:
					// draw filled ellipse
					SkinEditor.colorchooser.addToHistory(primaryColor);
				case 9:
					// erase filled rectangle
					resetStart();
					update = false;
					break;
				case 10:
					// rectangle selection
					if (start.x != -1) {
						if (grabbing) {
							getShapeBounds(false);
							// inside
						} else {
							getShapeBounds(true);
							// outside
							if (shapeWidth > 1 || shapeHeight > 1) {
								select = new Rectangle(shapeStart.x,
										shapeStart.y, shapeWidth + 1,
										shapeHeight + 1);
								SkinEditor.editItemList[2].setEnabled(true);
								SkinEditor.editItemList[3].setEnabled(true);
								selected = true;
							} else {
								SkinEditor.editItemList[2].setEnabled(false);
								SkinEditor.editItemList[3].setEnabled(false);
								selected = false;
							}
						}
					}
					resetStart();
					update = false;
					break;
				default:
					break;
				}
			}

			if (!grabbing) {
				// changes
				updateUndo(layer1);
			}

			repaint();
			SkinEditor.preview.retex();
			pressed = false;

		}

		public void resetStart() {
			start.x = -1;
			start.y = -1;
		}

		public void mouseMoved(MouseEvent e) {
			boolean updated = updateMousePlace(e);
			if (updated) {
				dotcurrent.x = (int) (mousecurrent.x / scale);
				dotcurrent.y = (int) (mousecurrent.y / scale);
				repaint();
				SkinEditor.preview.retex();
			}
		}

		public void mouseEntered(MouseEvent e) {
			Cursor c = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
			Component p = (Component) e.getSource();
			p.setCursor(c);
		}

		public void mouseExited(MouseEvent e) {
			Cursor c = Cursor.getDefaultCursor();
			Component p = (Component) e.getSource();
			p.setCursor(c);
		}

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (SkinEditor.tool + e.getWheelRotation() >= 0
					&& SkinEditor.tool + e.getWheelRotation() < SkinEditor.togglebuttons.length) {
				SkinEditor.tool += e.getWheelRotation();
				SkinEditor.togglebuttons[SkinEditor.tool].doClick();
				SkinEditor.configuration.setProperty("selectedTool",
						String.valueOf(SkinEditor.tool));
			}
		}

	}

	public void getChanges(BufferedImage b, BufferedImage a,
			ArrayList<unit> unitsb, ArrayList<unit> unitsa) {
		unitsb = new ArrayList<unit>();
		unitsa = new ArrayList<unit>();
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 64; j++) {
				int rgbold = b.getRGB(j, i);
				int rgbnew = a.getRGB(j, i);
				Color colorold = new Color(rgbold, true);
				Color colornew = new Color(rgbnew, true);
				if (!colorold.equals(colornew)) {
					unitsb.add(new unit(j, i, rgbold));
					unitsa.add(new unit(j, i, rgbnew));
				}
			}
		}
	}

	public Changes getChangesA(BufferedImage a) {
		ArrayList<unit> unitsb = new ArrayList<unit>();
		ArrayList<unit> unitsa = new ArrayList<unit>();
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 64; j++) {
				int rgbold = SkinEditor.source.getRGB(j, i);
				int rgbnew = a.getRGB(j, i);
				// U.say("old" + rgbold + ":new" + rgbnew);
				if (rgbold != rgbnew) {
					unitsb.add(new unit(j, i, rgbold));
					unitsa.add(new unit(j, i, rgbnew));
				}
			}
		}
		return new Changes(unitsb, unitsa);
	}

	public class Changes {
		ArrayList<unit> unitsold = new ArrayList<unit>();
		ArrayList<unit> unitsnew = new ArrayList<unit>();

		public Changes(ArrayList<unit> unitsb, ArrayList<unit> unitsa) {
			unitsold = unitsb;
			unitsnew = unitsa;
		}
	}

	public void updateUndo(BufferedImage after) {

		Changes change = getChangesA(after);
		if (change.unitsnew.size() != 0) {
			SkinEditor.source = deepCopy(after);
			editNum++;
			undoManager
					.undoableEditHappened(new UndoableEditEvent(this,
							new SetValueUndo(change.unitsold, change.unitsnew,
									editNum)));
			SkinEditor.edited = true;
			SkinEditor.updateTitle();

			changed = true;
		} else {
		}
		SkinEditor.editItemList[0].setEnabled(undoManager.canUndo());
		SkinEditor.editItemList[1].setEnabled(undoManager.canRedo());

	}

	void moveselect() {

		Point move = new Point(dotcurrent.x - dotprevious.x, dotcurrent.y
				- dotprevious.y);
		select.translate(move.x, move.y);
	}

	public boolean insideSelected() {
		if (selected) {

			if (select.x <= start.x && start.x < select.x + select.width
					&& select.y <= start.y
					&& start.y < select.y + select.height) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public void adaptSelect(BufferedImage before, BufferedImage after) {
		try {
			BufferedImage t = deepCopy(after).getSubimage(select.x, select.y,
					select.width, select.height);
			Graphics2D gsource = before.createGraphics();
			gsource.drawImage(t, select.x, select.y, this);
			gsource.dispose();
		} catch (RasterFormatException e) {

		}
	}

	public boolean updateMousePlace(MouseEvent e) {
		if (e.getX() > marginLeft && e.getX() < marginRight
				&& e.getY() > marginTop && e.getY() < marginBottom) {
			mousecurrent.x = e.getX() - marginLeft;
			mousecurrent.y = e.getY() - marginTop;
			entered = true;
		} else {
			entered = false;
			repaint();
		}
		if ((int) (mousecurrent.x / scale) != dotcurrent.x
				|| (int) (mousecurrent.y / scale) != dotcurrent.y) {
			return true;
		} else {
			return false;
		}
	}

	static int[][] changes;

	public List<unit> getChangesNew(BufferedImage b, BufferedImage a) {
		List<unit> u = new ArrayList<unit>();
		for (int i = 0; i < 64; i++) {
			for (int j = 0; j < 64; j++) {
				int rgb = a.getRGB(j, i);
				Color rgbb = new Color(b.getRGB(j, i), true);
				Color rgba = new Color(rgb, true);
				if (!rgbb.equals(rgba)) {
					u.add(new unit(j, i, rgb));
				}
			}
		}
		return u;
	}

	BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(bi.getRaster()
				.createCompatibleWritableRaster());
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	BufferedImage deepCopy0(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null)
				.getSubimage(0, 0, bi.getWidth(), bi.getHeight());
	}

	BufferedImage deepCopy1(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public void initUndo() {
		undoManager = new UndoManager();
	}

	class SetValueUndo extends AbstractUndoableEdit {
		List<unit> oldValues;
		List<unit> newValues;

		int num;

		/** コンストラクター */
		protected SetValueUndo(List<unit> before, List<unit> after, int n) {
			oldValues = before;
			newValues = after;

			num = n;
		}

		@Override
		public void undo() throws CannotUndoException {
			super.undo();
			for (int i = 0; i < oldValues.size(); i++) {
				SkinEditor.source.setRGB(oldValues.get(i).x,
						oldValues.get(i).y, oldValues.get(i).rgb);
			}
			layer1 = deepCopy(SkinEditor.source);
			editNum--;
			if (num - 1 == 0) {
				SkinEditor.edited = false;
			} else {
				SkinEditor.edited = true;
			}
			changed = true;

			SkinEditor.updateTitle();
			SkinEditor.preview.retex();

			repaint();
		}

		@Override
		public void redo() throws CannotRedoException {
			super.redo();
			for (int i = 0; i < newValues.size(); i++) {
				SkinEditor.source.setRGB(newValues.get(i).x,
						newValues.get(i).y, newValues.get(i).rgb);
			}
			editNum++;
			if (num == 0) {
				SkinEditor.edited = false;
			} else {
				SkinEditor.edited = true;
			}
			changed = true;

			SkinEditor.updateTitle();
			SkinEditor.preview.retex();

			repaint();
		}
	}

	public void copy(final boolean cut) {
		final BufferedImage image;
		if (grabbing) {
			image = deepCopy(selectedImage);
		} else {
			image = deepCopy(SkinEditor.source.getSubimage(select.x, select.y,
					select.width, select.height));
		}
		cb.setContents(new Transferable() {
			@Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return flavor.equals(DataFlavor.imageFlavor);
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			@Override
			public Object getTransferData(DataFlavor flavor)
					throws UnsupportedFlavorException, IOException {
				if (flavor.equals(DataFlavor.imageFlavor)) {
					return image;
				}
				throw new UnsupportedFlavorException(flavor);
			}
		}, null);
		if (cut) {
			BufferedImage change = deepCopy(SkinEditor.source);
			if (grabbing) {
				selectedImage = new BufferedImage(64, 64,
						BufferedImage.TYPE_INT_ARGB_PRE);
			} else {
				clearRect(change, select.x, select.y, select.width,
						select.height);
			}
			updateUndo(change);
		}
		SkinEditor.editItemList[4].setEnabled(true);
		// changed = true;
	}

	public void paste() {
		if (cb.getContents(null).isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				pasteImage = (BufferedImage) cb.getData(DataFlavor.imageFlavor);
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (selected) {
			if (grabbing) {
				Graphics2D gsource = SkinEditor.source.createGraphics();
				gsource.drawImage(selectedImage, select.x, select.y, this);
				gsource.dispose();
				selectedImage = null;
				grabbing = false;
			}
		}

		selectedImage = pasteImage;
		select = new Rectangle(0, 0, pasteImage.getWidth(),
				pasteImage.getHeight());
		grabbing = true;
		selected = true;
		SkinEditor.editItemList[2].setEnabled(true);
		SkinEditor.editItemList[3].setEnabled(true);
	}

	public void clearRect(BufferedImage source, int x1, int y1, int width,
			int height) throws ArrayIndexOutOfBoundsException {
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				source.setRGB(x1 + i, y1 + j, IU.argb(0, 0, 0, 0));
			}
		}
	}
}
