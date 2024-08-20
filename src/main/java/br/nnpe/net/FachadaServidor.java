package br.nnpe.net;

public interface FachadaServidor {
    public void esperarConeccao(int porta) throws Exception;
    
    public void iniciar() throws Exception;
    
    public int[] obterListaIdsConectados() throws Exception;

    public void enviarTodos(String comando) throws Exception;

    public void enviar(int id, String comando) throws Exception;
    public String lerComandos() throws Exception;
    
    public void enviarTodosUDP(String comando) throws Exception;
}
