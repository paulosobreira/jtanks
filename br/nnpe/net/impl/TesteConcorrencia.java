package br.nnpe.net.impl;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;


public class TesteConcorrencia {
    public static Vector vector = new Vector();

    public static void main(String[] args) {
        Thread thread = new Thread(new Runnable() {
                    public void run() {
                        for (int i = 0; i < 10000; i++) {
                            String add = "Indice " + i;
                            vector.add(add);
                            System.out.println("Adicionou " + add);

                            try {
                                Thread.sleep((long) (10 * Math.random()));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Thread thread2 = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                if (vector.isEmpty()) {
                                    continue;
                                }

                                int indice = (int) (10 * Math.random());
                                indice = ((indice >= vector.size()) ? 0 : indice);
                                vector.remove(indice);
                                System.out.println("Removeu " + indice);
                                Thread.sleep((long) (10 * Math.random()));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Thread thread3 = new Thread(new Runnable() {
                    public void run() {
                        while (true) {
                            try {
                                while (true) {
                                    String inidices = "";

                                    for (int i = 0; i < vector.size(); i++) {
                                        String string = (String) vector.get(i);
                                        inidices += string;
                                    }

                                    System.out.println(inidices);
                                    Thread.sleep((long) (10 * Math.random()));
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        //        thread.start();
        //        thread2.start();
        //        thread3.start();
        Hashtable map = new Hashtable();
        map.put("Paulo", "Paulo");
        map.put("Sobreira", "Sobreira");
        map.put("Concorencia", "Concorencia");

        for (Iterator iter = map.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            map.remove(element);
        }
    }
}
