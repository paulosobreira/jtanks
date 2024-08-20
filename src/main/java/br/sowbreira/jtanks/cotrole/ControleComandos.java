package br.sowbreira.jtanks.cotrole;

import br.sowbreira.jtanks.colisao.Ball;
import br.sowbreira.jtanks.colisao.Tank;

import java.io.IOException;

import java.util.List;


/**
 * @author Paulo Sobreira
 * Criado Em 21/08/2005
 */
public class ControleComandos implements Runnable {
    private int stado;
    private Tank avatar;
    private List avatarList;
    private ControleColisao colisao;

    /**
     * @param avatar
     * @param avatarList
     * @param colisao
     */
    public ControleComandos(
        Tank avatar, List avatarList, ControleColisao colisao
    ) {
        super();
        this.avatar = avatar;
        this.avatarList = avatarList;
        this.colisao = colisao;
    }

    public void alinhar() {
        stado = 1;
    }

    public void esquerda() {
        stado = 2;
    }

    public void direita() {
        stado = 3;
    }

    public void tiro() {
        stado = 4;
    }

    /**
      * @see java.lang.Runnable#run()
      */
    public void run() {
        while (true) {
            switch (stado) {
            case 1:

                //alinha
                stado = 0;

                break;

            case 2:
                avatar.decreasseAngle();

                break;

            case 3:
                avatar.increasseAngle();

                break;

            case 4:

                try {
                    long time = System.currentTimeMillis();

                    if ((time - avatar.lastFire) < (1000 * 
                            (avatar.velocidade==0?1:avatar.velocidade))) {
                        break;
                    }

                    avatar.lastFire = time;

                    Ball b =
                        new Ball(
                            "ball.png", avatar.pontoCentral(),
                            avatar.getAngle(), colisao, avatar
                        );
                    avatarList.add(b);
                    new Thread(b).start();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                break;

            default:
                stado = 0;

                break;
            }

            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
