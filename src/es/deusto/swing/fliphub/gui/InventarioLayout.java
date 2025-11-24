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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;



import es.deusto.swing.fliphub.domain.Estado;
import es.deusto.swing.fliphub.domain.Item;
import es.deusto.swing.fliphub.domain.Sale;

//Este panel sera la vista del inventario en el cardlayout
public class InventarioLayout extends JTable {
	
	//Componentes que iremos rellenando
	private JTable table;	//la tabla
	private DefaultTableModel model;	//modelo de datos de la tabla
	private TableRowSorter<DefaultTableModel> sorter; //para ordenar/filtrar
	//Setter para guardar la referencia de la venta
	private VentasLayout ventasref;
	public void setVentasLayoutRef(VentasLayout v) {this.ventasref = v; }
	
	//Datos de ejemplo 
	private final List<Item> datos = new ArrayList();
	
	//Formato de fecha
	private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	//Constructor
	public InventarioLayout() {
		//Usamos BorderLayout para poder colocar la tabla en el CENTER y botones en el SOUTH
		this.setLayout(new BorderLayout(8,8));
		//Margen interno para que la vista no se pegue a los bordes
		this.setBorder(new EmptyBorder(8,8,8,8));
		//Sembramos datos
		seed();
		
		//Modelo de la tabla, define columnas y tipos
		model = new DefaultTableModel(
				new Object[]{
				        "ID",            // 0
				        "Nombre",        // 1
				        "Categoría",     // 2
				        "Estado",        // 3
				        "Compra €",      // 4
				        "Fecha compra",  // 5
				        "Ubicación",     // 6
				        "Beneficio €",   // 7
				        "ROI %"          // 8
				    },
				    0

		) {
			@Override public boolean isCellEditable(int row, int column) {
				return false;
		}
		@Override public Class<?> getColumnClass(int columnIndex) {
		        // Esto ayuda a ordenar/representar bien cada columna
		        return switch (columnIndex) {
		          	case 0 -> Long.class;   // ID
		            case 4 -> Double.class; // Compra €
		            case 7 -> Double.class; //Beneficio €
		            case 8 -> Double.class; //ROI %
		            default -> String.class;
		        };
			
			}
		};
		
		//Cargamos los datos en el modelo
		for (Item it : datos) {
		    model.addRow(new Object[]{
		            it.getID(),
		            it.getNombre(),
		            it.getCategoria(),
		            it.getEstado().name(),              // mostramos el enum como texto
		            it.getPrecioCompra(),
		            DF.format(it.getFechaCompra()),     // LocalDate -> texto "dd/MM/yyyy"
		            it.getUbicacion(),
		            null,								//Beneficio aun sin vender
		            null								//ROI aun sin vender
		    });
		}
		
		//Creamos JTable y Sorter
		table = new JTable(model);
		table.setFillsViewportHeight(true); //la tabla ocupa todo el viewport del scroll
		table.setAutoCreateRowSorter(true); //ordenacion basica
		
		//Creamos un TableRowSorter explicito, lo vamos a usar para el filtrado por texto
		sorter = new TableRowSorter<DefaultTableModel>(model);
		table.setRowSorter(sorter);
		
		//Centramos el texto en todas las columnas
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}
		
		//Centramos el texto de la cabecera
		((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer())
		.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Hacemos el texto de la cabecera mas grande y en negrita
		table.getTableHeader().setFont(
				table.getTableHeader().getFont().deriveFont(Font.BOLD, 10f)
				);
		
		//Renderer para dejar los numeros con 2 decimales
		DefaultTableCellRenderer num2dec = new DefaultTableCellRenderer() {
			@Override
			protected void setValue(Object value) {
				if ( value == null){
					this.setText("");
				} else if ( value instanceof Number) {
					this.setText(String.format("%.2f", ((Number) value).doubleValue()));
				} else {
					this.setText(value.toString());
				}
				this.setHorizontalAlignment(SwingConstants.CENTER);
			}
			
		};
		
		//Aplicamos el nuevo renderer de 2 decimales a Compra, Beneficio Y ROI
		table.getColumnModel().getColumn(4).setCellRenderer(num2dec);
		table.getColumnModel().getColumn(7).setCellRenderer(num2dec);
		table.getColumnModel().getColumn(8).setCellRenderer(num2dec);
		
		//Botonera de acciones de la tabla en la parte inferior
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
		JButton btnañadir = new JButton("Añadir");
		JButton btneditar = new JButton("Editar");
		JButton btneliminar = new JButton("Eliminar");
		JButton btnVender = new JButton("Vender");
		actions.add(btnañadir);
		actions.add(btneditar);
		actions.add(btneliminar);
		actions.add(btnVender);
		
		//Listeners de los botones
		btnañadir.addActionListener(e -> {
			JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
			DialogItem dlg = new DialogItem(parent);
			dlg.setVisible(true);
			Item it = dlg.getResult();
			if (it != null) {
				// Añadir fila al modelo ahora tienemos 9 columnas
		        model.addRow(new Object[]{
		                it.getID(),
		                it.getNombre(),
		                it.getCategoria(),
		                it.getEstado().name(),
		                it.getPrecioCompra(),
		                DF.format(it.getFechaCompra()),
		                it.getUbicacion(),
		                null, // Beneficio
		                null  // ROI
		        });
			}
		});
		
		btneditar.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			
			//Si no hay filas seleccionadas
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(
						this,
						"Por favor selecciona una fila para editar.",
						"Aviso",
						JOptionPane.WARNING_MESSAGE
						);
				return;
			}
			
			//Convertimos indice de vista a indice de modelo, por si hay filtro
			int modelRow = table.convertColumnIndexToModel(selectedRow);
			
			//Recuperamos los valores de esa fila
			long id = (long) model.getValueAt(modelRow, 0);
			String nombre = (String) model.getValueAt(modelRow, 1);
			String categoria = (String) model.getValueAt(modelRow, 2);
			Estado estado = Estado.valueOf((String) model.getValueAt(modelRow, 3));
			double precioCompra = (double) model.getValueAt(modelRow, 4);
			java.time.LocalDate fechaCompra = java.time.LocalDate.parse(
		            (String) model.getValueAt(modelRow, 5),
		            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
		    );
			String ubicacion = (String) model.getValueAt(modelRow, 6);
			
			//Conseguimos el item actual
			Item actual = new Item(id, nombre, categoria, estado, precioCompra, fechaCompra, ubicacion);
			
			//Abrimos el dialogo en modo edicion
			JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);		
			DialogItem dlg = new DialogItem(parent, actual);
			dlg.setVisible(true);
			Item mod = dlg.getResult();
			
			if (mod != null) {
				//Actualizamos la fila con los nuevos datos
				model.setValueAt(mod.getNombre(), modelRow, 1);
				model.setValueAt(mod.getCategoria(), modelRow, 2);
				model.setValueAt(mod.getEstado().name(), modelRow, 3);
				model.setValueAt(mod.getPrecioCompra(), modelRow, 4);
				model.setValueAt(mod.getFechaCompra()
						.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
						modelRow, 5);
				model.setValueAt(mod.getUbicacion(), modelRow, 6);
				
				//Si el estado se cambia a EN_STOCK, limpia el Beneficio y el ROI
				if (mod.getEstado() != Estado.VENDIDO) {
					model.setValueAt(null, modelRow, 7); //Beneficio
					model.setValueAt(null, modelRow, 8); //ROI
				}
			}
		});
		
		btneliminar.addActionListener(e -> {
			int selectedRow = table.getSelectedRow();
			
			//Si no hay filas seleccionas
			if (selectedRow == -1) {
				JOptionPane.showMessageDialog(
						this,
						"Por favor selecciona una fila para eliminar.",
						"Aviso",
						JOptionPane.WARNING_MESSAGE
						);
				return;
			}
			
			//Convertimos indice de vista a indice de modelo, por si hay filtro
			int modelRow = table.convertRowIndexToModel(selectedRow);
			
			//Comfirmacion antes de eliminar
			int confirm = JOptionPane.showConfirmDialog(
					this,
					"Estas seguro de que quieres elimnar el item " + 
					model.getValueAt(modelRow, 1) + "?",
					"Confirmar eliminacion",
					JOptionPane.YES_NO_OPTION
					);
			
			if (confirm == JOptionPane.YES_OPTION) {
				model.removeRow(modelRow);
			}
		});
		
		btnVender.addActionListener(e -> {
			int selected = table.getSelectedRow();
			if (selected == -1 ) {
				JOptionPane.showMessageDialog(
						this,
						"Selecciona un item que quieras vender.",
						"Aviso",
						JOptionPane.WARNING_MESSAGE
						);
				return;
			}
			
			//Convertimos indice de vista a indice de modelo por si hay filtros
			int modelRow = table.convertRowIndexToModel(selected);
			
			//Leemos el item de la columna 0 que es el ID
			long itemID = (long) model.getValueAt(modelRow, 0);
			
			//Abrimos el dialogo con el item pre-rellenado
			JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
			DialogVenta dlg = new DialogVenta(parent, itemID);
			dlg.setVisible(true);
			
			//Recuperamos el resultado
			Sale s = dlg.getResult();
			if ( s != null) {
				//Añadimos la venta a Ventas
				if ( ventasref != null) {
					ventasref.addVenta(s);
				}
				//Cambiar el estado del item a Vendido
				model.setValueAt("VENDIDO", modelRow, 3);
				
				//Calculo de Beneficio y ROI
				double precioCompra = ((Number) model.getValueAt(modelRow, 4)).doubleValue(); 
				double beneficio = s.getPrecioVenta() - (precioCompra + s.getComisiones() +s.getEnvio() + s.getImpuestos());
				Double roi = (precioCompra > 0) ? (beneficio / precioCompra) * 100.0 : null;
				
				//Escribir los datos en las columnas
				model.setValueAt(beneficio, modelRow, 7);
				model.setValueAt(roi, modelRow, 8);

			}
		});
		
		//Añadimos la tabla creada y los botones a la vista
		this.add(new JScrollPane(table), BorderLayout.CENTER); //la tabla centrada
		this.add(actions, BorderLayout.SOUTH); //los botones abajo
		
	}
	
	//5 items de prueba
	private void seed() {
	        datos.add(new Item(1, "Zapatillas Nike Air", "Calzado",
	                Estado.EN_STOCK, 45.00, LocalDate.now().minusDays(10), "Armario A"));

	        datos.add(new Item(2, "Camiseta NBA", "Textil",
	                Estado.VENDIDO, 12.50, LocalDate.now().minusDays(30), "Armario B"));

	        datos.add(new Item(3, "Jersey vintage", "Textil",
	                Estado.EN_STOCK, 7.99, LocalDate.now().minusDays(5), "Caja 1"));

	        datos.add(new Item(4, "Funko POP", "Coleccionismo",
	                Estado.RESERVADO, 9.50, LocalDate.now().minusDays(20), "Estantería"));

	        datos.add(new Item(5, "Balón Spalding", "Deporte",
	                Estado.EN_STOCK, 20.00, LocalDate.now().minusDays(2), "Armario C"));
	   }
	
	//Añadimos un filtro por texto, nombre o categoria.
	public void appllyQuickFilter( String text) {
		if (text == null || text.isBlank()) {
			sorter.setRowFilter(null); //sin filtro, muestra todo
			return;
			}
		
		final String needle = text.trim().toLowerCase();
		
		sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
	        @Override
	        public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
	            // OJO: indices de columna según el modelo que definimos:
	            String nombre    = String.valueOf(entry.getValue(1)).toLowerCase(); // "Nombre"
	            String categoria = String.valueOf(entry.getValue(2)).toLowerCase(); // "Categoría"
	            return nombre.contains(needle) || categoria.contains(needle);
	        }
	    });
	}
	
	
}
