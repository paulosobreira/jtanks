package br.sowbreira.jtanks.adaptadoresNet;

import br.nnpe.net.FachadaCliente;
import br.nnpe.net.impl.Cliente;

import br.sowbreira.jtanks.colisao.Avatar;
import br.sowbreira.jtanks.colisao.Ball;
import br.sowbreira.jtanks.colisao.Flag;
import br.sowbreira.jtanks.colisao.Tank;
import br.sowbreira.jtanks.cotrole.ControleJtanks;
import br.sowbreira.jtanks.cotrole.Protocolo;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.io.IOException;

import javax.swing.JOptionPane;


public class AdaptadorNetCliente extends ControleJtanks implements Runnable {
    private boolean posicaoSetada;
    private boolean conectado;
    private FachadaCliente fachadaCliente;
    private String nome;
    long lastFire;

    public AdaptadorNetCliente() throws IOException {
        System.out.println(
            "Use o o Construtor AdaptadorNetCliente(String nome, String ip, int porta)"
        );
    }

    public AdaptadorNetCliente(String nome, String ip, int porta)
        throws IOException {
        fachadaCliente = new Cliente();

        try {
            fachadaCliente.conectar(ip, porta);
            conectado = true;
            new Thread(this).start();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        this.nome = nome;
    }

    public void iniciarJogo() {
        panel = new AdaptadorNetClientePanel("bg.png");
        panel.setBounds(0, 0, 800, 600);

        String msg = "Clique na agua ou no terreno para setar a base. \n"+
        "Caso seja um cliente aguarde o inicio do jogo. \n"+
        "Caso seja um servidor veja se todos ja conectaram \n (console) "+
        "e então inicie um jogo multiplayer.";
        JOptionPane.showMessageDialog(
            panel, msg, "Clike", JOptionPane.INFORMATION_MESSAGE
        );

        final Thread screenPainter =
            new Thread(
                new Runnable() {
                    public void run() {
                        while (true) {
                            panel.repaint();

                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            );
        screenPainter.start();

        /**
         * Controle continua no mouselistener do painel
         */
        panel.addMouseListener(this);
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
        long time = System.currentTimeMillis();

        try {
            if (keycode == KeyEvent.VK_LEFT) {
                fachadaCliente.enviarComando(
                    Protocolo.MOVER + Protocolo.SEPARADOR + Protocolo.ESQUERDA
                );
            } else if (keycode == KeyEvent.VK_RIGHT) {
                fachadaCliente.enviarComando(
                    Protocolo.MOVER + Protocolo.SEPARADOR + Protocolo.DIREITA
                );
            } else if (keycode == KeyEvent.VK_UP) {
                fachadaCliente.enviarComando(
                    Protocolo.MOVER + Protocolo.SEPARADOR + Protocolo.CIMA
                );
            } else if (keycode == KeyEvent.VK_DOWN) {
                fachadaCliente.enviarComando(
                    Protocolo.MOVER + Protocolo.SEPARADOR + Protocolo.BAIXO
                );
            } else if (keycode == KeyEvent.VK_INSERT) {
                if ((time - lastFire) < 1500) {
                    return;
                }

                lastFire = time;
                fachadaCliente.enviarComando(Protocolo.TIRO);
            } else if (keycode == KeyEvent.VK_SPACE) {
                if ((time - lastFire) < 1500) {
                    return;
                }

                lastFire = time;
                fachadaCliente.enviarComando(Protocolo.TIRO);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
        if (!posicaoSetada) {
            try {
                String cmd =
                    Protocolo.ENVIAR_PONTO_ORIGEM + Protocolo.SEPARADOR +
                    e.getPoint().x + Protocolo.SEPARADOR + e.getY() +
                    Protocolo.SEPARADOR + nome;
                fachadaCliente.enviarComando(cmd);
                posicaoSetada = true;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
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

    public void run() {
        while (conectado) {
            try {
                String comando = fachadaCliente.lerComando();
                String[] protoCmd = comando.split(Protocolo.SEPARADOR);

                if (Protocolo.TANK.equals(protoCmd[0])) {
                    criarTank(
                        protoCmd[1], protoCmd[2], protoCmd[3], protoCmd[4]
                    );
                } else if (Protocolo.MOVER.equals(protoCmd[0])) {
                    moverSprite(
                        protoCmd[1], protoCmd[2], protoCmd[3], protoCmd[4],
                        protoCmd[5]
                    );
                } else if (Protocolo.TIRO.equals(protoCmd[0])) {
                    criarTiro(protoCmd[1], protoCmd[2], protoCmd[3]);
                } else if (Protocolo.REMOVER.equals(protoCmd[0])) {
                    removerSprite(protoCmd[1]);
                } else if (Protocolo.FLAG.equals(protoCmd[0])) {
                    atualizaFlags(protoCmd[1], protoCmd[2]);
                } else if (Protocolo.CRIAR_FLAG.equals(protoCmd[0])) {
                    criarFlag(protoCmd[1], protoCmd[2], protoCmd[3]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void criarFlag(String id, String x, String y) {
        try {
            final Flag flag =
                new Flag(
                    new Point(Integer.parseInt(x), Integer.parseInt(y)), null
                );
            flag.id = id;
            panel.avatarList.add(flag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void atualizaFlags(String id, String valor) {
        for (int i = 0; i < panel.avatarList.size(); i++) {
            Avatar avatar = (Avatar) panel.avatarList.get(i);

            if (avatar instanceof Tank && avatar.id.equals(id)) {
                ((Tank) avatar).flags = Integer.parseInt(valor);
            }
        }
    }

    private void removerSprite(String id) {
        Avatar avatarRemover = null;

        synchronized (panel.avatarList) {
            for (int i = 0; i < panel.avatarList.size(); i++) {
                Avatar avatar = (Avatar) panel.avatarList.get(i);

                if (avatar.id.equals(id)) {
                    avatarRemover = avatar;
                }
            }

            panel.avatarList.remove(avatarRemover);
        }
    }

    private void criarTiro(String id, String x, String y) {
        try {
            Ball b =
                new Ball(
                    "ball.png",
                    new Point(Integer.parseInt(x), Integer.parseInt(y)), 0, null,
                    null
                );
            b.id = id;
            panel.avatarList.add(b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void criarTank(String id, String sX, String sY, String nome)
        throws IOException {
        int x = Integer.parseInt(sX);
        int y = Integer.parseInt(sY);
        Avatar avatar = new Tank("tankp" + id + ".png", new Point(x, y), null);
        avatar.id = id;
        avatar.nome = nome;
        panel.avatarList.add(avatar);
    }

    private void moverSprite(
        String tipo, String id, String x, String y, String angle
    ) {
        for (int i = 0; i < panel.avatarList.size(); i++) {
            Avatar avatar = (Avatar) panel.avatarList.get(i);

            if (
                avatar instanceof Tank && Protocolo.TANK.equals(tipo) &&
                    avatar.id.equals(id)
            ) {
                Tank tank = (Tank) avatar;
                tank.mover(new Point(Integer.parseInt(x), Integer.parseInt(y)));
                tank.rotate(Integer.parseInt(angle));
            }

            if (
                avatar instanceof Ball && Protocolo.TIRO.equals(tipo) &&
                    avatar.id.equals(id)
            ) {
                Ball ball = (Ball) avatar;
                ball.mover(new Point(Integer.parseInt(x), Integer.parseInt(y)));
            }
        }
    }
}
