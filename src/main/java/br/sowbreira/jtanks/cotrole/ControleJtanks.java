package br.sowbreira.jtanks.cotrole;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.Raster;
import java.io.IOException;

import javax.swing.JOptionPane;

import br.sowbreira.jtanks.colisao.Flag;
import br.sowbreira.jtanks.colisao.Tank;
import br.sowbreira.jtanks.view.MainPanel;


/**
 * @author Paulo Sobreira
 * Criado Em 12:31:42
 */
public class ControleJtanks implements KeyListener, MouseListener {
    private Tank avatar1;
    private Tank avatar2;
    public MainPanel panel;
    public ControleColisao colisao;
    private ControleComandos comandosP1;
    private ControleComandos comandosP2;
    private boolean p1ok;
    private boolean p2ok;
    private boolean jogoIniciado;

    public ControleJtanks() throws IOException {
     }

    public void criarJog1(Point p) throws IOException {
        avatar1 = new Tank("tankp1.png", p, colisao);
        panel.avatarList.add(avatar1);
    }

    public void criarJog2(Point p) throws IOException {
        avatar2 = new Tank("tankp2.png", p, colisao);
        panel.avatarList.add(avatar2);
    }

    public void iniciarJogo() {
        panel = new MainPanel("bg.png");
        colisao = new ControleColisao(panel.avatarList, panel,this);

        panel.setBounds(0, 0, 800, 600);

        final Thread screenPainter =
            new Thread(
                new Runnable() {
                    public void run() {
                        while (true) {
                            panel.repaint();

                            try {
                                Thread.sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            );
        screenPainter.start();
        String msg = "Clike para setar a base de Player1";
        JOptionPane.showMessageDialog(
            panel, msg, "Clike", JOptionPane.INFORMATION_MESSAGE
        );

        /**
         * Controle continua no mouselistener do painel
         */
        panel.addMouseListener(this);
    }

    public void iniciarThreads() throws InterruptedException {
        Thread.sleep(100);
        new Thread(avatar1).start();
        new Thread(avatar2).start();
        comandosP1 = new ControleComandos(avatar1, panel.avatarList, colisao);
        comandosP2 = new ControleComandos(avatar2, panel.avatarList, colisao);
        new Thread(comandosP1).start();
        new Thread(comandosP2).start();
        try {
            criarFlag();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jogoIniciado = true;
    }

    public void criarFlag() throws IOException {
        final Flag flag = new Flag( pontoFlag(), null);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                panel.avatarList.add(flag);
            }
        });
        thread.start();
    }

    public static void main(String[] args) {
        System.out.println((int) (1 + (Math.random() * 6)));
    }

    /**
     * Acha aleatoriamete um ponto onde o tank possa ir para setar a flag
     */
    public Point pontoFlag() {
        Raster raster = panel.backGround.getData();
        Point retorno = null;

        while (retorno == null) {
            int x = (int) (20 + (Math.random() * 720));
            int y = (int) (20 + (Math.random() * 520));

            int[] cor = new int[4];
            cor = raster.getPixel(x, y, cor);

            Color corDest = new Color(cor[0], cor[1], cor[2], cor[3]);

            if (
                ControleColisao.agua.equals(corDest) ||
                    ControleColisao.terra.equals(corDest)
            ) {
                retorno = new Point(x, y);
            }
        }

        return retorno;
    }

    public Tank getAvatar1() {
        return avatar1;
    }

    public Tank getAvatar2() {
        return avatar2;
    }

    public MainPanel getPanel() {
        return panel;
    }

    /**
      * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
      */
    public void keyTyped(KeyEvent e) {
    }

    /**
      * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
      */
    public void keyPressed(KeyEvent e) {
        int keycode = e.getKeyCode();

        if (!jogoIniciado) {
            return;
        }

        if (keycode == KeyEvent.VK_LEFT) {
            comandosP1.esquerda();
        } else if (keycode == KeyEvent.VK_RIGHT) {
            comandosP1.direita();
        } else if (keycode == KeyEvent.VK_UP) {
            avatar1.velocidade += 1;
            comandosP1.alinhar();
        } else if (keycode == KeyEvent.VK_DOWN) {
            avatar1.velocidade -= 1;
            comandosP1.alinhar();
        } else if (keycode == KeyEvent.VK_INSERT) {
            comandosP1.tiro();
        } else if (keycode == KeyEvent.VK_A) {
            comandosP2.esquerda();
        } else if (keycode == KeyEvent.VK_D) {
            comandosP2.direita();
        } else if (keycode == KeyEvent.VK_W) {
            avatar2.velocidade += 1;
            comandosP2.alinhar();
        } else if (keycode == KeyEvent.VK_S) {
            avatar2.velocidade -= 1;
            comandosP2.alinhar();
        } else if (keycode == KeyEvent.VK_SPACE) {
            comandosP2.tiro();
        }
    }

    /**
      * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
      */
    public void keyReleased(KeyEvent e) {
    }

    /**
      * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
      */
    public void mouseClicked(MouseEvent e) {
        try {
            if (!p1ok) {
                criarJog1(e.getPoint());
                p1ok = true;

                String msg = "Clike para setar a base de Player2";
                JOptionPane.showMessageDialog(
                    panel, msg, "Clike", JOptionPane.INFORMATION_MESSAGE
                );
            } else if (!p2ok) {
                criarJog2(e.getPoint());
                p2ok = true;
                iniciarThreads();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
      * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
      */
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
      * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
      */
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
      * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
      */
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
      * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
      */
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
    }
}
