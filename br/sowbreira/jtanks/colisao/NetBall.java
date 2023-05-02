package br.sowbreira.jtanks.colisao;

import java.awt.Point;
import java.io.IOException;

public class NetBall extends Ball {

    public NetBall(String imageGif, Point local, int angulo, ColisionListener listener, Avatar dono) throws IOException {
        super(imageGif, local, angulo, listener, dono);
    }

    public void run() {
    }
}
