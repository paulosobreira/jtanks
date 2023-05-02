package br.nnpe.net.impl;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import br.nnpe.net.FachadaCliente;


public class Cliente extends LinhaTCP implements FachadaCliente {
    long udpCont;
    int portaSrv;

    public void enviarComando(String comando) throws Exception {
        out.writeUTF(comando);
    }

    public void conectar(String ip, int porta) throws Exception {
        socket = new Socket(InetAddress.getByName(ip), porta);
        in = new java.io.DataInputStream(new java.io.BufferedInputStream(
                    socket.getInputStream()));
        out = new java.io.DataOutputStream(socket.getOutputStream());
        live = true;
        pilhaComandos = new BufferFinito();
        portaSrv = porta;
        new LinhaUDP(pilhaComandos, socket.getLocalPort()).start();
        this.start();
    }

    public void desConectar() throws Exception {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args)
        throws UnknownHostException, Exception {
        Cliente cliente = new Cliente();
        cliente.conectar(InetAddress.getLocalHost().getHostAddress(), 8080);
        cliente.enviarComando(JOptionPane.showInputDialog(""));
        System.out.println("Cliente recebeu" + cliente.lerComando());
    }
}
