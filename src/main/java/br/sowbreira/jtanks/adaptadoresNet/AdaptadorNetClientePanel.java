package br.sowbreira.jtanks.adaptadoresNet;

import br.sowbreira.jtanks.colisao.Avatar;
import br.sowbreira.jtanks.colisao.Tank;
import br.sowbreira.jtanks.view.MainPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;


public class AdaptadorNetClientePanel extends MainPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 5777434732940317415L;

    public AdaptadorNetClientePanel(String backGroundStr) {
        super(backGroundStr);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.drawImage(backGround, 0, 0, null);

        for (int i = 0; i < avatarList.size(); i++) {
            Avatar avatar = (Avatar) avatarList.get(i);

            graphics2D.drawImage(
                filtar(avatar), avatar.local.x, avatar.local.y, null
            );
            if (avatar instanceof Tank) {
                Tank t = (Tank) avatar;
                graphics2D.setColor(Color.GREEN);
                graphics2D.drawString(
                    "Nome : " + t.nome, t.local.x, t.local.y - 16
                );
                graphics2D.setColor(Color.YELLOW);
                graphics2D.drawString(
                    "Flags : " + t.getFlags(), t.local.x, t.local.y
                );
            }
        }
    }
}
