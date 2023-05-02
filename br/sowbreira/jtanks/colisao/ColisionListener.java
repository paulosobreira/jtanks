package br.sowbreira.jtanks.colisao;

/**
 * @author Paulo Sobreira
 * Criado Em 20/08/2005
 */
public interface ColisionListener {

    public boolean colisaoTerreno(Avatar avatar);
    public boolean colisaoAvatar(Avatar avatar);
}
