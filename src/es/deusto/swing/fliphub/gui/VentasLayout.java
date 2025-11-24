package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import es.deusto.swing.fliphub.domain.Canal;
import es.deusto.swing.fliphub.domain.Sale;

//Este panel sera la vista de las ventas en el cardlayout
public class VentasLayout extends JTable {
	
	//Componenentes que iremos rellenando
	private JTable table; //la tabla
	private DefaultTableModel model; //modelo de datos da la tabla
	private TableRowSorter<DefaultTableModel> sorter; //para ordenar/filtrar
	
	//Datos en memoria
	private List<Sale> datos = new ArrayList<>();
	
	//Formato de fecha
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	//Constructor
	public VentasLayout() {
		//Usamos BorderLayout para poder colocar la tabla en el CENTER y botones en el SOUTH
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
		//Ventas que se alimentaran desde inventario
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
                	return switch (c) {
                    	case 0,1 -> Long.class;
                    	case 4,5,6,7,8 -> Double.class;
                    	default -> String.class;
                	};
                }
			};
			
			//Cargamos los datos en el modelo
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
	
	//Creamos JTable y sorter
	private void buildTable() {
		table = new JTable(model);
		sorter = new TableRowSorter<DefaultTableModel>(model);
		table.setRowSorter(sorter);
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		
		//Centramos las celdas
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		for ( int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(center);
		}
		
		//Centramos la cabecera
		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);	
		
		this.add(new JScrollPane(table), BorderLayout.CENTER);
		//Hacemos el texto de la cabecera mas grande y en negrita
		table.getTableHeader().setFont(
			table.getTableHeader().getFont().deriveFont(Font.BOLD, 10f)
		);
	}
	
	//Creamos el boton de "Nueva Venta"
	private void buildActions() {
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		JButton btnNueva = new JButton("Nueva Venta");
		actions.add(btnNueva);
		
		//Abre el DialogVenta
		btnNueva.addActionListener(e -> {
			DialogVenta dlg = new DialogVenta((JFrame) SwingUtilities.getWindowAncestor(this));
			dlg.setVisible(true);
			Sale s = dlg.getResult();
			if (s!= null ) {
				//Añadimos a la tabla
				s.getId();
				s.getItemID();
				DF.format(s.getFechaVenta());
				s.getBeneficio();
				s.getEnvio();
				s.getCanal().name();
				s.getPrecioVenta();
				s.getImpuestos();
				s.getComisiones();
			}
		});
		this.add(actions, BorderLayout.SOUTH);
	}
	
	//Añadir una venta desde fuera(Inventario por ejemplo)
	public void addVenta(Sale s) {
	    if (s == null) return;
	    model.addRow(new Object[]{
	        s.getId(),                       // "ID"
	        s.getItemID(),                   // "ItemId"  (ojo: getItemId, no getItemID)
	        DF.format(s.getFechaVenta()),    // "Fecha"
	        s.getCanal().name(),             // "Canal"
	        s.getPrecioVenta(),              // "Precio"
	        s.getComisiones(),               // "Comisiones"
	        s.getEnvio(),                    // "Envío"
	        s.getImpuestos(),                // "Impuestos"
	        s.getBeneficio()                 // "Beneficio"
	    });
	}
		

}
