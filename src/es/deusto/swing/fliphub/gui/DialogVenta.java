package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.JobAttributes;
import java.util.concurrent.Flow;

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

import es.deusto.swing.fliphub.domain.Canal;
import es.deusto.swing.fliphub.domain.Sale;

//Dialogo para añadir una venta
public class DialogVenta extends JDialog{
	
	//Campos del formulario 
	private JTextField txtItemId;
	private JTextField txtFecha;
	private JComboBox<Canal> cbCanal;
	private JFormattedTextField txtPrecio;
	private JFormattedTextField txtComisiones;
	private JFormattedTextField txtEnvio;
	private JFormattedTextField txtImpuestos;
	
	//Estado del resultado
	private Sale result;   //Si el usuario guarda correctamente, guardamos aqui la venta
	
	//Constructor para el item pre-rellenado seleccionado de Inventario
	public DialogVenta(JFrame parent, long itemIdPreset) {
		this(parent); //Llama al constructor principal que hace InitUI y pone valores por defecto
		this.txtPrecio.setText(String.valueOf(itemIdPreset)); //Sobreescribe el campo item ID	
		this.txtItemId.setEditable(false); //Para evitar incoherncias
	}
	
	
	//Constructor
	public DialogVenta(JFrame parent) {
		super(parent, "Registrar venta", true); //parent -> la ventana; 
												//Item - Detalles -> nombre del dialogo 
												//true -> bloque la ventana hasta que se cierre
		initUI();
		this.setLocationRelativeTo(parent);
		
		//Valores iniciales
			this.txtItemId.setText("1");
			this.txtFecha.setText(java.time.LocalDate.now()
		       .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			this.cbCanal.setSelectedItem(Canal.WALLAPOP);
			this.txtPrecio.setValue(25.0);
			this.txtComisiones.setValue(2.0);
			this.txtEnvio.setValue(1.9);
			this.txtImpuestos.setValue(0.0);
					
	}
	
	//Metodo que congigura el dialogo
	private void initUI() {
		
		this.setLayout(new BorderLayout(8,8));
		this.setSize(500,360);
		this.setMinimumSize(new Dimension(480,320));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//Panel con GridBag
		JPanel form = new JPanel(new GridBagLayout());
		form.setBorder(new EmptyBorder(12,12,12,12));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(6,6,6,6);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		
		//Fila 0 : Item ID
		gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Item ID: "), gbc);
		gbc.gridx = 1;
		this.txtItemId = new JTextField();
		form.add(this.txtItemId, gbc);
		
		//Fila 1 : Fecha(dd/MM/yyyy)
		gbc.gridx = 0; gbc.gridy = 1; form.add( new JLabel("Fecha: "), gbc);
		gbc.gridx = 1;
		txtFecha = new JTextField();
		txtFecha.setToolTipText("Ej.: 03/11/2025");
		form.add(this.txtFecha, gbc);
		
		//Fila 2 : Canal
		gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Canal: "), gbc);
		gbc.gridx = 1;
		cbCanal = new JComboBox<Canal>(Canal.values());
		form.add(this.cbCanal, gbc);
		
		//Fila 3 : Precio Venta
		gbc.gridx = 0; gbc.gridy = 3; form.add(new JLabel("Precio Venta: "), gbc);
		gbc.gridx = 1;
		txtPrecio = numberField();
		form.add(this.txtPrecio, gbc);
		
		//Fila 4 : Comisiones
		gbc.gridx = 0; gbc.gridy = 4; form.add(new JLabel("Comisiones: "), gbc);
		gbc.gridx = 1;
		txtComisiones = numberField();
		form.add(this.txtComisiones, gbc);
		
		//Fila 5 : Envio
		gbc.gridx = 0; gbc.gridy = 5; form.add( new JLabel("Envio: "), gbc);
		gbc.gridx = 1;
		txtEnvio = numberField();
		form.add(this.txtEnvio, gbc);
		
		//Fila 6 : Impuestos
		gbc.gridx = 0; gbc.gridy = 6; form.add(new JLabel("Impuestos: "), gbc);
		gbc.gridx = 1;
		txtImpuestos = numberField();
		form.add(this.	txtImpuestos, gbc);
		
		this.add(form, BorderLayout.CENTER);
		
		//Barra de botones
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton btnGuardar = new JButton("Guardar");
		JButton btnCancelar = new JButton("Cancelar");
		buttons.add(btnCancelar);
		buttons.add(btnGuardar);
		this.add(buttons, BorderLayout.SOUTH);
		
		//Acciones basicas
		btnCancelar.addActionListener(e -> {
			result = null;
			dispose();
		});
		btnGuardar.addActionListener(e -> 
			onSave()
		);
		
	}
	
	//Utilidad para los numeros
	private JFormattedTextField numberField() {
			java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
			nf.setGroupingUsed(false); //sin separador de miles para evitar problemas
			JFormattedTextField f = new JFormattedTextField(nf);
			f.setColumns(10);
			f.setHorizontalAlignment(SwingConstants.RIGHT);
			return f;
	}
	
	//Comprobamos con el onsave que los campos son validos
	private void onSave() {
		// Leer y validar el item
		long itemId;
		try {
			itemId = Long.parseLong(txtItemId.getText().trim());
			if (itemId <= 0) throw new NumberFormatException("id <= 0");
		} catch (NumberFormatException ex) {
			warn("Item ID debe de ser un numero entero positivo.");
			txtItemId.requestFocus();
			return;
		}
		
		//Leer y validar fecha
		java.time.LocalDate fecha;
		try {
			fecha = java.time.LocalDate.parse(
					txtFecha.getText().trim(),
					java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
					);
		} catch (Exception ex) {
			warn("Fecha invalida. Usa formato dd/MM/yyyy. EJ.: 03/11/2025");
			txtFecha.requestFocus();
			return;
		}
		
		//Canal
		Canal canal = (Canal) cbCanal.getSelectedItem();
		
		//Cantidades numericas
		double precio = getDouble(txtPrecio, "Precio de venta");
		if( precio < 0 ) {txtPrecio.requestFocus(); return; }
		double comisiones = getDouble(txtComisiones, "Comisiones");
		if( comisiones < 0 ) {txtComisiones.requestFocus(); return; }
		double envio = getDouble(txtEnvio, "Envio");
		if( envio < 0 ) {txtEnvio.requestFocus(); return; }
		double impuestos = getDouble(txtImpuestos, "Impuestos");
		if( impuestos < 0 ) {txtImpuestos.requestFocus(); return; }
		
		//Coherencia de precios
		double costes = comisiones + envio + impuestos;
		if (precio < costes) {
			int r = JOptionPane.showConfirmDialog(
					this,
					"Aviso: El precio es menor que el coste, deseas guardar igualmente?",
					"Precio incoherente",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE
					);
			if(r != JOptionPane.YES_OPTION) return;
		}
		
		//Construimos la Sale
		long idVenta = System.currentTimeMillis(); // en BBDD será AUTOINCREMENT
	    result = new Sale(idVenta, itemId, fecha, canal, precio, comisiones, envio, impuestos);	
	    
	    //Cerramos el dialogo
	    dispose();
}
	
	//Helpers para leer numeros y mostrar avisos
	
	private double getDouble(JFormattedTextField f, String nombreCampo) {
	    try {
			f.commitEdit(); //Asegura que toma el valor escrito
			Number n = (Number) f.getValue();
			double v = 	(n == null) ? 0.0 : n.doubleValue();
			if ( v < 0) throw new IllegalArgumentException();
			return v;
		} catch (Exception e) {
			warn( nombreCampo + " debe ser un numero >= 0");
			return -1; //señal de error
		}
	}
	
	private void warn(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Dato invalido", JOptionPane.WARNING_MESSAGE);
		
	}
	
	public Sale getResult() {
		return result;
	}
}
