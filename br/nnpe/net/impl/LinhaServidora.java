package br.nnpe.net.impl;

import java.io.IOException;
import java.net.Socket;


public class LinhaServidora extends LinhaTCP {
    String id;

    public LinhaServidora(String id, Socket socket, BufferFinito pilhaComandos) throws IOException {
        
        this.id = id;
        this.socket = socket;
        this.pilhaComandos = pilhaComandos;
        live = true;
        in = new java.io.DataInputStream(
                new java.io.BufferedInputStream(socket.getInputStream())
            );
        out = new java.io.DataOutputStream(socket.getOutputStream());
    }
    
    
    public void run() {
        while (live) {
            try {
                String comando = id+" "+in.readUTF();
               
                pilhaComandos.add(comando);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                live = false;
            }
        }
    }

}
