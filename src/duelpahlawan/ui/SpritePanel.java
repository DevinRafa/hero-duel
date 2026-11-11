package duelpahlawan.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Panel yang menampilkan animasi sprite dari spritesheet horizontal.
 * Setiap frame memiliki lebar = tinggi sheet (semua asset 96px per frame).
 * Mendukung: animasi loop (idle), animasi sekali jalan + callback (attack/hurt/death).
 */
public class SpritePanel extends JPanel {

    private BufferedImage sheet;
    private int frameCount;
    private boolean flip;
    private int frameWidth;
    private int frameHeight;
    private int currentFrame;
    private final int displaySize;
    private boolean drawShadow = false;

    private Timer timer;

    public SpritePanel(BufferedImage sheet, int frameCount, boolean flip, int displaySize) {
        this.displaySize = displaySize;
        setOpaque(false);
        setPreferredSize(new Dimension(displaySize, displaySize));
        if (sheet != null) loadSheet(sheet, frameCount, flip);
    }

    public void setDrawShadow(boolean drawShadow) {
        this.drawShadow = drawShadow;
        repaint();
    }

    /** Ganti sheet, mulai animasi loop. */
    public void loadSheet(BufferedImage sheet, int frameCount, boolean flip) {
        stopTimer();
        this.sheet      = sheet;
        this.frameCount = frameCount;
        this.flip       = flip;
        this.currentFrame = 0;
        if (sheet == null || frameCount <= 0) { repaint(); return; }
        this.frameWidth  = sheet.getWidth() / frameCount;
        this.frameHeight = sheet.getHeight();
        startLoop();
    }

    /** Putar animasi sekali, lalu panggil callback di EDT. */
    public void playOnce(BufferedImage sheet, int frameCount, boolean flip, Runnable onDone) {
        stopTimer();
        this.sheet      = sheet;
        this.frameCount = frameCount;
        this.flip       = flip;
        this.currentFrame = 0;
        if (sheet == null || frameCount <= 0) { if (onDone != null) onDone.run(); return; }
        this.frameWidth  = sheet.getWidth() / frameCount;
        this.frameHeight = sheet.getHeight();
        int delay = Math.max(60, 800 / frameCount);
        timer = new Timer(delay, null);
        timer.addActionListener(e -> {
            currentFrame++;
            if (currentFrame >= frameCount) {
                stopTimer();
                currentFrame = frameCount - 1;
                repaint();
                if (onDone != null) SwingUtilities.invokeLater(onDone);
            } else {
                repaint();
            }
        });
        timer.start();
        repaint();
    }

    private void startLoop() {
        int delay = Math.max(60, 800 / Math.max(frameCount, 1));
        timer = new Timer(delay, e -> {
            currentFrame = (currentFrame + 1) % frameCount;
            repaint();
        });
        timer.start();
        repaint();
    }

    private void stopTimer() {
        if (timer != null) { timer.stop(); timer = null; }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (sheet == null || frameCount <= 0 || frameWidth <= 0 || frameHeight <= 0) return;

        int src_x = currentFrame * frameWidth;
        int src_y = 0;
        int src_w = frameWidth;
        int src_h = frameHeight;

        int pad = 6;
        int availW = Math.max(10, getWidth()  - pad * 2);
        int availH = Math.max(10, getHeight() - pad * 2);
        double scale = Math.min((double) availW / frameWidth, (double) availH / frameHeight);
        int dst_w = (int) Math.round(frameWidth * scale);
        int dst_h = (int) Math.round(frameHeight * scale);
        int dst_x = (getWidth()  - dst_w) / 2;

        int padBottom = drawShadow ? 14 : 6;
        int dst_y = drawShadow ? (getHeight() - dst_h - padBottom) : ((getHeight() - dst_h) / 2);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (drawShadow) {
            g2.setColor(new Color(12, 10, 24, 190));
            int shadowW = (int) (dst_w * 0.55);
            int shadowH = Math.max(8, (int) (dst_h * 0.10));
            int shadowX = (getWidth() - shadowW) / 2;
            int shadowY = dst_y + dst_h - shadowH / 2;
            g2.fillOval(shadowX, shadowY, shadowW, shadowH);
        }

        if (flip) {
            g2.translate(dst_x + dst_w, dst_y);
            g2.scale(-1, 1);
            g2.drawImage(sheet, 0, 0, dst_w, dst_h,
                    src_x, src_y, src_x + src_w, src_y + src_h, null);
        } else {
            g2.drawImage(sheet, dst_x, dst_y, dst_w, dst_h,
                    src_x, src_y, src_x + src_w, src_y + src_h, null);
        }
        g2.dispose();
    }
}
