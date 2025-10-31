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

//Este panel sera la vista del inventari en el cardlayout
public class InventarioLayout extends JTable {
	
	//Componentes que iremos rellenando
	private JTable table;	//la tabla
	private DefaultTableModel model;	//modelo de datos de la tabla
	private TableRowSorter<DefaultTableModel> sorter; //para ordenar/filtrar
	
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
				new Object[] {"ID", "Nombre", "Categoría", "Estado", "Compra €", "Fecha compra", "Ubicación"},
				0 //0 filas inciales, ahora las añadiremos
		) {
			@Override public boolean isCellEditable(int row, int column) {
				return false;
		}
		@Override public Class<?> getColumnClass(int columnIndex) {
		        // Esto ayuda a ordenar/representar bien cada columna
		        return switch (columnIndex) {
		          	case 0 -> Long.class;   // ID
		            case 4 -> Double.class; // Compra €
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
		            it.getUbicacion()
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
		
		//Botonera de acciones de la tabla en la parte inferior
		JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT,8,0));
		JButton btnañadir = new JButton("Añadir");
		JButton btneditar = new JButton("Editar");
		JButton btneliminar = new JButton("Eliminar");
		actions.add(btnañadir);
		actions.add(btneditar);
		actions.add(btneliminar);
		
		//De momento solo mostramos mensajes imformativos
		btnañadir.addActionListener(e -> {
			new DialogItem((JFrame) SwingUtilities.getWindowAncestor(this)).setVisible(true);
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
			String estado = (String) model.getValueAt(modelRow, 3);
			double precioCompra = (double) model.getValueAt(modelRow, 4);
			String fechaCompra = (String) model.getValueAt(modelRow, 5);
			String ubicacion = (String) model.getValueAt(modelRow, 6);
			
			//Mensaje con los datos del item 
			JOptionPane.showMessageDialog(
					this,
					"EDITAR ITEM\n\n" +
							"ID: " + id + "\n" +
							"Nombre: " + nombre + "\n" +
							"Categoria: " + categoria + "\n" +
							"Estado: " + estado + "\n" +
							"Precio Compra: " + precioCompra + "\n" +
							"Fecha Compra: " + fechaCompra + "\n" +
							"Ubicacion: " + ubicacion,
					"INFO del Item seleccionado.",
					JOptionPane.INFORMATION_MESSAGE
					);
			
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
					"Estas seguro de que quieres elimnar el item" + 
					model.getValueAt(modelRow, 1) + "?",
					"Confirmar eliminacion",
					JOptionPane.YES_NO_OPTION
					);
			
			if (confirm == JOptionPane.YES_OPTION) {
				model.removeRow(modelRow);
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
