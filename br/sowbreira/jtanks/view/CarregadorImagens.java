package br.sowbreira.jtanks.view;

import java.net.URL;

public class CarregadorImagens {
    
    public static URL carregarImagem(String imagem){
        return CarregadorImagens.class.getResource(imagem);
    }

}
