package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

//Dialogo para añadir una venta
public class DialogVenta extends JDialog{
	//Constructor
	public DialogVenta(JFrame parent) {
		super(parent, "Registrar venta", true); //parent -> la ventana; 
												//Item - Detalles -> nombre del dialogo 
												//true -> bloque la ventana hasta que se cierre
		initUI();
		this.setLocationRelativeTo(parent);
	}
	
	//Metodo que congigura el dialogo
	private void initUI() {
		this.setLayout(new BorderLayout(8,8));
		this.setSize(420,260);
		this.setMinimumSize(new Dimension(380,220));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//Creamos un JLabel que servira como marcador temporal
		JLabel lbl = new JLabel("Formulario de venta (pendiente Fase B–2)", SwingConstants.CENTER);
		
		//Margenes interiores
		lbl.setBorder(new EmptyBorder(20,20,20,20));
				
		//Añadimos el label al centro de la ventana de dialogo
		this.add(lbl, BorderLayout.CENTER);
	}
}
