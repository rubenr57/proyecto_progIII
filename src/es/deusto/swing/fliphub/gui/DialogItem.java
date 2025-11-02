package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

//Dialogo para añadir y editar items del inventario

public class DialogItem extends JDialog {
	
	//Constructor del dialogo
	public DialogItem(JFrame parent) {
		super(parent, "Item - Detalles", true); //parent -> la ventana; 
												//Item - Detalles -> nombre del dialogo 
												//true -> bloque la ventana hasta que se cierre
		initUI();
		this.setLocationRelativeTo(parent);//Centra el dialogo respecto a la ventana 
	}
	
	//Metodo que configura el dialogo
	private void initUI() {
		this.setLayout(new BorderLayout(8,8));
		this.setSize(400,300);
		this.setMinimumSize(new Dimension(380,280));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE); //Disponse in close hace que solo se cierra esa ventana y no todo el programa
		
		//Creamos un JLabel que servira como marcador temporal
		JLabel lblplaceHolder = new JLabel(
				"Formulario de Item(se implementara en la FASE B)",
				SwingConstants.CENTER
				);
		
		//Margenes interiores
		lblplaceHolder.setBorder(new EmptyBorder(20,20,20,20));
		
		//Añadimos el label al centro de la ventana de dialogo
		this.add(lblplaceHolder, BorderLayout.CENTER);
	}
}
