package es.deusto.swing.fliphub.util;

import java.io.File;

public class Recursividad {
	
	//Cuenta cuantas imagenes hay dentro de una carpeta con una busqueda recursiva
	
	public static int countImagesRecursive(File dir) {
		//si no existe una carpeta
		if ( dir == null || !dir.exists() || !dir.isDirectory()) {
			return 0; 	
		}
		
		int count = 0;
		
		File[] files = dir.listFiles();
		
		if (files == null) {
			return 0;
		}
		
		for (File f : files) {
			if (f.isDirectory()) {
				//paso recursivo, si es carpeta volvemos a llamar al metodo
				count += countImagesRecursive(f);
			} else {
				// si es fichero comprobamos la extension
				String name = f.getName().toLowerCase();
				if (name.endsWith(".png") || name.endsWith(".jpg")
                        || name.endsWith(".jpeg") || name.endsWith(".gif")) {
					count ++;
				}
			}
		}
		return count;
	}

}
