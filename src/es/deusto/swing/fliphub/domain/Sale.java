package es.deusto.swing.fliphub.domain;

import java.time.LocalDate;
import java.util.Objects;

//Representa la venta de un item
public class Sale {
	private long id;
	private long itemID;
	private LocalDate fechaVenta;
	private Canal canal;
	private double precioVenta;
	private double comisiones;
	private double envio;
	private double impuestos;
	
	//Constructor
	public Sale(long iD, long itemID, LocalDate fechaVenta, Canal canal, double precioVenta, double comisiones,
			double envio, double impuestos) {
		this.id = iD;
		this.itemID = itemID;
		this.fechaVenta = fechaVenta;
		this.canal = canal;
		this.precioVenta = precioVenta;
		this.comisiones = comisiones;
		this.envio = envio;
		this.impuestos = impuestos;
	}
	
	//Getters
	public long getId() {
		return id;
	}
	public long getItemID() {
			return itemID;
		}
	public LocalDate getFechaVenta() {
		return fechaVenta;
	}
	public Canal getCanal() {
			return canal;
		}
	public double getPrecioVenta() {
		return precioVenta;
	}
	public double getComisiones() {
		return comisiones;
	}
	public double getEnvio() {
		return envio;
	}
	public double getImpuestos() {
		return impuestos;
	}
	
	//Setters
	public void setFechaVenta(LocalDate fechaVenta) {
		this.fechaVenta = fechaVenta;
	}
	public void setCanal(Canal canal) {
		this.canal = canal;
	}
	public void setPrecioVenta(double precioVenta) {
		this.precioVenta = precioVenta;
	}
	public void setComisiones(double comisiones) {
		this.comisiones = comisiones;
	}
	public void setEnvio(double envio) {
		this.envio = envio;
	}
	public void setImpuestos(double impuestos) {
		this.impuestos = impuestos;
	}
	
	
	//Derivados utiles
	public double getBeneficio() {
		return precioVenta - comisiones - envio - impuestos;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sale other = (Sale) obj;
		return id == other.id;
	}

	@Override
	public String toString() {
		return "Venta #" + id + " de item " + itemID + " (" + canal + " )"; 
	}
	
	
	
	
	
	
	
	

	
}
