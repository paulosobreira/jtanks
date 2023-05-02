package br.sowbreira.jtanks.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import br.nnpe.ImageUtil;
import br.sowbreira.jtanks.colisao.Avatar;
import br.sowbreira.jtanks.colisao.Tank;


/**
 * @author Paulo Sobreira
 * Criado Em 10:51:26
 */
public class MainPanel extends JPanel {
    public List avatarList = new Vector();
    public BufferedImage backGround;
    public Rectangle rectangle;
    public Avatar avatarForaTela;

    public MainPanel(String backGroundStr) {
        ImageIcon icon =
            new ImageIcon(CarregadorImagens.carregarImagem(backGroundStr));
        backGround = ImageUtil.toBufferedImage(icon.getImage());
        rectangle =
            new Rectangle(
                backGround.getWidth() - 1, backGround.getHeight() - 1
            );

        if (backGround == null) {
            System.out.println("backGround=" + backGround);
            System.exit(1);
        }

        this.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int[] cor = new int[4];
                    cor = backGround.getData()
                                    .getPixel(e.getX(), e.getY(), cor);
                    System.out.println(
                        new Color(cor[0], cor[1], cor[2], cor[3])
                    );
                }
            }
        );
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
                graphics2D.setColor(Color.RED);
                graphics2D.drawString(
                    "Nome : " + t.nome, t.local.x, t.local.y-16
                );
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawString(
                    "Flags : " + t.getFlags(), t.local.x, t.local.y
                );
            }

            if (avatar.foraTela) {
                avatarForaTela = avatar;
            }
        }

        avatarList.remove(avatarForaTela);
    }

    protected BufferedImage filtar(Avatar avatar) {
        BufferedImage retorno =
            new BufferedImage(
                avatar.getImageWidth(), avatar.getImageHeight(),
                BufferedImage.TYPE_INT_ARGB
            );

        if (avatar instanceof Tank) {
            AffineTransformOp op =
                new AffineTransformOp(
                    ((Tank) avatar).affineTransform,
                    AffineTransformOp.TYPE_NEAREST_NEIGHBOR
                );

            op.filter(avatar.avatarImage, retorno);
        } else {
            return avatar.avatarImage;
        }

        return retorno;
    }

    public boolean estaContido(Point p) {
        if (
            ((p.x | p.y) < 0) || (p.x > rectangle.width) ||
                (p.y > rectangle.height)
        ) {
            return false;
        }

        return true;
    }
}
