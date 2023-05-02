package br.nnpe.net;

public interface FachadaCliente {
    public void enviarComando(String comando) throws Exception;
    
       
    public String lerComando() throws Exception;

    public void conectar(String ip, int porta) throws Exception;

    public void desConectar() throws Exception;
}
