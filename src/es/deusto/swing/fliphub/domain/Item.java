package es.deusto.swing.fliphub.domain;

import java.time.LocalDate;
import java.util.Objects;


public class Item {
	
	//ID unico para cada item
	private long ID;
	
	//Datos basicos del negocio
	private String nombre;
	private String categoria;
	private Estado estado;
	private double precioCompra;
	private LocalDate fechaCompra;
	private String ubicacion;
	
	//Constructor para crear el Item
	public Item(long iD, String nombre, String categoria, Estado estado, double precioCompra, LocalDate fechaCompra,
			String ubicacion) {
		super();
		this.ID = iD;  							//Asignamos el identificador
		this.nombre = nombre;					//Nombre visible en la tabla
		this.categoria = categoria;				//Para filtrar u ordenar items
		this.estado = estado;					//En_stock, vendido o reservado
		this.precioCompra = precioCompra;		//Coste de la compra
		this.fechaCompra = fechaCompra;			//Para calcular dias en stock
		this.ubicacion = ubicacion;				//Donde esta guardado el item
	}
	
	//Getters y Setters
	
	//Lectura de los campos, los usara la tabla y algun otro componente
	public String getNombre() {
		return nombre;
	}
	public String getCategoria() {
		return categoria;
	}
	public Estado getEstado() {
		return estado;
	}
	public double getPrecioCompra() {
		return precioCompra;
	}
	public LocalDate getFechaCompra() {
		return fechaCompra;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public long getID() {
		return ID;
	}

	//Modificacion para cuando necesitemos modificar un item
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public void setPrecioCompra(double precioCompra) {
		this.precioCompra = precioCompra;
	}
	public void setFechaCompra(LocalDate fechaCompra) {
		this.fechaCompra = fechaCompra;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(ID);
	}
	
	//Dos items son iguales si tienen el mismo ID
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return ID == other.ID;
	}
	
	@Override
	public String toString() {
		return nombre + "(" + categoria + ")";
	}
	
	
	
	
	
}
