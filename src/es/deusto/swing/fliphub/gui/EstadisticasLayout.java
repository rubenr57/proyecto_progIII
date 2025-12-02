package es.deusto.swing.fliphub.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;


public class EstadisticasLayout extends JPanel{
	//Referencias a los otros paneles para leer sus datos
	private final InventarioLayout inventarioRef;
	private final VentasLayout ventasRef;
	
	private final JLabel lblTotalVentas = new JLabel("--");
	private final JLabel lblTotalBenef  = new JLabel("--");
	
	//Labels para los kpis
	private final JLabel lblBeneficioTotal = bigKpiLabel();
	private final JLabel lblTicketMedio = bigKpiLabel();
	private final JLabel lblVendidosPct = bigKpiLabel();
	private final JLabel lblItemsVendidos = bigKpiLabel();
	
	//Campo para el hover de la tabla 
	private int hoverRowMonthly = -1;
	
	//Modelo para la tabla mensual
	private final DefaultTableModel monthModel = new DefaultTableModel(
			new Object[] {
					"Mes", "Ventas (€)", "Comisiones (€)", "Envío (€)", "Impuestos (€)", "Beneficio (€)"}, 
					0
			) {
				@Override 
				public boolean isCellEditable(int r, int c) {
					return false;
				}
				@Override
				//Uso de IAGenerativa
				public Class<?> getColumnClass(int c) {
		            //Ayuda al JTable para alinear y ordenar columnas
		            return switch (c) {
		                case 1,2,3,4,5 -> Double.class;
		                default -> String.class;
		            };
				}
			};
	// JTable (se inicializa en initUI)
	private JTable monthTable;
			
	
	//Colores para el renderizado
	private static final Color KPI_BG_1 = new Color(232, 245, 233); //verde muy claro
	private static final Color KPI_BG_2 = new Color(227, 242, 253); //azul muy claro
	private static final Color KPI_BG_3 = new Color(255, 243, 224); //naranja muy claro
	private static final Color KPI_BG_4 = new Color(243, 229, 245); //lila muy claro

	//Carga iconos con imagenes
	private Icon loadIcon(String path, int w, int h) {
		java.net.URL url = getClass().getResource("/" + path);
		if(url == null) {
			return null;
		}
		Image base = new ImageIcon(url).getImage();
		Image scaled = base.getScaledInstance(w, h, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
	
	//Constructor principal
	public EstadisticasLayout(InventarioLayout inventarioRef, VentasLayout ventasRef) {
		//Recibe los panels de inventario y ventas
		this.inventarioRef = inventarioRef;
		this.ventasRef = ventasRef;
		initUI(); 
	}
	
	//Metodod que es el que crea y coloca los componentes
	private void initUI() {
		this.setLayout(new BorderLayout(10,10));
		this.setBorder(new EmptyBorder(12,12,12,12));
		
		//cargar los iconos
		Icon icoMoney = loadIcon("icons/stats_money.png", 22, 22);
		Icon icoTicket = loadIcon("icons/stats_ticket.png", 22, 22);
		Icon icoPct = loadIcon("icons/stats_pct.png", 22, 22);
		Icon icoItems = loadIcon("icons/stats_items.png", 22, 22);
		
		//El panel de los KPIs
		JPanel kpi = new JPanel(new GridLayout(1,4,10,10));
		kpi.add(kpiCard("Beneficio total", lblBeneficioTotal, KPI_BG_1, icoMoney));
		kpi.add(kpiCard("Ticket medio", lblTicketMedio, KPI_BG_2, icoTicket));
		kpi.add(kpiCard("% Vendidos", lblVendidosPct, KPI_BG_3, icoPct));
		kpi.add(kpiCard("Items vendidos", lblItemsVendidos, KPI_BG_4, icoItems));
		this.add(kpi, BorderLayout.NORTH);
		
		//Tabla mensual con el resumen
		monthTable = new JTable(monthModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int column) {
                Component c = super.prepareRenderer(r, row, column);
                if (!isRowSelected(row)) {
                    if (row == hoverRowMonthly) {
                        c.setBackground(getSelectionBackground());
                        c.setForeground(getSelectionForeground());
                    } else {
                        c.setBackground((row % 2 == 0) ? Color.WHITE : new Color(247, 250, 252));
                        c.setForeground(Color.DARK_GRAY);
                    }
                }
                return c;
            }
        };
		monthTable.setRowHeight(24);
		monthTable.setFillsViewportHeight(true);
		monthTable.setAutoCreateRowSorter(true); //Ordena las filas con un click
		monthTable.setSelectionBackground(new Color(187,222,251));
        monthTable.setSelectionForeground(Color.BLACK);
        
        //Rederizado para la cabecera
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, value, isSelected, hasFocus, row, column);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 13f));
                lbl.setBackground(new Color(47, 79, 79)); 
                lbl.setForeground(Color.WHITE);
                lbl.setOpaque(true);
                return lbl;
            }
        };
        // Aplicar a todas las columnas de la tabla mensual
        for (int i = 0; i < monthTable.getColumnModel().getColumnCount(); i++) {
            monthTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
		
		//Hover para la tabla
		monthTable.addMouseMotionListener(new java.awt.event.MouseMotionAdapter(){
			@Override
			public void mouseMoved(java.awt.event.MouseEvent e) {
				int r = monthTable.rowAtPoint(e.getPoint());
				if(r != hoverRowMonthly) {
					hoverRowMonthly = r;
					monthTable.repaint();
				}
			}
		});
		monthTable.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				hoverRowMonthly = -1;
				monthTable.repaint();
			}
		});
		
		this.add(new JScrollPane(monthTable), BorderLayout.CENTER);
		
		//Renderizado basico
		JTableHeader header = monthTable.getTableHeader();
		header.setOpaque(true);
		header.setBackground(new Color(33,49,63));
		header.setForeground(Color.DARK_GRAY);
		((DefaultTableCellRenderer) header.getDefaultRenderer())
				.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Las celdas conb texto en el centro
		DefaultTableCellRenderer center = new DefaultTableCellRenderer();
		center.setHorizontalAlignment(SwingConstants.CENTER);
		monthTable.setDefaultRenderer(Object.class, center);
		
		//Render para que tengan 2 decimales
		DefaultTableCellRenderer num2 = new DefaultTableCellRenderer() {
			@Override 
			protected void setValue(Object v) {
				if(v instanceof Number n) {
					setText(String.format("%.2f", n.doubleValue()));
				}else {
					super.setValue(v);
					setHorizontalAlignment(SwingConstants.CENTER);
				}
			}
		};
		//Lo aplico a todas las columnas
		for (int c = 1 ; c <= 5; c++) {
			monthTable.getColumnModel().getColumn(c).setCellRenderer(num2);	
		}
		
		//Barra inferior con los totales
		JPanel footer = new JPanel(new GridLayout(1,2,10,0));
		footer.setBorder(new EmptyBorder(8,0,0,0));

		JLabel tv = new JLabel(" Total Ventas: ", SwingConstants.RIGHT);
		JLabel tb = new JLabel(" Total Beneficio: ", SwingConstants.RIGHT);
		tv.setForeground(new Color(80,90,100));
		tb.setForeground(new Color(80,90,100));

		JPanel p1 = new JPanel(new BorderLayout());
		p1.add(tv, BorderLayout.WEST);
		p1.add(lblTotalVentas, BorderLayout.CENTER);

		JPanel p2 = new JPanel(new BorderLayout());
		p2.add(tb, BorderLayout.WEST);
		p2.add(lblTotalBenef, BorderLayout.CENTER);

		footer.add(p1);
		footer.add(p2);

		add(footer, BorderLayout.SOUTH);
		
		//ToolTipTexts
		lblBeneficioTotal.setToolTipText("Suma de beneficios de todas las ventas.");
		lblTicketMedio.setToolTipText("Media del precio de venta.");
		lblVendidosPct.setToolTipText("Porcentaje de ítems con estado 'VENDIDO'.");
		lblItemsVendidos.setToolTipText("Número total de ítems vendidos.");
	}
	
	//JLabel para mostrar los valores del KPI
	private static JLabel bigKpiLabel() {
		JLabel l = new JLabel("--", SwingConstants.CENTER);
		l.setFont(l.getFont().deriveFont(Font.BOLD, 20f));
		return l;
	}
	
	//Panel conn titulo y valor KPI
	//Añade tambien un hover y colores y iconos
	private JPanel kpiCard(String title, JLabel value, Color bg, Icon icon) {
		JPanel p = new JPanel(new BorderLayout());
		p.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(210, 215, 220)),
				new EmptyBorder(10,10,10,10)
				));
		p.setBackground(bg);
		
		//Titulo con el icono
		JLabel t = new JLabel(title, SwingConstants.CENTER);
		t.setForeground(new Color(60, 70, 80));
		t.setBorder(new EmptyBorder(0,0,6,0));
		t.setIcon(icon);
		t.setHorizontalAlignment(SwingConstants.CENTER); //icono a la izq y el texto a la drch
		t.setIconTextGap(8);
		
		//el valkor en el centro
		value.setFont(value.getFont().deriveFont(Font.BOLD, 20f));
		value.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Efecto hover
		p.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				p.setBackground(bg.darker());
			}
			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				p.setBackground(bg);
			}
		});
		
		p.add(t, BorderLayout.NORTH);
		p.add(value, BorderLayout.CENTER);
		return p;
	}
	
	//Metodo que se llma cuando se actualizan los datos de inventario o ventas
	//Actualiza los KPIs y la tabla mensual
	public void refresh() {
		calcKPIs();
		fillMonthlyTable();
	}
	
	//Calculo de los KPIs
	//Uso de IAGenerativa
	private void calcKPIs() {
	    DefaultTableModel mInv = inventarioRef.getModel();
	    DefaultTableModel mVen = ventasRef.getModel();

	    // Buscar índices por nombre en el modelo de Ventas
	    int cPrecio     = findCol(mVen, "Precio");
	    int cComisiones = findCol(mVen, "Comisiones");
	    int cEnvio      = findCol(mVen, "Envío");
	    int cImpuestos  = findCol(mVen, "Impuestos");
	    int cBeneficio  = findCol(mVen, "Beneficio");

	    double sumBeneficio = 0.0, sumPrecioVenta = 0.0;
	    int nVentas = mVen.getRowCount();

	    for (int r = 0; r < nVentas; r++) {
	        double pv  = toD(mVen.getValueAt(r, cPrecio));
	        double com = toD(mVen.getValueAt(r, cComisiones));
	        double env = toD(mVen.getValueAt(r, cEnvio));
	        double imp = toD(mVen.getValueAt(r, cImpuestos));
	        double ben = (cBeneficio >= 0) ? toD(mVen.getValueAt(r, cBeneficio)) : Double.NaN;

	        if (Double.isNaN(ben)) ben = pv - (com + env + imp);

	        sumBeneficio   += ben;
	        sumPrecioVenta += pv;
	    }

	    double ticketMedio = (nVentas > 0) ? (sumPrecioVenta / nVentas) : 0.0;

	    // % vendidos e items vendidos: se leen del Inventario (Estado)
	    int totalItems = mInv.getRowCount();
	    int vendidos   = 0;
	    int cEstadoInv = findCol(mInv, "Estado");
	    for (int r = 0; r < totalItems; r++) {
	        String estado = String.valueOf(mInv.getValueAt(r, cEstadoInv));
	        if ("VENDIDO".equalsIgnoreCase(estado)) vendidos++;
	    }
	    double pctVendidos = (totalItems > 0) ? (vendidos * 100.0 / totalItems) : 0.0;

	    // Pintamos KPIs
	    lblBeneficioTotal.setText(String.format("%.2f €", sumBeneficio));
	    lblTicketMedio.setText(String.format("%.2f €", ticketMedio));
	    lblVendidosPct.setText(String.format("%.1f %%", pctVendidos));
	    lblItemsVendidos.setText(String.valueOf(vendidos));
	}

	
	//LLenar tabla mensual
	private void fillMonthlyTable() {
	    monthModel.setRowCount(0);

	    DefaultTableModel mVen = ventasRef.getModel();

	    int cFecha      = findCol(mVen, "Fecha");
	    int cPrecio     = findCol(mVen, "Precio");
	    int cComisiones = findCol(mVen, "Comisiones");
	    int cEnvio      = findCol(mVen, "Envío");
	    int cImpuestos  = findCol(mVen, "Impuestos");
	    int cBeneficio  = findCol(mVen, "Beneficio");

	    Map<YearMonth, double[]> agg = new LinkedHashMap<>();
	    DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	    for (int r = 0; r < mVen.getRowCount(); r++) {
	        String fechaStr = String.valueOf(mVen.getValueAt(r, cFecha));
	        LocalDate fecha;
	        try { fecha = LocalDate.parse(fechaStr, DF); } catch (Exception ex) { continue; }
	        YearMonth ym = YearMonth.of(fecha.getYear(), fecha.getMonth());

	        double pv  = toD(mVen.getValueAt(r, cPrecio));
	        double com = toD(mVen.getValueAt(r, cComisiones));
	        double env = toD(mVen.getValueAt(r, cEnvio));
	        double imp = toD(mVen.getValueAt(r, cImpuestos));
	        double ben = (cBeneficio >= 0) ? toD(mVen.getValueAt(r, cBeneficio)) : Double.NaN;
	        if (Double.isNaN(ben)) ben = pv - (com + env + imp);

	        agg.computeIfAbsent(ym, k -> new double[5]);
	        double[] a = agg.get(ym);
	        a[0] += pv;  // Ventas
	        a[1] += com; // Comisiones
	        a[2] += env; // Envío
	        a[3] += imp; // Impuestos
	        a[4] += ben; // Beneficio
	    }

	    for (Map.Entry<YearMonth, double[]> e : agg.entrySet()) {
	        YearMonth ym = e.getKey();
	        double[] a   = e.getValue();
	        String mes = ym.getMonthValue() + "/" + ym.getYear();
	        monthModel.addRow(new Object[]{ mes, a[0], a[1], a[2], a[3], a[4] });
	    }
	    
	    double totVentas = 0.0, totBenef = 0.0;
	    for (int r = 0; r < monthModel.getRowCount(); r++) {
	        totVentas += toD(monthModel.getValueAt(r, 1)); // "Ventas (€)"
	        totBenef  += toD(monthModel.getValueAt(r, 5)); // "Beneficio (€)"
	    }
	    lblTotalVentas.setText(String.format("%.2f €", totVentas));
	    lblTotalBenef.setText(String.format("%.2f €", totBenef));
	}

	
	//Convierte los objetos a double y sino devuelve NaN
	private static double toD(Object v) {
        if (v == null) return Double.NaN;
        if (v instanceof Number n) return n.doubleValue();
        try { 
        	return Double.parseDouble(String.valueOf(v).replace(',', '.')); 
        	}
        catch (Exception e) { 
        	return Double.NaN; 
        	}
    }
	
	//indice de la columna con ese nombre
	private static int findCol(DefaultTableModel m, String header) {
	    for (int i = 0; i < m.getColumnCount(); i++) {
	        if (header.equalsIgnoreCase(m.getColumnName(i))) return i;
	    }
	    return -1;
	}
	
}
