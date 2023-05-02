package br.nnpe.net.impl;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.MulticastSocket;
import java.net.SocketException;


public class LinhaUDP extends Thread {
    byte[] buffer = new byte[100]; // Cria um buffer local
    BufferFinito bufferFinito;
    DatagramPacket pacote;
    DatagramSocket datagramSocket;
    MulticastSocket multicastSocket;
    long udpCont;

     public LinhaUDP(BufferFinito pilhaComandos, int porta)
        throws SocketException {
        /**
         * Multicast
         */

        /*
        multicastSocket = new MulticastSocket(Servidor.PORTA_UDP);
        multicastSocket.joinGroup(InetAddress.getByName(Servidor.ipaddr));
        */
        pacote = new DatagramPacket(buffer, buffer.length);
        datagramSocket = new DatagramSocket(porta);
        this.bufferFinito = pilhaComandos;
    }

    public void run() {
        while (true) {
            try {
                /*
                multicastSocket.receive(pacote);
                */
                datagramSocket.receive(pacote);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String comando = new String(pacote.getData(), 0, pacote.getLength());

            if ((comando == null) || "".equals(comando)) {
                return;
            }

            long udpNum = Long.parseLong(comando.split("#")[0]);

            if (udpNum < udpCont) {
                return;
            }

            udpCont = udpNum;
            bufferFinito.add(comando.split("#")[1].trim());
        }
    }
}
