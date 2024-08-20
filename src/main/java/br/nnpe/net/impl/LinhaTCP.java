package br.nnpe.net.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class LinhaTCP extends Thread {
    Socket socket;
    boolean live;
    DataInputStream in;
    DataOutputStream out;
    BufferFinito pilhaComandos;

    public void run() {
        while (live) {
            try {
                String comando = in.readUTF();
                pilhaComandos.add(comando);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                live = false;
            }
        }
    }
    
    public String lerComando() throws Exception {
        return (String) pilhaComandos.remove();
    }
}
