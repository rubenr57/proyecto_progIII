package es.deusto.swing.fliphub.db;

import java.sql.Statement;

import javax.swing.table.DefaultTableModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Persistencia {
	
	//URL de la BD SQLite
	private static final String DB_URL = "jdbc:sqlite:fliphub.db";
	
	//Conecto y creo las tablas
	
	//conexion
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB_URL);
	}
	
	//creo las tablas
	public static void initDatabase() {
		String sqlItems = """	
					CREATE TABLE IF NOT EXISTS items (
						id INTEGER PRIMARY KEY,
				        nombre TEXT,
				        categoria TEXT,
				        estado TEXT,
				        precio_compra REAL,
				        fecha_compra TEXT,
				        ubicacion TEXT
				    );
				""";
		
		String sqlSales = """ 
				CREATE TABLE IF NOT EXISTS sales (
				    id INTEGER PRIMARY KEY,
                    item_id INTEGER,
                    fecha TEXT,
                    canal TEXT,
                    precio REAL,
                    comisiones REAL,
                    envio REAL,
                    impuestos REAL,
                    beneficio REAL
                );
				""";
		
		try (Connection conn = getConnection(); Statement st = conn.createStatement()){
			
			st.execute(sqlItems);
			st.execute(sqlSales);
			
			System.out.println("[DB] Tablas comprobadas/creadas correctamente.");
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	//guardo desde los modelos de inventario y ventas
	public static void guardarDesdeModelos( DefaultTableModel invModel, DefaultTableModel venModel) {
		
		try (Connection conn = getConnection(); Statement st = conn.createStatement()){
			
			conn.setAutoCommit(false); //para que sea mas rapido
			
			//Vacio las tablas
			st.executeUpdate("DELETE FROM items");
			st.executeUpdate("DELETE FROM sales");
			
			//Inserto los items
			int colId = findCol(invModel, "ID");
			int colNombre = findCol(invModel, "Nombre");
			int colCategoria = findCol(invModel, "Categoría");
			int colEstado = findCol(invModel, "Estado");
			int colPrecioC = findCol(invModel, "Compra €");
			int colFechaC = findCol(invModel, "Fecha Compra");
			int colUbic = findCol(invModel, "Ubicación");
			
			
			for (int r = 0; r < invModel.getRowCount(); r++) {
				long   id   = toLong(invModel.getValueAt(r, colId));
                String nom  = safeStr(invModel.getValueAt(r, colNombre));
                String cat  = safeStr(invModel.getValueAt(r, colCategoria));
                String est  = safeStr(invModel.getValueAt(r, colEstado));
                double pc   = toDouble(invModel.getValueAt(r, colPrecioC));
                String fcomp= safeStr(invModel.getValueAt(r, colFechaC));
                String ubi  = safeStr(invModel.getValueAt(r, colUbic));
                
                String sql = "INSERT INTO items(id, nombre, categoria, estado, precio_compra, fecha_compra, ubicacion) " +
                        "VALUES (" + id + ", '" + nom + "', '" + cat + "', '" + est + "', " + pc + ", '" + fcomp + "', '" + ubi + "');";
				
                st.executeUpdate(sql);
			}
			
			//inserto las ventas
            int sId      = findCol(venModel, "ID");
            int sItemId  = findCol(venModel, "ItemId");
            int sFecha   = findCol(venModel, "Fecha");
            int sCanal   = findCol(venModel, "Canal");
            int sPrecio  = findCol(venModel, "Precio");
            int sCom     = findCol(venModel, "Comisiones");
            int sEnv     = findCol(venModel, "Envío");
            int sImp     = findCol(venModel, "Impuestos");
            int sBenef   = findCol(venModel, "Beneficio");
            

            for (int r = 0; r < venModel.getRowCount(); r++) {
                long   id   = toLong(venModel.getValueAt(r, sId));
                long   item = toLong(venModel.getValueAt(r, sItemId));
                String fec  = safeStr(venModel.getValueAt(r, sFecha));
                String can  = safeStr(venModel.getValueAt(r, sCanal));
                double pr   = toDouble(venModel.getValueAt(r, sPrecio));
                double com  = toDouble(venModel.getValueAt(r, sCom));
                double env  = toDouble(venModel.getValueAt(r, sEnv));
                double imp  = toDouble(venModel.getValueAt(r, sImp));
                double ben  = toDouble(venModel.getValueAt(r, sBenef));

                String sql = "INSERT INTO sales(id, item_id, fecha, canal, precio, comisiones, envio, impuestos, beneficio) " +
                             "VALUES (" + id + ", " + item + ", '" + fec + "', '" + can + "', " + pr + ", " +
                             com + ", " + env + ", " + imp + ", " + ben + ");";
                st.executeUpdate(sql);
            }

            conn.commit();
            System.out.println("[DB] Guardado completo en BD.");
            
		} catch (Exception e) {
			e.printStackTrace();
		}	
	} 
	
	//cargo los modelos para rellenar la BD y rellenar los modelos de inv y ven
	public static void cargarEnModelos( DefaultTableModel invModel, DefaultTableModel venModel) {
		
		try (Connection conn = getConnection();
				Statement st = conn.createStatement()){
			
			//limpio los modelos actuales
			invModel.setRowCount(0);
			venModel.setRowCount(0);
			
			//cargo los items
			String sqlItems = "SELECT id, nombre, categoria, estado, precio_compra, fecha_compra, ubicacion FROM items ORDER BY id;";
			try( java.sql.ResultSet rs = st.executeQuery(sqlItems)){
				while(rs.next()) {
					long id = rs.getLong("id");
					String nom = rs.getString("nombre");
					String cat = rs.getString("categoria");
					String est = rs.getString("estado");
					double pc = rs.getDouble("precio_compra");
					String fcomp = rs.getString("fecha_compra");
	                String ubi   = rs.getString("ubicacion");
	                
	             //de momento beneficio y ROI a 0
	                double beneficio = 0.0;
	                double roi       = 0.0;
	                
	                invModel.addRow(new Object[]{
	                		id,         // "ID"
	                        nom,        // "Nombre"
	                        cat,        // "Categoría"
	                        est,        // "Estado"
	                        pc,         // "Compra €"
	                        fcomp,      // "Fecha compra"
	                        ubi,        // "Ubicación"
	                        beneficio,  // "Beneficio €"
	                        roi         // "ROI %"
	                });
				}
			}
			
			//cargar sales
			String sqlSales = "SELECT id, item_id, fecha, canal, precio, comisiones, envio, impuestos, beneficio FROM sales ORDER BY id;";
	        try (java.sql.ResultSet rs2 = st.executeQuery(sqlSales)) {
	            while (rs2.next()) {
	                long   id    = rs2.getLong("id");
	                long   item  = rs2.getLong("item_id");
	                String fecha = rs2.getString("fecha");
	                String canal = rs2.getString("canal");
	                double pr    = rs2.getDouble("precio");
	                double com   = rs2.getDouble("comisiones");
	                double env   = rs2.getDouble("envio");
	                double imp   = rs2.getDouble("impuestos");
	                double ben   = rs2.getDouble("beneficio");

	                venModel.addRow(new Object[]{
	                        id,    // "ID"
	                        item,  // "ItemId"
	                        fecha, // "Fecha"
	                        canal, // "Canal"
	                        pr,    // "Precio"
	                        com,   // "Comisiones"
	                        env,   // "Envío"
	                        imp,   // "Impuestos"
	                        ben    // "Beneficio"
	                });
	            }
	        }
	        
	        System.out.println("[DB] Datos cargados desde BD en los modelos.");
			
		} catch (SQLException e) {
			System.err.println("[DB] Error al cargar datos en modelos.");
	        e.printStackTrace();
		}
	}
		
		
		//HELPERS 
		//Uso de IAGenerativa

	private static int findCol(DefaultTableModel m, String header) {
	    for (int i = 0; i < m.getColumnCount(); i++) {
	        if (header.equalsIgnoreCase(m.getColumnName(i))) {
	            return i;
	        }
	    }

	    // Si no la encontramos, mostramos las columnas disponibles
	    System.err.println("[DB] No se encontró la columna \"" + header + "\". Columnas disponibles:");
	    for (int i = 0; i < m.getColumnCount(); i++) {
	        System.err.println("   - " + m.getColumnName(i));
	    }
	    return -1;
	}

	    private static String safeStr(Object o) {
	        return (o == null) ? "" : o.toString().replace("'", "''");
	    }

	    private static double toDouble(Object o) {
	        if (o == null) return 0.0;
	        if (o instanceof Number n) return n.doubleValue();
	        try { return Double.parseDouble(o.toString().replace(',', '.')); }
	        catch (Exception e) { return 0.0; }
	    }

	    private static long toLong(Object o) {
	        if (o == null) return 0L;
	        if (o instanceof Number n) return n.longValue();
	        try { return Long.parseLong(o.toString()); }
	        catch (Exception e) { return 0L; }
	    }
	}
	
	 


