package br.sowbreira.jtanks.colisao;

import java.awt.Point;

import java.io.IOException;


/**
 * @author Paulo Sobreira
 * Criado Em 03/09/2005
 */
public class Flag extends Avatar {
    public Flag(String imageGif, Point local, ColisionListener listener)
        throws IOException {
        super(imageGif, local, listener);
    }

    /**
     * @param point
     * @param object
     * @throws IOException
     */
    public Flag(Point point, Object object) throws IOException {
        super("flag.png", point, null);
    }

    public int value() {
        return (int) (1 + (Math.random() * 10));
    }

    /**
      * @see br.sowbreira.jtanks.colisao.Avatar#getWidth()
      */
    public int getWidth() {
        return 5;
    }

    /**
      * @see br.sowbreira.jtanks.colisao.Avatar#getHeight()
      */
    public int getHeight() {
        return 10;
    }

    /**
      * @see java.lang.Runnable#run()
      */
    public void run() {
    }
}
