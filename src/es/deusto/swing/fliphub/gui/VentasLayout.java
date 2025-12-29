package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import es.deusto.swing.fliphub.domain.Canal;
import es.deusto.swing.fliphub.domain.Sale;

//Este panel va a ser la vista de las ventas en el cardlayout
public class VentasLayout extends JTable {
	
	//Componentes que vamos a rellenar
	private JTable table; //la tabla
	private DefaultTableModel model; //modelo de datos de la tabla
	private TableRowSorter<DefaultTableModel> sorter; //para ordenar/filtrar
	
	//Datos en memoria
	private List<Sale> datos = new ArrayList<>();
	
	//Formato de fecha
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	//Campo para detectar si la fila esta seleccionada
	private int hoverRow = -1;
	
	//Constructor
	public VentasLayout() {
		//BorderLayout para poder colocar la tabla en el CENTER y botones en el SOUTH
		this.setLayout(new BorderLayout(8,8));
		//Margen interno para que la vista no se pegue a los bordes
		this.setBorder(new EmptyBorder(8,8,8,8));
		//Datos de ejemplo
		seed();
		//Define columnas y carga filas
		buildModel();
		//Crea JTable + sorter + renderes
		buildTable();
		//Crea botonera "Nueva Venta"
		buildActions();
	}
	
	//Datos de ejemplo
	private void seed() {
		//Ventas que se rellenaran desde inventario
		datos.add(new Sale(1, 2, LocalDate.now().minusDays(3),  Canal.WALLAPOP, 25.0, 2.0, 1.9, 0.0));
	    datos.add(new Sale(2, 1, LocalDate.now().minusDays(15), Canal.VINTED,   60.0, 6.0, 3.5, 0.0));
	}
	
	//Modelo de tabla y carga
	private void buildModel() {
		model = new DefaultTableModel(
				new Object[] {"ID", "ItemId", "Fecha", "Canal", "Precio", "Comisiones", "Envío", "Impuestos", "Beneficio"},
				0 //0 filas inciales, ahora las añadiremos
		) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return false;
			} 	
			@Override public Class<?> getColumnClass(int c) {
				// Esto ayuda a ordenar/representar bien cada columna
				//Uso de IAGenerativa
                return switch (c) {
                	case 0,1 -> Long.class;
                	case 4,5,6,7,8 -> Double.class;
                	default -> String.class;
                };
            }
		};
			
		//Carga los datos en el modelo
		for (Sale s : datos) {
			model.addRow( new Object[] {
					s.getId(),
					s.getItemID(),
					DF.format(s.getFechaVenta()),
					s.getCanal().name(),
					s.getPrecioVenta(),
					s.getComisiones(),
					s.getEnvio(),
					s.getImpuestos(),
					s.getBeneficio()
			});
		}
	}
	
	//Crea JTable y sorter
	private void buildTable() {
		table = new JTable(model) {
			@Override
			public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int column) {
				Component c = super.prepareRenderer(r, row, column);
				//Si la fila no esta seleccionada decidimos si poner hover o zebra
				if(!isRowSelected(row)) {
					if( row == hoverRow) {
						c.setBackground(getSelectionBackground());
						c.setForeground(getSelectionForeground());
					} else {
						c.setBackground((row % 2 == 0) ? Color.WHITE : new Color(247, 250, 252) );
						c.setForeground(Color.DARK_GRAY);
					}
				}
				return c;
			}
		};
		
		//Atajo de teclado
		table.addKeyListener(new KeyAdapter() {

		    @Override
		    public void keyPressed(KeyEvent e) {

		        int row = table.getSelectedRow();
		        if (row == -1) return;

		        // SUPR: eliminar venta
		        if (e.getKeyCode() == KeyEvent.VK_DELETE) {
		            int modelRow = table.convertRowIndexToModel(row);
		            model.removeRow(modelRow);
		        }
		    }
		});
		
		//Listeners para el hoverRow
		table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				int r = table.rowAtPoint(e.getPoint());
				if (r != hoverRow) {
					hoverRow = r;
					table.repaint();
				}
			}
		});
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				hoverRow = -1;
				table.repaint();
			}
		});
		
		sorter = new TableRowSorter<DefaultTableModel>(model);
		table.setRowSorter(sorter);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.setRowHeight(26);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setGridColor(new Color(230, 235, 240));
		table.setIntercellSpacing(new Dimension(0,1));
		table.setSelectionBackground(new Color(187, 222, 251));
		table.setSelectionForeground(Color.BLACK);
		
		//Centra las celdas
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		for ( int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(center);
		}
		
		//Estilo de la cabecera
		JTableHeader header = table.getTableHeader();

		// Crea un nuevo renderer para la cabecera
		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
		    @Override
		    public Component getTableCellRendererComponent(
		            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

		        JLabel lbl = (JLabel) super.getTableCellRendererComponent(
		                table, value, isSelected, hasFocus, row, column);

		        lbl.setHorizontalAlignment(SwingConstants.CENTER);
		        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 12f));
		        lbl.setBackground(new Color(47, 79, 79)); 
		        lbl.setForeground(Color.WHITE);
		        lbl.setOpaque(true); 
		        return lbl;
		    }
		};

		// Aplica este renderer a todas las columnas de la cabecera
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
		    table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
		}
		
		//Centra la cabecera
		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);	
		
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		//Hace el texto de la cabecera mas grande y en negrita
		table.getTableHeader().setFont(
			table.getTableHeader().getFont().deriveFont(Font.BOLD, 10f)
		);
	}
	
	//Crea el boton de "Nueva Venta"
	private void buildActions() {
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		JButton btnNueva = new JButton("Nueva Venta");
		actions.add(btnNueva);
		
		//Abre el DialogVenta
		btnNueva.addActionListener(e -> {
			DialogVenta dlg = new DialogVenta((JFrame) SwingUtilities.getWindowAncestor(this));
			dlg.setVisible(true);
			Sale s = dlg.getResult();
			if (s != null ) {
				//Añade a la tabla usando el método ya definido
				addVenta(s);
			}
		});
		this.add(actions, BorderLayout.SOUTH);
	}
	
	//Añadir una venta desde fuera(Inventario por ejemplo)
	public void addVenta(Sale s) {
	    if (s == null) return;
	    model.addRow(new Object[]{
	        s.getId(),                       // "ID"
	        s.getItemID(),                   // "ItemId"
	        DF.format(s.getFechaVenta()),    // "Fecha"
	        s.getCanal().name(),             // "Canal"
	        s.getPrecioVenta(),              // "Precio"
	        s.getComisiones(),               // "Comisiones"
	        s.getEnvio(),                    // "Envío"
	        s.getImpuestos(),                // "Impuestos"
	        s.getBeneficio()                 // "Beneficio"
	    });
	}
	
	//Metodo para que EstadisticasLayout pueda leer los datos
	public javax.swing.table.DefaultTableModel getModel(){
		return model;
	}
	
	//metodo para el atajo de teclado
	public void deleteSelectedSale() {
	    int row = table.getSelectedRow();
	    if (row == -1) return;

	    int modelRow = table.convertRowIndexToModel(row);
	    model.removeRow(modelRow);
	}
}
