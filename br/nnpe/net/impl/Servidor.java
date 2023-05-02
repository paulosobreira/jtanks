package br.nnpe.net.impl;

import br.nnpe.net.FachadaServidor;

import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Servidor extends Thread implements FachadaServidor {
    public static Map socketsClientes;
    public static BufferFinito pilhaComandos = new BufferFinito();
    public boolean iniciarOuvir;
    private java.net.ServerSocket srv;
    MulticastSocket multicastSocket;
    DatagramSocket datagramSocket;
    long udpCont;
    int porta;

    public void esperarConeccao(int porta) throws Exception {
        socketsClientes = new HashMap();
        srv = new java.net.ServerSocket(porta);
        this.porta = porta;
        datagramSocket = new DatagramSocket();
        System.out.println(
            "Ok eu sou " + InetAddress.getLocalHost() + " ouvindo na porta " +
            srv.getLocalPort() + "\n"
        );
        this.start();
    }

    public void enviarTodos(String comando) throws Exception {
        for (Iterator iter = socketsClientes.keySet()
                                            .iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            Socket socket = (Socket) socketsClientes.get(key);

            try {
                new java.io.DataOutputStream(socket.getOutputStream()).writeUTF(
                    comando
                );
            } catch (Exception e) {
                socket.close();
                iter.remove();
            }
        }
    }

    public void enviar(int id, String comando) throws Exception {
        Socket socket = (Socket) socketsClientes.get(String.valueOf(id));

        try {
            new java.io.DataOutputStream(socket.getOutputStream()).writeUTF(
                comando
            );
        } catch (Exception e) {
            socket.close();
            socketsClientes.remove(socket);
        }
    }

    public void iniciar() throws Exception {
        // TODO Auto-generated method stub
    }

    public int[] obterListaIdsConectados() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public String lerComandos() throws Exception {
        String comando = (String) pilhaComandos.remove();

        return comando;
    }

    public void run() {
        while (!iniciarOuvir) {
            try {
                synchronized (socketsClientes) {
                    Socket socket = srv.accept();
                    String id = String.valueOf(socketsClientes.size() + 1);
                    socketsClientes.put(id, socket);
                    new LinhaServidora(id, socket, pilhaComandos).start();

                    System.out.println("Conectou ID :" + id+
                            " IP : "+socket.getRemoteSocketAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Servidor servidor = new Servidor();
        servidor.esperarConeccao(8080);
        System.out.println("Servidor leu" + servidor.lerComandos());
    }

    public void enviarTodosUDP(String comando) throws Exception {
        /**
         * NCast via UDP
         */
        udpCont++;
        comando = udpCont + "#" + comando;

        for (Iterator iter = socketsClientes.keySet()
                                            .iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            Socket socket = (Socket) socketsClientes.get(key);
            DatagramPacket datagramPacket =
                new DatagramPacket(
                    comando.getBytes(), comando.length(),
                    socket.getInetAddress(), socket.getPort()
                );
            datagramSocket.send(datagramPacket);
        }
    }
}
