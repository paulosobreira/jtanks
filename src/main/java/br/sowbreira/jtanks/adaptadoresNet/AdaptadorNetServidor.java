package br.sowbreira.jtanks.adaptadoresNet;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.nnpe.net.FachadaServidor;
import br.nnpe.net.impl.Servidor;
import br.sowbreira.jtanks.colisao.Avatar;
import br.sowbreira.jtanks.colisao.Ball;
import br.sowbreira.jtanks.colisao.Flag;
import br.sowbreira.jtanks.colisao.Tank;
import br.sowbreira.jtanks.cotrole.ControleColisao;
import br.sowbreira.jtanks.cotrole.ControleJtanks;
import br.sowbreira.jtanks.cotrole.Protocolo;


public class AdaptadorNetServidor extends ControleJtanks implements Runnable {
    public Map mapaSprites = new Hashtable();
    private boolean servidorRodando;
    private boolean inciadoParaClientes;
    public boolean modoDebug;
    private FachadaServidor fachadaServidor;
    private String idMutex = "10";
    private Avatar avatarForaTela;
    private boolean srvUDP;

    public AdaptadorNetServidor() throws IOException {
        System.out.println("Use o o Construtor AdaptadorNetServidor(int porta)");
    }

    public AdaptadorNetServidor(int porta, boolean tipoSrv)
        throws IOException {
        fachadaServidor = new Servidor();
        servidorRodando = true;
        srvUDP = tipoSrv;

        try {
            fachadaServidor.iniciar();
            fachadaServidor.esperarConeccao(porta);

            Thread thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void iniciarServidorJogo() throws Exception {
        panel = new AdaptadorNetServerPanel("bg.png", this);
        panel.setBounds(0, 0, 800, 600);
        colisao = new ControleColisao(panel.avatarList, panel, this);
        colisao.adaptadorNetServidor = this;

        final Thread screenPainter = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            if (modoDebug) {
                                panel.repaint();
                            }

                            atualizaClientes();

                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        screenPainter.start();
    }

    public void iniciarJogo() {
        try {
            for (Iterator iter = mapaSprites.keySet().iterator();
                    iter.hasNext();) {
                String key = (String) iter.next();

                Avatar avatar = (Avatar) mapaSprites.get(key);

                if (!(avatar instanceof Tank)) {
                    continue;
                }

                new Thread(avatar).start();

                fachadaServidor.enviarTodos(Protocolo.TANK +
                    Protocolo.SEPARADOR + avatar.id + Protocolo.SEPARADOR +
                    avatar.local.x + Protocolo.SEPARADOR + avatar.local.y +
                    Protocolo.SEPARADOR + avatar.nome);
            }

            criarFlag();
        } catch (Exception e) {
            e.printStackTrace();
        }

        inciadoParaClientes = true;
    }

    public void atualizaClientes() {
        if (!inciadoParaClientes) {
            return;
        }

        List remover = new ArrayList();

        try {
            List sprites = new ArrayList(mapaSprites.keySet());

            for (int i = 0; i < sprites.size(); i++) {
                String key = (String) sprites.get(i);

                Avatar avatar = (Avatar) mapaSprites.get(key);

                if (avatar.foraTela) {
                    avatarForaTela = avatar;

                    continue;
                }

                if (avatar instanceof Tank) {
                    if (srvUDP) {
                        fachadaServidor.enviarTodosUDP(Protocolo.MOVER +
                            Protocolo.SEPARADOR + Protocolo.TANK +
                            Protocolo.SEPARADOR + avatar.id +
                            Protocolo.SEPARADOR + avatar.local.x +
                            Protocolo.SEPARADOR + avatar.local.y +
                            Protocolo.SEPARADOR + ((Tank) avatar).angle);
                    } else {
                        fachadaServidor.enviarTodos(Protocolo.MOVER +
                            Protocolo.SEPARADOR + Protocolo.TANK +
                            Protocolo.SEPARADOR + avatar.id +
                            Protocolo.SEPARADOR + avatar.local.x +
                            Protocolo.SEPARADOR + avatar.local.y +
                            Protocolo.SEPARADOR + ((Tank) avatar).angle);
                    }
                } else if (avatar instanceof Ball) {
                    if (srvUDP) {
                        fachadaServidor.enviarTodosUDP(Protocolo.MOVER +
                            Protocolo.SEPARADOR + Protocolo.TIRO +
                            Protocolo.SEPARADOR + avatar.id +
                            Protocolo.SEPARADOR + avatar.local.x +
                            Protocolo.SEPARADOR + avatar.local.y +
                            Protocolo.SEPARADOR + "0");
                    } else {
                        fachadaServidor.enviarTodos(Protocolo.MOVER +
                            Protocolo.SEPARADOR + Protocolo.TIRO +
                            Protocolo.SEPARADOR + avatar.id +
                            Protocolo.SEPARADOR + avatar.local.x +
                            Protocolo.SEPARADOR + avatar.local.y +
                            Protocolo.SEPARADOR + " ");
                    }
                }
            }

            if (avatarForaTela != null) {
                panel.avatarList.remove(avatarForaTela);

                if ((mapaSprites != null) && (avatarForaTela != null)) {
                    remover.add(avatarForaTela.id);
                }
            }

            avatarForaTela = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Iterator iter = remover.iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            mapaSprites.remove(element);
            removerSprite(element);
        }
    }

    public void run() {
        while (servidorRodando) {
            try {
                String comando = fachadaServidor.lerComandos();
                String[] cmds = comando.split(" ");
                String id = cmds[0];
                String[] protoCmd = cmds[1].split(Protocolo.SEPARADOR);

                if (Protocolo.ENVIAR_PONTO_ORIGEM.equals(protoCmd[0])) {
                    criarJogador(id, protoCmd[1], protoCmd[2], protoCmd[3]);
                } else if (Protocolo.MOVER.equals(protoCmd[0])) {
                    moverSprite(id, protoCmd[1]);
                } else if (Protocolo.TIRO.equals(protoCmd[0])) {
                    tiroDisparado(id);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tiroDisparado(String id) {
        Tank avatar = (Tank) mapaSprites.get(id);
        long time = System.currentTimeMillis();

        if ((time - avatar.lastFire) < (2000 * ((avatar.velocidade == 0) ? 1
                                                                             : avatar.velocidade))) {
            return;
        }

        try {
            synchronized (idMutex) {
                Ball b = new Ball("ball.png", avatar.pontoCentral(),
                        avatar.getAngle(), colisao, avatar);
                panel.avatarList.add(b);
                b.id = String.valueOf(Integer.parseInt(idMutex) + 1);
                idMutex = b.id;
                mapaSprites.put(idMutex, b);
                fachadaServidor.enviarTodos(Protocolo.TIRO +
                    Protocolo.SEPARADOR + idMutex + Protocolo.SEPARADOR +
                    b.local.x + Protocolo.SEPARADOR + b.local.y);
                new Thread(b).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moverSprite(String id, String posi) {
        if (!inciadoParaClientes) {
            return;
        }

        Avatar avatar = (Avatar) mapaSprites.get(id);

        if (Protocolo.BAIXO.equals(posi)) {
            avatar.velocidade -= 1;
        } else if (Protocolo.CIMA.equals(posi)) {
            avatar.velocidade += 1;
        } else if (Protocolo.ESQUERDA.equals(posi)) {
            ((Tank) avatar).decreasseAngle();
        } else if (Protocolo.DIREITA.equals(posi)) {
            ((Tank) avatar).increasseAngle();
        }
    }

    private void criarJogador(String id, String sX, String sY, String nome)
        throws IOException {
        int x = Integer.parseInt(sX);
        int y = Integer.parseInt(sY);
        Avatar avatar = new Tank("tankp" + id + ".png", new Point(x, y),
                colisao, this);
        avatar.id = id;
        avatar.nome = nome;
        panel.avatarList.add(avatar);
        mapaSprites.put(id, avatar);
    }

    public void removerSprite(String id) {
        if (inciadoParaClientes) {
            try {
                fachadaServidor.enviarTodos(Protocolo.REMOVER +
                    Protocolo.SEPARADOR + id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void atualizaFlags(Tank tank) {
        if (inciadoParaClientes) {
            try {
                fachadaServidor.enviarTodos(Protocolo.FLAG +
                    Protocolo.SEPARADOR + tank.id + Protocolo.SEPARADOR +
                    tank.flags);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void criarFlag() throws IOException {
        synchronized (idMutex) {
            final Flag flag = new Flag(pontoFlag(), null);
            Thread thread = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            flag.id = String.valueOf(Integer.parseInt(idMutex) +
                                    1);
                            idMutex = flag.id;
                            mapaSprites.put(idMutex, flag);

                            try {
                                fachadaServidor.enviarTodos(Protocolo.CRIAR_FLAG +
                                    Protocolo.SEPARADOR + idMutex +
                                    Protocolo.SEPARADOR + flag.local.x +
                                    Protocolo.SEPARADOR + flag.local.y);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            panel.avatarList.add(flag);
                        }
                    });
            thread.start();
        }
    }
}
