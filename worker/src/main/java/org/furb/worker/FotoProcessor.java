package org.furb.worker;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class FotoProcessor {

    private static final int THUMB_MAX = 320;
    private static final int FULL_MAX = 1280;

    /** Re-encoda para JPG (descarta EXIF/GPS) limitando a maior dimensão a FULL_MAX. */
    public byte[] sanitizar(byte[] original) throws IOException {
        BufferedImage img = ler(original);
        BufferedImage redimensionada = redimensionar(img, FULL_MAX);
        return paraJpg(redimensionada);
    }

    /** Gera thumbnail JPG limitando a maior dimensão a THUMB_MAX. */
    public byte[] thumbnail(byte[] original) throws IOException {
        BufferedImage img = ler(original);
        BufferedImage thumb = redimensionar(img, THUMB_MAX);
        return paraJpg(thumb);
    }

    private BufferedImage ler(byte[] bytes) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
        if (img == null) {
            throw new IOException("Conteúdo não é uma imagem suportada");
        }
        return img;
    }

    private BufferedImage redimensionar(BufferedImage origem, int maxLado) {
        int w = origem.getWidth();
        int h = origem.getHeight();
        double escala = Math.min(1.0, (double) maxLado / Math.max(w, h));
        int nw = (int) Math.round(w * escala);
        int nh = (int) Math.round(h * escala);

        BufferedImage destino = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = destino.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(origem, 0, 0, nw, nh, null);
        g.dispose();
        return destino;
    }

    private byte[] paraJpg(BufferedImage img) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "jpg", out);
        return out.toByteArray();
    }
}