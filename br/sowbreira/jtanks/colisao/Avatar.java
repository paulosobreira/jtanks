package br.sowbreira.jtanks.colisao;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.ImageIcon;

import br.nnpe.ImageUtil;
import br.sowbreira.jtanks.adaptadoresNet.AdaptadorNetServidor;
import br.sowbreira.jtanks.view.CarregadorImagens;


/**
 * @author Paulo Sobreira
 * Criado Em 17:40:49
 */
public abstract class Avatar implements Runnable {
    public BufferedImage avatarImage;
    public Point local;
    public Point moverPara;
    public Point spawPoint;
    public Rectangle rect;
    public boolean stuck;
    public boolean foraTela;
    public int velocidade = 0;
    private Point pontoCentroAvatar = new Point();
    public ColisionListener listener;
    public Avatar dono;
    public String nome;
    public ImageIcon icon;
    public String id;
    public AdaptadorNetServidor adaptadorNetServidor;

    public Avatar(String imageGif, Point local, ColisionListener listener,AdaptadorNetServidor adaptadorNetServidor)
    throws IOException {
        this(imageGif, local, listener);
        this.adaptadorNetServidor = adaptadorNetServidor;
    }
    public Avatar(String imageGif, Point local, ColisionListener listener)
        throws IOException {
        if (imageGif != null) {
            icon = new ImageIcon(CarregadorImagens.carregarImagem(imageGif));
            avatarImage = ImageUtil.toBufferedImage(icon.getImage());
            avatarImage = ImageUtil.geraTransparencia(avatarImage, Color.WHITE);
        }

        this.local = new Point();
        this.local.setLocation(
            local.x - (getImageWidth() / 3), local.y - (getImageWidth() / 3)
        );
        moverPara = local;
        spawPoint = new Point(local.x, local.y);
        rect = new Rectangle(local.x, local.y, getWidth(), getHeight());
        this.listener = listener;
    }

    public void mover(Point p) {
        moverPara.x = p.x + (getImageWidth() / 2);
        moverPara.y = p.y + (getImageWidth() / 2);

        if (
            (listener != null) &&
                (
                    listener.colisaoTerreno(this) ||
                    listener.colisaoAvatar(this)
                )
        ) {
            return;
        }

        local = p;
        rect.setBounds(
            local.x + (getImageWidth() / 3), local.y + (getImageWidth() / 3),
            getWidth(), getHeight()
        );
    }

    public boolean colide(Avatar avatar) {
        return rect.intersects(avatar.rect);
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public int getImageWidth() {
        return icon.getIconWidth();
    }

    public int getImageHeight() {
        return icon.getIconHeight();
    }

    public Point pontoCentral() {
        pontoCentroAvatar.setLocation(
            local.x + (getImageWidth() / 2), local.y + (getImageHeight() / 2)
        );

        return pontoCentroAvatar;
    }

    public void sleep(int s) {
        try {
            Thread.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return nome;
    }
}
