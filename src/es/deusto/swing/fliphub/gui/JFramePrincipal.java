package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class JFramePrincipal extends JFrame {

	
	/**
	 * 
	 * Ventana principal de FlipHub (esqueleto Fase 0).
	 * - Norte: barra superior (buscar + "+ Añadir ítem")
	 * - Oeste: menú lateral (Inventario, Ventas, Estadísticas, Import/Export)
	 * - Centro: CardLayout con 4 vistas
	 * - Sur: barra de estado
	 */
	
	//Constantes con las que registraremos las vistas dentro de CardLayout
	public static final String CARD_INV = "INV";
	public static final String CARD_VEN = "VEN";
	public static final String CARD_EST = "EST";
	
	
	//Atributos(Componentes que se usan en varios metodos)
	private JTextField txtBuscar; //Barra para buscar
	private JButton btnAñadirItem; //Boton para añadir item (no usado aún)
	private JPanel cards; //contenedor en el centro con CardLayout
	private CardLayout cardLayout; //layout de cards
	private JLabel statusLabel; //la barra de estados
	private InventarioLayout inventarioPanel; //Vista del inventario
	private VentasLayout ventasPanel; //Vista de las ventas
	
	//Constructor 
	public JFramePrincipal() {
		super("FlipHub"); //titulo de la ventana
		initUI(); //crea y coloca los componentes
		bindActions(); //conecta eventos
		//Vista Inicial
		showCard(CARD_INV); //muestra por defecto la vista de inventario
		setVisible(true);
	}	
	
	//METODO INITUI -> MONTAJE DE LA INTERFAZ
	private void initUI() {
		//Frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout( new BorderLayout());
		this.setSize(1000,650);
		this.setLocationRelativeTo(null);
		
		//Barra Superior Norte(Buscador + Boton de añadir)
		JPanel topBar = new JPanel( new BorderLayout(8,0));
		topBar.setBorder(new EmptyBorder(10,12,10,12));
		
		txtBuscar = new JTextField();
		txtBuscar.setToolTipText("Buscador");
		
		topBar.add(txtBuscar, BorderLayout.CENTER);
		
		//Menu Lateral Oeste
		JPanel leftNav = new JPanel();
		leftNav.setLayout(new GridLayout(0,1,0,8));
		leftNav.setBorder(new EmptyBorder(12,12,12,8));
		
		//Crea los botones del menu lateral
		JButton btnInventario = makeNavButton("Inventario", "icons/inventory.png");
		JButton btnVentas = makeNavButton("Ventas", "icons/sales.png");
		JButton btnEstadisticas = makeNavButton("Estasisticas", "icons/stats.png");
		
		//Define un tamaño para los botones y los añadimos al menu lateral
		Dimension navBtnSize = new Dimension(140,36);
		for ( JButton b : new JButton[]{btnInventario,btnVentas,btnEstadisticas}) {
			b.setPreferredSize(navBtnSize);
			leftNav.add(b);
		}
		
		//Zona central con CardLayout
		cardLayout = new CardLayout(); //Permite cambiar entre paneles
		cards = new JPanel(cardLayout); //Contenedor que tendra CardLayout
		cards.setBorder(new EmptyBorder(12,8,12,12));
		
		//Paneles
		ventasPanel = new VentasLayout();
		inventarioPanel = new InventarioLayout();
		inventarioPanel.setVentasLayoutRef(ventasPanel);
		EstadisticasLayout estadisticasPanel = new EstadisticasLayout(inventarioPanel, ventasPanel);
        
        cards.add(inventarioPanel, CARD_INV);
        cards.add(ventasPanel, CARD_VEN);
        cards.add(estadisticasPanel, CARD_EST);
        
        // Refrescar estadísticas cuando cambien ventas o inventario
        ventasPanel.getModel().addTableModelListener(e -> estadisticasPanel.refresh());
        inventarioPanel.getModel().addTableModelListener(e -> estadisticasPanel.refresh());
        
        // Primer cálculo al arrancar
        estadisticasPanel.refresh();
        
        //Barra de estados Sur
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(6,12,6,12));
        statusLabel = new JLabel("Items: 0 | En stock: 0 | Beneficio mes: 0€");
        statusBar.add(statusLabel, BorderLayout.WEST);
        
        //Añade todas las partes al FRAME
        this.add(topBar, BorderLayout.NORTH);
        this.add(leftNav, BorderLayout.WEST);
        this.add(cards, BorderLayout.CENTER);
        this.add(statusBar, BorderLayout.SOUTH);
        
        //Acciones de navegacion
        btnInventario.addActionListener(e -> showCard(CARD_INV));
        btnVentas.addActionListener(e -> showCard(CARD_VEN));
        btnEstadisticas.addActionListener(e -> showCard(CARD_EST));
	}
	
	//METODO BINDSACTIONS -> CONECTAR ACCIONES 
	private void bindActions() {
		txtBuscar.addActionListener((ActionEvent e) -> {
				if(inventarioPanel != null) {
					inventarioPanel.appllyQuickFilter(txtBuscar.getText()); //Conectamos el filtro que hemos creado en InventarioLayout
				}
		});
		
	}
	
	//Cambia la carta visible en el panel central
	public void showCard (String cardName) {
		cardLayout.show(cards, cardName);
	}
	
	//Actualiza el texto de la barra de estado
	public void setStatus(String text) {
			statusLabel.setText(text);
	}
	
	//Devuelve el texto actual de la barra de busqueda
	public String getSearchText() {
		return txtBuscar.getText();
	}
	
	//Crea un panel simple de marcador de posicion (por si lo necesitas en el futuro)
	private JPanel placeholderPanel(String title) {
		JPanel p = new JPanel(new BorderLayout());
		JLabel lbl = new JLabel(title, SwingConstants.CENTER);
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 18f));
		p.add(lbl, BorderLayout.CENTER);
		p.setBorder(new EmptyBorder(24,24,24,24));
		return p;
	}
	
	//Helper para los iconos
	private Icon loadIcon(String path, int w, int h) {
		java.net.URL url = getClass().getResource("/" + path);
		if(url == null) return null; //Si no existe el boton funciona sin icono
		Image img = new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
		return new ImageIcon(img);
	}
	
	//Creador de los botones laterales con icono
	private JButton makeNavButton(String texto, String iconPath) {
		JButton b = new JButton();
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setContentAreaFilled(false);
		b.setHorizontalAlignment(SwingConstants.CENTER);
		
		Icon icono = loadIcon(iconPath, 80, 80);
		 // Si hay icono -> icono por defecto y texto en hover.
	    // Si NO hay icono -> muestra texto siempre (no alterna).
	    if (icono != null) {
	        b.setIcon(icono);
	        b.setText("");
	    } else {
	        b.setIcon(null);
	        b.setText(texto);
	    }
		b.setToolTipText(texto);
		
		//Listeners para detectar si esta el raton encima
		b.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				b.setText(" " + texto);
				b.setIcon(null);
				b.setOpaque(true);
				b.setBackground(new Color(230, 242, 255));
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				b.setText("");
				b.setIcon(icono);
				b.setOpaque(false);
			}
		});
		return b;
	}
	
}
