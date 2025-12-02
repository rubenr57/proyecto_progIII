package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import es.deusto.swing.fliphub.domain.Estado;
import es.deusto.swing.fliphub.domain.Item;

//Dialogo para añadir y editar items del inventario

public class DialogItem extends JDialog {
	
	//Modo del dialogo
	private final boolean editMode; ///true = editar , false = añadir
	
	//Campos del formulario
	private JTextField txtNombre;
	private JTextField txtCategoria;
	private JComboBox<Estado> cbEstado;
	private JFormattedTextField txtPrecioCompra;
	private JTextField txtFechaCompra;
	private JTextField txtUbicacion;
	
	//Resultado
	private Item result;	//si guarda OK: item actualizado/creado, si guarda cancela: null
	private Long editingID; //id existente en modo edicion para no perderlo
	
	
	//Constructor de añadir
	public DialogItem(JFrame parent) {
		super(parent, "Nuevo Item", true); //parent -> la ventana; 
												//Nuevo item -> nombre del dialogo 
												//true -> bloque la ventana hasta que se cierre
		this.editMode = false;
		initUI();
		this.setLocationRelativeTo(parent);//Centra el dialogo respecto a la ventana 
		//Valores por defecto
		cbEstado.setSelectedItem(Estado.EN_STOCK);
		txtFechaCompra.setText(java.time.LocalDate.now()
		        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		txtPrecioCompra.setValue(0.0);
	}
	
	//Constructor de editar
	public DialogItem(JFrame parent, Item itemExisting) {
		super(parent, "Nuevo Item", true);  //parent -> la ventana; 
											//Nuevo item -> nombre del dialogo 
											//true -> bloque la ventana hasta que se cierre
		this.editMode = true;
		this.editingID = itemExisting.getID();
		initUI();
		this.setLocationRelativeTo(parent);
		
		//Pre-rellenar
		txtNombre.setText(itemExisting.getNombre());
		txtCategoria.setText(itemExisting.getCategoria());
		cbEstado.setSelectedItem(itemExisting.getEstado());
		txtFechaCompra.setText(itemExisting.getFechaCompra()
				.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		txtPrecioCompra.setValue(itemExisting.getPrecioCompra());
		txtUbicacion.setText(itemExisting.getUbicacion());	
	}
	
	
	
	//Metodo que configura el dialogo
	private void initUI() {
		this.setLayout(new BorderLayout(8,8));
		this.setSize(400,300);
		this.setMinimumSize(new Dimension(380,280));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Dispose in close hace que solo se cierra esa ventana y no todo el programa
		
		//Creamos el formulario
		//Uso de IAGenerativa
		JPanel form = new JPanel(new GridBagLayout());
		form.setBorder(new EmptyBorder(12,12,12,12));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets= new Insets(6,6,6,6);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		
		int y = 0;
		
		//Nombre
		gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Nombre: "), gbc);
		gbc.gridx = 1;
		txtNombre = new JTextField();
		form.add(txtNombre, gbc);
		y++;
		
		//Categoria
		gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Categoria: "), gbc);
		gbc.gridx = 1;
		txtCategoria = new JTextField();
		form.add(txtCategoria, gbc);
		y++;
				
		//Estado
		gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Estado: "), gbc);
		gbc.gridx = 1;
		cbEstado = new JComboBox<>(Estado.values());
		form.add(cbEstado, gbc);
		y++;
		
		//Precio compra
		gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Precio compra: "), gbc);
		gbc.gridx = 1;
		txtPrecioCompra=  numberField(); //helper abajo
		form.add(txtPrecioCompra, gbc);
		y++;
		
		//Fecha compra
		gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Fecha compra: "), gbc);
		gbc.gridx = 1;
		txtFechaCompra = new JTextField();
		txtFechaCompra.setToolTipText("Ej.: 03/11/2025");
		form.add(txtFechaCompra, gbc);
		y++;
		
		//Ubicacion
		gbc.gridx = 0; gbc.gridy = y; form.add(new JLabel("Ubicacion: "), gbc);
		gbc.gridx = 1;
		txtUbicacion = new JTextField(); 
		form.add(txtUbicacion, gbc);
		y++;
		
		this.add(form, BorderLayout.CENTER);	
		
		//Botones
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnCancelar = new JButton("Cancelar");
		JButton btnguardar = new JButton(editMode ? "Guardar cambios" : "Crear");
		buttons.add(btnguardar);
		buttons.add(btnCancelar);
		this.add(buttons, BorderLayout.SOUTH);
		
		//Listeners
		btnCancelar.addActionListener(e -> {result = null; dispose(); });
		btnguardar.addActionListener(e -> onSave());
	}
	
	//Helpers para el dialogo
	private JFormattedTextField numberField(){
		
		java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
		nf.setGroupingUsed(false);
		JFormattedTextField f = new JFormattedTextField(nf);
		f.setColumns(10);
		f.setHorizontalAlignment(SwingConstants.RIGHT);
		return f;
	}
	
	private void warn(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Dato invalido", JOptionPane.WARNING_MESSAGE);
	}
	
	//Validacion de datos y onSave(): para crear el item o actualizarlo
	private void onSave() {
		
		//Nombre
		String nombre = txtNombre.getText().trim();
		if 	(nombre.isEmpty()) {warn("El nombre es obligatorio"); txtNombre.requestFocus(); return;}
		
		//Categoria
		String categoria = txtCategoria.getText().trim();
		if 	(categoria.isEmpty()) {warn("La categoria es obligatoria"); txtCategoria.requestFocus(); return;}
		
		//Estado
		Estado estado = (Estado) cbEstado.getSelectedItem();
		
		//Precio Compra
		double precioCompra;
		try {
			txtPrecioCompra.commitEdit();
			Number n = (Number) txtPrecioCompra.getValue();
			precioCompra = (n == null) ? 0.0 : n.doubleValue();
			if (precioCompra < 0) throw new IllegalArgumentException();
		} catch (Exception ex) {
			warn("Precio compra debe ser un numero mayor que 0");
			txtPrecioCompra.requestFocus();
			return;
		}
		
		//Fecha compra
		java.time.LocalDate fechaCompra;
		try {
			fechaCompra = java.time.LocalDate.parse(
						txtFechaCompra.getText().trim(),
						java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
			);
			if (fechaCompra.isAfter(java.time.LocalDate.now())) {
				warn("La fecha no puede ser futura");
				txtFechaCompra.requestFocus();
				return;
			}
		} catch (Exception ex) {
			warn("Fecha inválida. Usa formato dd/MM/yyyy (ej.: 03/11/2025).");
			txtFechaCompra.requestFocus();
			return;
		}
		
		//Ubicacion
		String ubicacion = txtUbicacion.getText().trim();
		if (ubicacion.isEmpty()) ubicacion = "-";
		
		//Construccion del item
		long id = editMode ? editingID : System.currentTimeMillis(); // id simple
		result = new Item(id, nombre, categoria, estado, precioCompra, fechaCompra, ubicacion);
		
		dispose();
	}
	
	//Getter del resultado
	public Item getResult() {
		return result;
	}
}	
