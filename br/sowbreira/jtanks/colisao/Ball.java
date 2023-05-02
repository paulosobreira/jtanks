package br.sowbreira.jtanks.colisao;

import br.nnpe.GeoUtil;

import java.awt.Point;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Paulo Sobreira
 * Criado Em 20/08/2005
 */
public class Ball extends Avatar {
    static int counter;
    public List rota;

    public Ball(String imageGif, Point local, int angulo,
        ColisionListener listener, Avatar dono) throws IOException {
        super(imageGif, local, listener);

        Point destino = GeoUtil.calculaPonto(angulo, 350, local);
        this.rota = GeoUtil.drawBresenhamLine(local, destino);

        List meiaRota = new ArrayList();

        for (int i = 0; i < rota.size(); i++) {
            if ((i % 2) == 0) {
                meiaRota.add(rota.get(i));
            }
        }

        rota = meiaRota;

        this.dono = dono;
        nome = "Bola" + counter++;
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
        return 5;
    }

    /**
      * @see java.lang.Runnable#run()
      */
    public void run() {
        boolean mover = false;

        while (!rota.isEmpty()) {
            Point p = (Point) rota.remove(0);

            if (mover) {
                mover(p);
                mover = false;
            } else {
                mover = true;
            }

            sleep(15);
        }

        this.foraTela = true;
    }
}
