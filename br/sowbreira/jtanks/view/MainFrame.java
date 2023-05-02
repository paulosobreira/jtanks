package br.sowbreira.jtanks.view;

import br.sowbreira.jtanks.adaptadoresNet.AdaptadorNetCliente;
import br.sowbreira.jtanks.adaptadoresNet.AdaptadorNetServidor;
import br.sowbreira.jtanks.cotrole.ControleJtanks;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.IOException;

import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 * @author Paulo Created on 31/12/2004
 */
public class MainFrame extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = -284357233387917389L;
	private ControleJtanks controleJtanks;
	private AdaptadorNetServidor adaptadorNetServidor;

	public MainFrame() throws IOException {
		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);

		JMenu menu1 = new JMenu("Jogo");
		bar.add(menu1);

		JMenu menu3 = new JMenu("Multiplayer");
		bar.add(menu3);

		JMenu menu2 = new JMenu("Sobre");
		bar.add(menu2);

		JMenuItem iniciarServidorTCP = new JMenuItem("Iniciar Servidor TCP");
		iniciarServidorTCP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msg = "Entre com o número da porta então veja no \n "
							+ " console os ids de clientes que se conectam.";
					adaptadorNetServidor = new AdaptadorNetServidor(Integer.parseInt(JOptionPane.showInputDialog(msg)),
							false);
					adaptadorNetServidor.iniciarServidorJogo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		menu3.add(iniciarServidorTCP);

		JMenuItem iniciarServidorUDP = new JMenuItem("Iniciar Servidor UDP");
		iniciarServidorUDP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msg = "Entre com o número da porta e então veja no \n"
							+ "console os ids de clientes que se conectam.";
					adaptadorNetServidor = new AdaptadorNetServidor(Integer.parseInt(JOptionPane.showInputDialog(msg)),
							true);
					adaptadorNetServidor.iniciarServidorJogo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		menu3.add(iniciarServidorUDP);

		JMenuItem iniciarJogoServidor = new JMenuItem("Iniciar Jogo Multiplayer");
		iniciarJogoServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (adaptadorNetServidor != null) {
						adaptadorNetServidor.iniciarJogo();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		menu3.add(iniciarJogoServidor);

		JMenuItem conectarServidor = new JMenuItem("Conectar a um servidor");
		conectarServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String[] dados = // "paulo:localhost:8080".split(":");
							JOptionPane.showInputDialog("SeuNome:Ip:Porta").split(":");
					controleJtanks = new AdaptadorNetCliente(dados[0], dados[1], Integer.parseInt(dados[2]));
					controleJtanks.iniciarJogo();
					addKeyListener(controleJtanks);
					getContentPane().add(controleJtanks.getPanel());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		menu3.add(conectarServidor);

		JMenuItem debugarServidor = new JMenuItem("Debugar Servidor");
		debugarServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					if (adaptadorNetServidor == null) {
						return;
					}

					adaptadorNetServidor.modoDebug = true;

					JFrame frame = new JFrame("Janela de debug");
					frame.setSize(800, 600);
					frame.getContentPane().add(adaptadorNetServidor.getPanel());
					frame.setVisible(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		menu3.add(debugarServidor);

		// ==============================================================================
		JMenuItem sobreNet = new JMenuItem("Versão em rede");
		sobreNet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = "Feito por Paulo Sobreira \n" + "sowbreira@gmail.com \n"
						+ "https://sowbreira-26fe1.firebaseapp.com/ \n Maio de 2006 Maio de 2023";
				JOptionPane.showMessageDialog(MainFrame.this, msg, "Sobre", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu2.add(sobreNet);

		JMenuItem sobre = new JMenuItem("Versão Single");
		sobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = "Feito por Paulo Sobreira \n" + "sowbreira@gmail.com \n"
						+ "https://sowbreira-26fe1.firebaseapp.com/ \n Setembro de 2005";
				JOptionPane.showMessageDialog(MainFrame.this, msg, "Sobre", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu2.add(sobre);

		getContentPane().setLayout(null);
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				MainFrame.this.requestFocus();
				System.out.println(e.getPoint());
			}
		});

		JMenuItem controles = new JMenuItem("Comandos");
		controles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg = "Setas move e ins atira - Player 1. \n"
						+ "Teclas a,s,d,w move  e spaco atira - Player 2. \n"
						+ "Cada flag capturada pode valer de 1 a 10 \n "
						+ "O tank atingido de volta pra base. Perde de 1 a 5 flags";
				JOptionPane.showMessageDialog(MainFrame.this, msg, "Sobre", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu1.add(controles);

		JMenuItem iniciar = new JMenuItem("Iniciar Jogo");
		iniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					controleJtanks = new ControleJtanks();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				controleJtanks.iniciarJogo();
				addKeyListener(controleJtanks);
				getContentPane().add(controleJtanks.getPanel());
			}
		});
		menu1.add(iniciar);

		JMenuItem telaCheia = new JMenuItem("Tela Cheia");
		telaCheia.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice device = environment.getDefaultScreenDevice();
					setUndecorated(true);
					setIgnoreRepaint(true);
					setResizable(false);
					if (device.isFullScreenSupported()) {
						device.setFullScreenWindow(MainFrame.this);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		menu1.add(telaCheia);

		setSize(810, 660);
		setTitle("JTanks Computação Distribida (Certifique ter 32bit de cor)");

		setVisible(true);
	}

	public static void main(String[] args) throws IOException {
		MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
}
