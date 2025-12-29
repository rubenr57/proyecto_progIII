package es.deusto.swing.fliphub.gui;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.io.File;
import es.deusto.swing.fliphub.util.Recursividad;
import es.deusto.swing.fliphub.gui.GradientPanel;

import es.deusto.swing.fliphub.util.AutoSaveService;





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
	private AutoSaveService autosaveService; //hilo de autosave
	
	private int numIcons = 0;

	
	//Constructor 
	public JFramePrincipal() {
		super("FlipHub"); //titulo de la ventana
		initUI(); //crea y coloca los componentes
		bindActions(); //conecta eventos
		
		//Atajos de teclado
		this.setFocusable(true);
		this.requestFocus();
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				//tecla F enfoca el buscador
				if (e.getKeyCode() == KeyEvent.VK_F) {
					txtBuscar.requestFocus();
					txtBuscar.selectAll();
				}	
			}
		});
		
		//Vista Inicial
		showCard(CARD_INV); //muestra por defecto la vista de inventario
		setVisible(true);
	}	
	
	//METODO INITUI -> MONTAJE DE LA INTERFAZ
	private void initUI() {
		//fondo con degradado para el JFrame 
		GradientPanel root = new GradientPanel(
		        new Color(20, 30, 45),   // arriba
		        new Color(45, 75, 110)   // abajo
		);
		root.setLayout(new BorderLayout());
		this.setContentPane(root);

		//Frame
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout( new BorderLayout());
		this.setSize(1000,650);
		this.setLocationRelativeTo(null);
		
		
		//Barra superior
		JPanel topBar = new JPanel(new BorderLayout(10, 0));
		topBar.setBorder(new EmptyBorder(10, 12, 10, 12));
		topBar.setOpaque(false); // para que se vea el degradado

		//Logo a la izquierda
		Icon logoIcon = loadIcon("icons/LogoFlipHub.png", 50, 50);
		JLabel lblLogo = new JLabel(logoIcon);
		lblLogo.setHorizontalAlignment(SwingConstants.LEFT);
		
		//Buscador
		txtBuscar = new JTextField();
		txtBuscar.setToolTipText("Buscar en inventario...");
		txtBuscar.setMaximumSize(new Dimension(1000, 28));
		txtBuscar.setPreferredSize(new Dimension(1000, 28));
		txtBuscar.setMinimumSize(new Dimension(1000, 28));


		//Panel para el buscador
		JPanel searchPanel = new JPanel();
		searchPanel.setOpaque(false);
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

		//Espacio izquierda
		searchPanel.add(Box.createHorizontalGlue());

		// Buscador en el centro
		searchPanel.add(txtBuscar);

		//Espacio derecha
		searchPanel.add(Box.createHorizontalGlue());


		//Panel izquierdo solo para el logo
		JPanel leftTop = new JPanel(new BorderLayout());
		leftTop.setOpaque(false);
		leftTop.add(lblLogo, BorderLayout.WEST);

		
		topBar.add(leftTop, BorderLayout.WEST);
		topBar.add(searchPanel, BorderLayout.CENTER);

		
		//Menu Lateral Oeste
		JPanel leftNav = new JPanel();
		leftNav.setLayout(new GridLayout(0,1,0,8));
		leftNav.setBorder(new EmptyBorder(12,12,12,8));
		leftNav.setOpaque(false);

		
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
		cards.setOpaque(false);

		
		//Paneles
		ventasPanel = new VentasLayout();
		inventarioPanel = new InventarioLayout();
		inventarioPanel.setVentasLayoutRef(ventasPanel);
		
		//cargo los datos desde la BD a los modelos de la tablas
		es.deusto.swing.fliphub.db.Persistencia.cargarEnModelos(
				inventarioPanel.getModel(),
		        ventasPanel.getModel()
				);
		
		//creo el panel de estadisticas con los modelos ya cargados
		EstadisticasLayout estadisticasPanel = new EstadisticasLayout(inventarioPanel, ventasPanel);
        
        cards.add(inventarioPanel, CARD_INV);
        cards.add(ventasPanel, CARD_VEN);
        cards.add(estadisticasPanel, CARD_EST);
        
        //creo el autosave cada 10 segundos
        autosaveService = new AutoSaveService(inventarioPanel, ventasPanel, 10_000);
        autosaveService.start();
        
        // Refrescar estadísticas cuando cambien ventas o inventario
        ventasPanel.getModel().addTableModelListener(e -> estadisticasPanel.refreshHilos());
        inventarioPanel.getModel().addTableModelListener(e -> estadisticasPanel.refreshHilos());
        
        
        // Primer cálculo al arrancar
        estadisticasPanel.refreshHilos();
        
        //Barra de estados Sur
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(new EmptyBorder(6,12,6,12));
        statusLabel = new JLabel("Items: 0 | En stock: 0 | Beneficio mes: 0€");
        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.setOpaque(false);
        
        //actualizar barra de estado cuando cambian los datos
        ventasPanel.getModel().addTableModelListener(e -> updateStatusBar(numIcons));
        inventarioPanel.getModel().addTableModelListener(e -> updateStatusBar(numIcons));

        updateStatusBar(numIcons);
    

        
        //Panel de botones para el autosave
        JButton btnPausarAutosave = new JButton("Pausar autosave");
        JButton btnReanudarAutosave = new JButton("Reanudar autosave");
        
        JPanel autosavePanel = new JPanel();
        autosavePanel.add(btnPausarAutosave);
        autosavePanel.add(btnReanudarAutosave);
        autosavePanel.setOpaque(false); 
        
        statusBar.add(autosavePanel, BorderLayout.EAST);
        
        btnPausarAutosave.addActionListener(e -> {
        	if (autosaveService != null) {
        			autosaveService.pauseAutosave();
        			statusLabel.setText("Autosave pausado");
        	}
        });
        
        btnReanudarAutosave.addActionListener(e -> {
        	if (autosaveService != null) {
        		autosaveService.resumeAutosave();
        		statusLabel.setText("Autosave activo");
        	}
        });
        
        
        //Añade todas las partes al FRAME
        this.add(topBar, BorderLayout.NORTH);
        this.add(leftNav, BorderLayout.WEST);
        
        JPanel centerCard = new JPanel(new BorderLayout());
        centerCard.setBackground(Color.WHITE);
        centerCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerCard.add(cards, BorderLayout.CENTER);
        this.add(centerCard, BorderLayout.CENTER);


        this.add(statusBar, BorderLayout.SOUTH);
        
        //Acciones de navegacion
        btnInventario.addActionListener(e -> showCard(CARD_INV));
        btnVentas.addActionListener(e -> showCard(CARD_VEN));
        btnEstadisticas.addActionListener(e -> {showCard(CARD_EST); estadisticasPanel.refreshHilos();});
        
        //Recursividad 
        numIcons = Recursividad.countImagesRecursive(new File("resources/images"));
        System.out.println("[Recursividad] Iconos encontrados: " + numIcons);

	}
	


	
	//METODO BINDSACTIONS -> CONECTAR ACCIONES 
	private void bindActions() {
		txtBuscar.addActionListener((ActionEvent e) -> {
				if(inventarioPanel != null) {
					inventarioPanel.appllyQuickFilter(txtBuscar.getText()); //Conectamos el filtro que hemos creado en InventarioLayout
				}
		});
		
	}
	
	//Recalcula y pone la barra de estado (Items | En stock | Beneficio mes | Iconos)
	private void updateStatusBar(int numIcons) {

	    //ITEMS y EN STOCK desde Inventario
	    javax.swing.table.DefaultTableModel mInv = inventarioPanel.getModel();
	    int totalItems = mInv.getRowCount();

	    int colEstado = findCol(mInv, "Estado");
	    int enStock = 0;

	    for (int r = 0; r < mInv.getRowCount(); r++) {
	        String estado = String.valueOf(mInv.getValueAt(r, colEstado));
	        if ("EN_STOCK".equalsIgnoreCase(estado)) {
	            enStock++;
	        }
	    }

	    //BENEFICIO MES desde Ventas
	    javax.swing.table.DefaultTableModel mVen = ventasPanel.getModel();

	    int colFecha = findCol(mVen, "Fecha");        
	    int colBenef = findCol(mVen, "Beneficio");

	    java.time.YearMonth thisMonth = java.time.YearMonth.now();
	    java.time.format.DateTimeFormatter df = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    double beneficioMes = 0.0;

	    for (int r = 0; r < mVen.getRowCount(); r++) {
	        String f = String.valueOf(mVen.getValueAt(r, colFecha));

	        java.time.LocalDate fecha;
	        try {
	            fecha = java.time.LocalDate.parse(f, df);
	        } catch (Exception ex) {
	            continue; // si alguna fecha está mal la saltamos
	        }

	        if (java.time.YearMonth.from(fecha).equals(thisMonth)) {
	            Object bObj = mVen.getValueAt(r, colBenef);
	            double b = toDouble(bObj);
	            if (!Double.isNaN(b)) beneficioMes += b;
	        }
	    }

	    //texto final
	    statusLabel.setText(String.format(
	        "Items: %d | En stock: %d | Beneficio mes: %.2f€ | Iconos: %d",
	        totalItems, enStock, beneficioMes, numIcons
	    ));
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
	
	//Busca el índice de una columna por nombre
	private int findCol(javax.swing.table.DefaultTableModel m, String name) {
	    for (int i = 0; i < m.getColumnCount(); i++) {
	        if (name.equalsIgnoreCase(m.getColumnName(i))) return i;
	    }
	    return -1;
	}

	//Convierte cualquier celda a double
	private double toDouble(Object v) {
	    if (v == null) return Double.NaN;
	    if (v instanceof Number n) return n.doubleValue();
	    try {
	        return Double.parseDouble(String.valueOf(v).replace(',', '.'));
	    } catch (Exception e) {
	        return Double.NaN;
	    }
	}

	
}
