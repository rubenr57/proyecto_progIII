package es.deusto.swing.fliphub.util;

import javax.swing.table.DefaultTableModel;

import es.deusto.swing.fliphub.db.Persistencia;
import es.deusto.swing.fliphub.gui.InventarioLayout;
import es.deusto.swing.fliphub.gui.VentasLayout;

//Esto es un sevicio que hace autoguardados automaricos cada x segundos.
//se puede parar y reanudar

public class AutoSaveService {
	
	//refs para leer los datos
	private final InventarioLayout inventario;
	private final VentasLayout ventas;
	
	//hilo de trabajo
	private Thread worker;
	
	//control del bucle
	private volatile boolean running = false; //seguir ejecutando 
	private boolean paused = false; //esta en pausa o no
	
	//Objeto para wait()/notify()
	private final Object lock = new Object();
	
	//Intervalo entre AutoSaves (ms)
	private final long intervalMs;
	
	public AutoSaveService(InventarioLayout inventario, VentasLayout ventas, long intervalMs) {
		this.inventario = inventario;
		this.ventas = ventas;
		this.intervalMs = intervalMs;
	}
	
	//Arranca el hilo 
	public void start() {
		if (worker != null && worker.isAlive()) {
			//ya esta en marcha
			return;
		}
		
		running = true;
		
		worker = new Thread(() -> loop(), "AutoSaveThread");
		worker.setDaemon(true);
		worker.start();
	}
	
	//bucle principal
	private void loop() {
		while(running) {
			try {
				//pausa si hace falta
				synchronized (lock) {
					while (paused && running) {
						lock.wait(); //espera hasta que alguien llame a resume
					}
				}
				
				if (!running) break; //por si se ha hecho stop durante pause
				
				//hacer el autosave
				doAutosave();
				
				//esperar x milisegundos
				Thread.sleep(intervalMs);
				
			} catch (InterruptedException e) {
				//si se interrupte volvemos a comprobar el running/paused
			}
		}
	}
	
	//Logica de autoguardado
	private void doAutosave() {
		DefaultTableModel mInv = inventario.getModel();
		DefaultTableModel mVen = ventas.getModel();
		
		//llamamos a persistencia para guardar en la BD
		System.out.println("[Autosave] Guardando en BD...");
		Persistencia.guardarDesdeModelos(mInv, mVen);
		System.out.println("[Autosave] OK");
		
	}
	
	//Controles para los botones
	
	//Pone el hilo en pausa se queda en wait()
	public void pauseAutosave() {
		synchronized (lock) {
			paused = true;
		}
		System.out.println("[Autosave] Pausado");
	}
	
	//Reanuda el hilo 
	public void resumeAutosave() {
		synchronized (lock) {
			paused = false;
			lock.notifyAll();
		}
		System.out.println("[Autosave] Reanudado");
	}
	
	//Detiene el hilo
	public void stop() {
		running = false;
		if (worker != null) {
			worker.interrupt();
		}
		synchronized (lock) {
			lock.notifyAll(); //por si estaba en pausa
		}
		System.out.println("[Autosave] Parado");
	}
	
	public boolean isPaused() {
		synchronized (lock) {
			return paused;
		}
	}
}
