package br.sowbreira.jtanks.adaptadoresNet;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import br.sowbreira.jtanks.colisao.Avatar;
import br.sowbreira.jtanks.colisao.Tank;
import br.sowbreira.jtanks.view.MainPanel;


public class AdaptadorNetServerPanel extends MainPanel {
    /**
     *
     */
    private static final long serialVersionUID = -2462026945915811648L;
    AdaptadorNetServidor servidor;

    public AdaptadorNetServerPanel(String backGroundStr) {
        super(backGroundStr);
    }

    public AdaptadorNetServerPanel(
        String string, AdaptadorNetServidor servidor
    ) {
        super(string);
        this.servidor = servidor;
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
            graphics2D.setColor(Color.DARK_GRAY);
            graphics2D.drawString(
                "Id : " + avatar.id, avatar.local.x, avatar.local.y - 2
            );

            if (avatar instanceof Tank) {
                Tank t = (Tank) avatar;
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawString(
                    "Flags : " + t.getFlags(), t.local.x, t.local.y - 14
                );
                graphics2D.setColor(Color.RED);
                graphics2D.drawString(
                    "Nome : " + t.nome, t.local.x, t.local.y - 24
                );
            }

            graphics2D.setColor(Color.BLUE);
            graphics2D.draw(avatar.rect);
            graphics2D.setColor(Color.RED);
            graphics2D.drawOval(
                avatar.pontoCentral().x, avatar.pontoCentral().y, 5, 5
            );
            graphics2D.setColor(Color.GREEN);
            graphics2D.drawOval(avatar.local.x, avatar.local.y, 5, 5);

            graphics2D.setColor(Color.ORANGE);
            graphics2D.drawRect(
                avatar.local.x, avatar.local.y, avatar.getImageWidth() - 1,
                avatar.getImageHeight() - 1
            );

        }



        
    }
}
