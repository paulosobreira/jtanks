package br.sowbreira.jtanks.colisao;

import br.nnpe.GeoUtil;

import br.sowbreira.jtanks.adaptadoresNet.AdaptadorNetServidor;
import br.sowbreira.jtanks.cotrole.ControleColisao;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import java.io.IOException;


/**
 * @author Paulo Sobreira
 * Criado Em 10:57:32
 */
public class Tank extends Avatar {
    static int counter;
    public AffineTransform affineTransform;
    public long lastFire;
    public Point pontoGiroAvatar;
    public int angle;
    public int flags;

    public Tank(String imageGif, Point local, ColisionListener listener)
        throws IOException {
        super(imageGif, local, listener);
        affineTransform = new AffineTransform();
        pontoGiroAvatar =
            new Point((getImageWidth() / 2), (getImageHeight() / 2));
        nome = "Tank " + counter++;
    }

    public Tank(
        String string, Point point, ControleColisao colisao,
        AdaptadorNetServidor servidor
    ) throws IOException {
        this(string, point, colisao);
        this.adaptadorNetServidor = servidor;
    }

    public void achouFlags(int v) {
        flags += v;

        if (adaptadorNetServidor != null) {
            adaptadorNetServidor.atualizaFlags(this);
        }
    }

    public int getFlags() {
        return flags;
    }

    public int getWidth() {
        return 20;
    }

    public int getHeight() {
        return 20;
    }

    public void increasseAngle() {
        if (angle >= 360) {
            angle = 0;
        }

        angle += (7 - (velocidade * 2));
        rotate(angle);
    }

    public void rotate(int angle) {
        double rad = Math.toRadians((double) angle);
        affineTransform.setToRotation(
            rad, pontoGiroAvatar.x, pontoGiroAvatar.y
        );
    }

    public void decreasseAngle() {
        if (angle <= 0) {
            angle = 360;
        }

        angle -= (7 - (velocidade * 2));
        rotate(angle);
    }

    public int getAngle() {
        return angle;
    }

    /**
      * @see java.lang.Runnable#run()
      */
    public void run() {
        while (true) {
            if (velocidade < 0) {
                velocidade = 0;
            }

            if (velocidade > 3) {
                velocidade = 3;
            }

            Point p2 = GeoUtil.calculaPonto(getAngle(), velocidade, local);

            mover(p2);

            try {
                Thread.sleep(80);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
