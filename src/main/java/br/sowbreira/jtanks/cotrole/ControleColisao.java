package br.sowbreira.jtanks.cotrole;

import br.sowbreira.jtanks.adaptadoresNet.AdaptadorNetServidor;
import br.sowbreira.jtanks.colisao.Avatar;
import br.sowbreira.jtanks.colisao.Ball;
import br.sowbreira.jtanks.colisao.ColisionListener;
import br.sowbreira.jtanks.colisao.Flag;
import br.sowbreira.jtanks.colisao.Tank;
import br.sowbreira.jtanks.view.MainPanel;

import java.awt.Color;
import java.awt.image.Raster;

import java.io.IOException;

import java.util.List;


/**
 * @author Paulo Sobreira
 * Criado Em 20/08/2005
 */
public class ControleColisao implements ColisionListener {
    public static Color terra = new Color(153, 153, 51, 0);
    public static Color agua = new Color(0, 0, 255, 0);
    private List avatarList;
    private MainPanel panel;
    private Raster raster;
    private int[] cor;
    private Color corDest;
    private ControleJtanks jtanks;
    public AdaptadorNetServidor adaptadorNetServidor;

    public ControleColisao(
        List avatarList, MainPanel panel, ControleJtanks jtanks
    ) {
        super();
        this.avatarList = avatarList;
        this.panel = panel;
        this.jtanks = jtanks;
        raster = panel.backGround.getData();
    }

    /**
      * @see br.sowbreira.jtanks.colisao.ColisionListener#colision(br.sowbreira.jtanks.colisao.Avatar)
      */
    public boolean colisaoTerreno(Avatar avatar) {
        /**
         * Verifica se o ponto esta contido na tela
         */
        if (this.panel.estaContido(avatar.moverPara)) {
            /**
             * Os tanks so podem andar na terra e na agua
             */
            cor = new int[4];
            cor = raster.getPixel(avatar.moverPara.x, avatar.moverPara.y, cor);
            corDest = new Color(cor[0], cor[1], cor[2], cor[3]);

            if (agua.equals(corDest) || terra.equals(corDest)) {
                if (agua.equals(corDest)) {
                    avatar.velocidade = 2;
                }

                return false;
            } else {
                destruirBala(avatar);
                avatar.velocidade = 0;
                return true;
            }
        } else {
            destruirBala(avatar);

            return true;
        }
    }

    public boolean colisaoAvatar(Avatar avatar) {
        if (avatar.stuck) {
            avatar.stuck = false;

            return false;
        }

        for (int i = 0; i < avatarList.size(); i++) {
            Avatar colidCom = (Avatar) avatarList.get(i);

            /**
             * se for tank
             */
            if (!colidCom.equals(avatar) && colidCom.colide(avatar)) {
                achouFlag(avatar, colidCom);

                if ((avatar.dono != null) && avatar.dono.equals(colidCom)) {
                    return false;
                }

                if ((colidCom.dono != null) && colidCom.dono.equals(avatar)) {
                    return false;
                }

                if (avatar instanceof Ball) {
                    destrirAlvo(avatar, colidCom);
                }

                avatar.velocidade = 0;
                colidCom.velocidade = 0;
                avatar.stuck = true;

                return true;
            }
        }

        return false;
    }

    /**
     * @param avatar
     * @param colidCom
     */
    private void achouFlag(Avatar avatar, Avatar colidCom) {
        if (avatar instanceof Tank && colidCom instanceof Flag) {
            Tank tank = (Tank) avatar;
            Flag flag = (Flag) colidCom;
            tank.achouFlags(flag.value());
            colidCom.foraTela = true;

            if (adaptadorNetServidor != null) {
                adaptadorNetServidor.removerSprite(flag.id);
            }

            try {
                jtanks.criarFlag();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * manda o tank atingido de volta pra base. Perde de 1 a 5 flags
     */
    private void destrirAlvo(Avatar avatar, Avatar avatarL) {
        destruirBala(avatar);
        destruirBala(avatarL);

        if (avatarL instanceof Tank) {
            ((Tank) avatarL).achouFlags(-(int) (1 + (Math.random() * 5)));
            avatarL.local = avatarL.spawPoint;
        }
    }

    /**
     * Se for bala de canhão destroy
     */
    private void destruirBala(Avatar avatar) {
        if (avatar instanceof Ball) {
            ((Ball) avatar).rota.clear();
        }
    }
}
