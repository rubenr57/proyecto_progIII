package es.deusto.swing.fliphub.main;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import es.deusto.swing.fliphub.db.Persistencia;
<<<<<<< HEAD
import es.deusto.swing.fliphub.gui.DialogLogin;
=======
>>>>>>> branch 'main' of https://github.com/rubenr57/proyecto_progIII.git
import es.deusto.swing.fliphub.gui.JFramePrincipal;

public class Main {
	
	
	public static void main(String[] args) {
		
		
		//Inicializar la BD
		Persistencia.initDatabase();
		
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException | UnsupportedLookAndFeelException e) {
            // Si falla, seguimos con el L&F por defecto
            System.err.println("No se pudo aplicar el Look&Feel del sistema: " + e.getMessage());
        }

        // Lanza la UI
        SwingUtilities.invokeLater(() -> {
        	JFrame dummy = new JFrame();
            dummy.setUndecorated(true);
            dummy.setLocationRelativeTo(null);

            DialogLogin login = new DialogLogin(dummy);
            login.setVisible(true);

            if (login.isOk()) {
                new JFramePrincipal();
            } else {
                System.exit(0);
            }
            
        });
	}
	
	
}
