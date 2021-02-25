package vista;

import controlador.ClaseM;
import controlador.EditorCeldas;
import controlador.Fraccion;
import controlador.GenerarPDF;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

/**
 *
 * @author FORTY
 */
public class Framehd extends javax.swing.JFrame {

    int numRestricciones, numVariables, columSR, columnSRcompletas;
    int numHolgura, numArtificial;
    int numIteraciones;
    int numFotos;
    File directorio;
    //Creamos un combo box sin modelo
    JComboBox< Object> cmbSigno = new JComboBox<>();

    public Framehd() {
        asignarLookandfeel();
        initComponents();
        editarComponentes();
    }

    private void asignarLookandfeel() {
        try {

            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");

        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FrameBigM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(FrameBigM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(FrameBigM.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(FrameBigM.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void editarComponentes() {
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setTitle("Método Simplex & Método Big M");
        panelRestrFunc.setVisible(false);
        panelTablaIteraccion.setVisible(false);
        Object[] objetivos = {"Minimizar", "Maximizar"};
        DefaultComboBoxModel modeloCombo = new DefaultComboBoxModel(objetivos);
        cmbObjectivo.setModel(modeloCombo);
        Object[] metodos = {"Método Simplex", "Método Big M"};
        DefaultComboBoxModel modelcmbMeth = new DefaultComboBoxModel(metodos);
        cmbMetodo.setModel(modelcmbMeth);
        cmbObjectivo.setVisible(false);
        lblFunObj.setVisible(false);
        btnGenerarPDF.setVisible(false);

    }

    public void crearCarpeta() {
        String rutaCarpeta = "capturasSimplexBigM" + horaActual();
        File home = FileSystemView.getFileSystemView().getHomeDirectory();
        String absPath = home.getAbsolutePath();
        directorio = new File(absPath + "/" + rutaCarpeta);
        directorio.mkdir();
    }

    private String horaActual() {
        LocalDateTime locaDate = LocalDateTime.now();
        String hours = String.valueOf(locaDate.getHour());
        String minutes = String.valueOf(locaDate.getMinute());
        String seconds = String.valueOf(locaDate.getSecond());
        return hours + minutes + seconds;
    }

    private void guardarCaptura(Component component) {
        BufferedImage imagen = new BufferedImage(
                component.getWidth(),
                component.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        // call the Component's paint method, using 
        // the Graphics object of the image. 
        component.paint(imagen.getGraphics()); // alternately use .printAll(..) 
        //guardar captura 
        try {
            // write the image as a PNG 

            ImageIO.write(imagen, "png", new File(directorio.getPath() + "/" + numFotos + ".png"));
            //numFotos++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //funcion que crea tabla para introducir la funcion objetivo
    private void llenarTablaVar() {
        DefaultTableModel modelo = new DefaultTableModel();
        tblFuncion.setModel(modelo);
        for (int i = 1; i <= numVariables; i++) {//ciclo que añade columnas
            modelo.addColumn("X" + i);
        }
        Object obj[] = null;
        modelo.insertRow(0, obj);//tabla con una unica fila
    }

    //funcion que crea tabla para introducir las restricciones
    private void llenarTablaRestr() {
        DefaultTableModel modeloTablaRestricciones = new DefaultTableModel();
        tblSR.setModel(modeloTablaRestricciones);
        //Creamos un modelo de combobox y le añadimos 3 elementos
        DefaultComboBoxModel modelo = new DefaultComboBoxModel();
        modelo.addElement("<=");
        if (cmbMetodo.getSelectedIndex() == 1) { //Metodo Big M
            modelo.addElement(">=");
            modelo.addElement("=");
        }
        //Asignamos el modelo al combobox
        cmbSigno.setModel(modelo);

        for (int i = 1; i <= numVariables; i++) { //ciclo que añade columnas
            modeloTablaRestricciones.addColumn("X" + i);
        }
        modeloTablaRestricciones.addColumn(""); //columna <=
        modeloTablaRestricciones.addColumn("Bi"); //columna Bi
        //Ahora vamos a recoger una columna que será donde insertemos el combobox
        TableColumn columna = tblSR.getColumnModel().getColumn(numVariables);
        //Creamos un nuevo editor de celda. Tambien puede insertarse checkboxs y textfields
        TableCellEditor editor = new DefaultCellEditor(cmbSigno);
        //Le asignamos a la columna el editor creado
        columna.setCellEditor(editor);
        Object[] obj = new Object[numVariables + 2];
        for (int i = 0; i < numRestricciones; i++) { //ciclo que añade filas para llenar
            for (int j = 0; j < obj.length; j++) {
                if (j == numVariables) {
                    obj[j] = "<=";
                } else {
                    obj[j] = "";
                }
            }
            modeloTablaRestricciones.insertRow(i, obj);
        }
    }

    private String devolverValorSR(String stringDecimal) {
        try {
            Fraccion frac = new Fraccion();
            double decimal = 0;
            if (frac.posicionBarra(stringDecimal) == -1) { //no es fraccion
                decimal = Double.parseDouble(stringDecimal); //
            } else { //si es fraccion
                decimal = 1;
            }
            if (decimal % (int) decimal == 0) { //no es decimal
                return stringDecimal; //se devuelve fraccion o entero
            } else { //es decimal
                return frac.toFraccion(decimal).toString();//se devuelve fraccion simplificada
            }
        } catch (NumberFormatException nfe) {
            //JOptionPane.showMessageDialog(this, "Dato vacio");
            return "Dato vacio";

        }

    }

    //funcion para guardar en matriz las inecuaciones
    private Object[][] sistemaRestricciones() {
        boolean sinExcepcion = false;
        columSR = 0;
        columnSRcompletas = 0;
        numHolgura = 0;
        numArtificial = 0;
        columSR = numVariables; //numero de columnas que ocupan las inecuaciones

//ciclo que aumenta columnas articiales
        for (int i = 0; i < numRestricciones; i++) {
            if (String.valueOf(tblSR.getValueAt(i, numVariables)).equals(">=")) {
                numHolgura++;
                numArtificial++;
            } else if (String.valueOf(tblSR.getValueAt(i, numVariables)).equals("=")) {
                numArtificial++;
            } else {
                numHolgura++;
            }
        }
        columSR += numHolgura;
        columnSRcompletas = columSR + numArtificial + 1;
        Object restricciones[][] = new Object[numRestricciones][columnSRcompletas];

//Ciclo para agregar los coeficientes
        String stringDecimal1 = "";
        for (int iResFila = 0; iResFila < numRestricciones; iResFila++) { //ciclo filas
            if (sinExcepcion) {
                System.out.println("Despues de aqui");
                break;

            }
            for (int iResCol = 0; iResCol < numVariables; iResCol++) { //ciclo columnas
                stringDecimal1 = String.valueOf(tblSR.getValueAt(iResFila, iResCol));
                restricciones[iResFila][iResCol] = devolverValorSR(stringDecimal1);
                if (restricciones[iResFila][iResCol] == "Dato vacio") {
                    sinExcepcion = true;
                    System.out.println("SALEEEE DE AQUI");
                    break;
                }

            }
        }
        if (!sinExcepcion) { //No se detecto excepcion
            //ciclo para asignar variables de holgura
            int numIguales = 0;
            for (int iResFila = 0; iResFila < numRestricciones; iResFila++) { //ciclo filas

                for (int iResCol = 0; iResCol < numHolgura; iResCol++) { //ciclo columnas

                    if (iResFila == (iResCol + numIguales)) {
                        if (String.valueOf(tblSR.getValueAt(iResFila, numVariables)) == ">=") {
                            restricciones[iResFila][iResCol + numVariables] = -1;
                        } else if (String.valueOf(tblSR.getValueAt(iResFila, numVariables)) == "=") {
                            restricciones[iResFila][iResCol + numVariables] = 0;
                        } else {
                            restricciones[iResFila][iResCol + numVariables] = 1;

                        }
                    } else {
                        restricciones[iResFila][iResCol + numVariables] = 0; //calculo para ingresar 0

                    }
                }

                if (String.valueOf(tblSR.getValueAt(iResFila, numVariables)) == "=") {
                    numIguales++;
                }

            }

            //Agregar variables artificiales
            int saltoFila = 0;
            for (int iResFila = 0; iResFila < numRestricciones; iResFila++) { //ciclo filas
                for (int iResCol = 0; iResCol < numArtificial; iResCol++) { //ciclo columnas
                    if (iResFila == (iResCol + saltoFila)) {
                        if (String.valueOf(tblSR.getValueAt(iResFila, numVariables)) == "<=") {
                            restricciones[iResFila][iResCol + columSR] = 0;
                        } else {
                            restricciones[iResFila][iResCol + columSR] = 1;
                        }
                    } else {
                        restricciones[iResFila][iResCol + columSR] = 0; //calculo para ingresar 0
                    }
                }
                if (String.valueOf(tblSR.getValueAt(iResFila, numVariables)) == "<=") {
                    saltoFila++;
                }
            }

            //Agregar Bi
            String stringDecimal2 = "";
            for (int bi = 0; bi < numRestricciones; bi++) {
                stringDecimal2 = String.valueOf(tblSR.getValueAt(bi, (tblSR.getColumnCount() - 1)));
                restricciones[bi][columnSRcompletas - 1] = devolverValorSR(stringDecimal2);
            }
            return restricciones;
        } else {
            return null;
        }

    }
//funcion para llenar la tabla con la matriz inicial

    private void matrizInicial() {
        try {
            //Guarda las inecuaciones en el arreglo matriz inicial
            Object[][] inecuaciones = sistemaRestricciones();
            if (inecuaciones == null) {
                throw null;
            }
            //Definimos tamaño columnas y filas
            int numColumnas = columnSRcompletas + 2;
            int numFilas = numRestricciones + 4;
            System.out.println("Que paso");
            //matriz inicial
            Object matriz[][] = new Object[numFilas][numColumnas];
            //espacios en blanco y datos que no cambian
            matriz[0][0] = " ";
            matriz[0][1] = "CJ";
            matriz[0][numColumnas - 1] = " ";
            matriz[1][0] = "CB";
            matriz[1][1] = "XB";
            matriz[1][numColumnas - 1] = "BI";
            matriz[numFilas - 2][0] = " ";
            matriz[numFilas - 2][1] = "ZJ";
            matriz[numFilas - 1][0] = " ";
            matriz[numFilas - 1][1] = "ZJ-CJ";
            matriz[numFilas - 1][numColumnas - 1] = " ";

            // Xb, Cb, Cj
            int numVar = 0;
            int numArt = 0;
            String funcion = "";
            String stringDecimal = "";
            for (int j = 2; j < (numColumnas - 1); j++) {
                //Cj, Xb
                if (numVar < numVariables) {
                    stringDecimal = String.valueOf(tblFuncion.getValueAt(0, numVar));
                    System.out.println("STRING DECIMAL: " + stringDecimal);
                    if (stringDecimal == "null" || stringDecimal.isEmpty()) {
                        System.out.println("Lanza null");
                        throw null;
                    }
                    matriz[0][j] = devolverValorSR(stringDecimal);
                    funcion += " " + String.valueOf(matriz[0][j]);
                    matriz[1][j] = "X" + (numVar + 1);
                    funcion += String.valueOf(matriz[1][j] + " +");
                    numVar++;
                } else if (numVar < columSR) {
                    matriz[0][j] = "0";
                    matriz[1][j] = "X" + (numVar + 1);
                    numVar++;
                } else {
                    if (cmbMetodo.getSelectedIndex() == 0) { //Metodo Simplex
                        matriz[0][j] = "1";

                    } else {                                  //Metodo Big M
                        if (cmbObjectivo.getSelectedIndex() == 0) {//Minimizar
                            matriz[0][j] = "M";

                        } else {
                            matriz[0][j] = "-M";
                        }
                    }

                    matriz[1][j] = "A" + (numArt + 1);
                    numArt++;
                }
            }

            //Ya no se lanza excepcion se pueden mostrar la tablaU y las etiquetas
            mostrarLabels(true);

            String minmax = "";
            if (cmbMetodo.getSelectedIndex() == 0 || cmbObjectivo.getSelectedIndex() == 1) {
                minmax = "Max Z = ";
            } else {

                minmax = "Min Z = ";

            }

            lblFuncionObjetivo.setText("  " + minmax + funcion.substring(0, funcion.length() - 1));

            //Guardamos inecuaciones en matriz inicial
            // Fraccion fracUno = new Fraccion();
            for (int q = 0; q < numRestricciones; q++) {//filas
                for (int w = 0; w < columnSRcompletas; w++) { //columnas
                    matriz[q + 2][w + 2] = inecuaciones[q][w];

                    if ("1".equals(String.valueOf(inecuaciones[q][w])) && w >= numVariables) {
                        matriz[q + 2][0] = matriz[0][w + 2]; //Cb
                        matriz[q + 2][1] = matriz[1][w + 2]; //Xb
                    }
                }
            }

            //obtener fila zj
            String stringZj = "";
            String acumulaZj = "";
            for (int col = 0; col < columnSRcompletas; col++) {
                acumulaZj = "";
                for (int fila = 0; fila < numRestricciones; fila++) {
                    stringZj = guardarZj(String.valueOf(matriz[fila + 2][0]), String.valueOf(matriz[fila + 2][col + 2]));
                    acumulaZj += stringZj + ";";
                }
                matriz[numFilas - 2][col + 2] = devolverZj(acumulaZj);
            }

            //obtener fila cj-zj
            for (int col = 2; col < numColumnas - 1; col++) {
                matriz[numFilas - 1][col] = devolverZjCj(String.valueOf(matriz[0][col]), String.valueOf(matriz[numFilas - 2][col]));
            }
            //mostrar matriz en la tabla
            DefaultTableModel modeloInicial = new DefaultTableModel();
            tablaU.setModel(modeloInicial);
            for (int i = 0; i < numColumnas; i++) { //Agregar columnas
                modeloInicial.addColumn("");
            }
            Object obj[] = new Object[numColumnas];
            for (int i = 0; i < numFilas; i++) {
                for (int k = 0; k < numColumnas; k++) {
                    obj[k] = matriz[i][k];
                }
                modeloInicial.addRow(obj);
            }

            System.out.println("Ya llego aca");
            //pintamos el pivote
            pintarPivote();
            //mostrar iteracion 0
            numIteraciones = 0;
            lblNumItera.setText("" + numIteraciones + "");

        } catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(null, "Las tablas no fueron llenadas correctamente");
            mostrarLabels(false);
        }

    }

    private String devolverZj(String acumulaZj) {
        String arregloZj[] = acumulaZj.split(";");
        String zjfinal = "";
        boolean llevaM = false;
        boolean llevaFra = false;
        ClaseM claseM = new ClaseM();
        ClaseM claseMAcum = new ClaseM();
        claseMAcum.setCoeficiente(new Fraccion(0, 1));
        Fraccion FracAcumuladora = new Fraccion(0, 1);
        Fraccion frac = new Fraccion();
        int i = 0;

        for (int k = 0; k < arregloZj.length; k++) {
            if (claseM.esM(arregloZj[k])) {
                claseM = claseM.obtenerM(arregloZj[k]);
                claseMAcum = claseMAcum.sumarM(claseM);
                llevaM = true;
            } else if (!"0".equals(arregloZj[k])) {
                FracAcumuladora = FracAcumuladora.sumar(frac.deTablaFraccion(arregloZj[k]));
                llevaFra = true;
            }
        }
        if (!llevaM) {
            zjfinal = FracAcumuladora.toString();
        } else if (llevaFra && FracAcumuladora.toString().charAt(0) == '-') {
            zjfinal = claseMAcum.toString() + FracAcumuladora.toString();
        } else if (llevaFra && FracAcumuladora.toString().charAt(0) != '-') {
            zjfinal = claseMAcum.toString() + "+" + FracAcumuladora.toString();
        } else {
            zjfinal = claseMAcum.toString();
        }

        return zjfinal;
    }

    private String guardarZj(String cb, String numero) {
        String multiCb = "";
        Fraccion fracNum = new Fraccion();
        Fraccion fracCB = new Fraccion();
        if (("0".equals(numero)) || ("0".equals(cb))) {
            multiCb = "0";
        } else if ("M".equals(cb)) {
            multiCb = numero + "M";
        } else if ("-M".equals(cb)) {
            fracNum = fracNum.deTablaFraccion(numero);
            fracCB.setNumerador(-1);
            fracCB.setDenominador(1);
            multiCb = fracCB.multiplicar(fracNum).toString() + "M";
        } else {
            fracNum = fracNum.deTablaFraccion(numero);
            fracCB = fracCB.deTablaFraccion(cb);
            multiCb = fracCB.multiplicar(fracNum).toString();
        }
        return multiCb;
    }

    private String devolverZjCj(String cj, String zj) {
        String zjcj = "";
        String newCj = "";
        String corteM = "";
        String corteFrac = "";
        int posM = 0;
        Fraccion fraccionZj = new Fraccion();
        Fraccion fraccionCj = new Fraccion();
        Fraccion fraCorte = new Fraccion();
        ClaseM zjM = new ClaseM();
        ClaseM cjM = new ClaseM();

        if (cj == "0" & zj == "0") {  // 0 y 0
            zjcj = "0";

        } else if ((zjM.esM(zj)) && (cjM.esM(cj))) { //ambas son M

            if ("M".equals(cj)) {
                cj = "1M";
            }
            if ("-M".equals(cj)) {
                cj = "-1M";
            }
            posM = zj.indexOf('M');
            //separar fraccion de M
            if (posM == zj.length() - 1) { //no tiene nada despues de M
                corteFrac = "";
            } else {                       //si tiene despues de M
                corteFrac = zj.substring(posM + 1, zj.length());
                if (corteFrac.charAt(0) == '-') {
                    corteFrac = "+" + corteFrac.substring(1, corteFrac.length());
                } else {
                    corteFrac = "-" + corteFrac.substring(1, corteFrac.length());
                }

            }
            corteM = zj.substring(0, posM + 1);
            //cambiar signo al corte M
            if (corteM.charAt(0) == '-') {
                corteM = corteM.substring(1, corteM.length());
            } else {
                corteM = "-" + corteM;
            }

            cjM = cjM.obtenerM(cj);
            zjM = zjM.obtenerM(corteM);
            cjM = cjM.sumarM(zjM);
            zjcj = cjM.toString() + corteFrac;

        } else if (zjM.esM(zj)) {   //cj es numero zj = tiene M
            posM = zj.indexOf('M');
            //separar M de fraccion
            if (posM == zj.length() - 1) { //no tiene nada despues de M
                fraCorte.setNumerador(0);
                fraCorte.setDenominador(1);
            } else {                       //si tiene despues de M
                corteFrac = zj.substring(posM + 1, zj.length());
                //cambiar sigNo a fraccion
                if (corteFrac.charAt(0) == '-') {
                    corteFrac = corteFrac.substring(1, corteFrac.length());
                } else {
                    corteFrac = "-" + corteFrac.substring(1, corteFrac.length());
                }
                fraCorte = fraCorte.deTablaFraccion(corteFrac);
            }
            corteM = zj.substring(0, posM + 1);

            //cambiar signo al corte M
            if (corteM.charAt(0) == '-') {
                corteM = corteM.substring(1, corteM.length());
            } else {
                corteM = "-" + corteM;
            }
            //hacer calculo para fraccion
            if ("0".equals(cj)) {
                fraccionCj.setNumerador(0);
                fraccionCj.setDenominador(1);
                fraccionCj = fraccionCj.deTablaFraccion(cj);
            } else {
                fraccionCj = fraccionCj.deTablaFraccion(cj);
            }

            fraccionCj = fraccionCj.sumar(fraCorte);

            if (fraccionCj.toString().charAt(0) != '-') {
                newCj = "+" + fraccionCj.toString();
            } else {
                newCj = fraccionCj.toString();
            }
            if (newCj.charAt(newCj.length() - 1) == '0') {
                newCj = "";
            }
            zjcj = corteM + newCj;

        } else if (cjM.esM(cj)) { //cj tiene M  zj es fraccion
            if ("M".equals(cj)) {
                corteM = "1M";

            }
            if ("-M".equals(cj)) {
                corteM = "-1M";
            }

            if (zj.equals("0")) {
                zjcj = corteM;

            } else {
                if (zj.charAt(0) == '-') {
                    zj = "+" + zj.substring(1, zj.length());
                } else {
                    zj = "-" + zj;
                }

                zjcj = corteM + zj;

            }

        } else {
            fraccionCj = fraccionCj.deTablaFraccion(cj);
            if (zj.charAt(0) == '-') {
                zj = zj.substring(1, zj.length());
            } else {
                zj = "-" + zj;
            }
            fraccionZj = fraccionZj.deTablaFraccion(zj);
            zjcj = fraccionCj.sumar(fraccionZj).toString();
        }
        return zjcj;
    }

    //funcion pinta la celda del numero pivote
    private void pintarPivote() {
        int fila = variableSalida();//ubicacion de la variable de salida
        int col = variableEntrada();//ubicacion de la variable de entrada
        TableColumn columna = tablaU.getColumnModel().getColumn(col);// selecciono la columna que me interesa de la tabla
        EditorCeldas TableCellRenderer = new EditorCeldas() {
        };
        TableCellRenderer.setColumns(col); //se le da por parametro la columna que se quiere modificar
        TableCellRenderer.setRow(fila);//se le da por parametro la fila que se quiere modificar
        columna.setCellRenderer(TableCellRenderer); // le aplico la edicion
    }

    //funcion que devuelve la ubicacion de la variable de salida
    private int variableSalida() {
        Fraccion fraccionAij = new Fraccion();
        Fraccion fraccionBi = new Fraccion();
        double vSal = 0d;
        double siguiente = 0d;
//        int numColumnas = numVariables + numRestricciones + 3;
//        int numFilas = numRestricciones + 4;
        int numColumnas = columnSRcompletas + 2;
        int numFilas = numRestricciones + 4;
        int menor = 2; //primera ubicacion
        int ve = variableEntrada(); //ubicacion de la variable de entrada
        boolean entra = true; //booleana para ejecutarse una sola vez
        double aij = 0;
        double bi = 0;
        String stringAij = "";
        String stringBi = "";
        for (int i = 2; i < numFilas - 2; i++) { //ciclo recorre las filas de las inecuaciones

            stringAij = String.valueOf(tablaU.getValueAt(i, ve));
            if (fraccionAij.posicionBarra(stringAij) == -1) {
                aij = Double.parseDouble(stringAij);
            } else {
                fraccionAij = fraccionAij.deTablaFraccion(stringAij);
                aij = fraccionAij.toDecimal();
            }

            stringBi = String.valueOf(tablaU.getValueAt(i, numColumnas - 1));
            if (fraccionBi.posicionBarra(stringBi) == -1) {
                bi = Double.parseDouble(stringBi);
            } else {
                fraccionBi = fraccionBi.deTablaFraccion(stringBi);
                bi = fraccionBi.toDecimal();
            }

            if (aij > 0 && bi > 0) { //para que no haya division entre 0

                siguiente = (double) bi / aij;
                if (entra) {//solo se ejecuta una vez al inicio del ciclo
                    vSal = siguiente;// vSal toma el primer valor de siguiente
                    entra = false;
                    menor = i;

                }

                if (siguiente < vSal) {// condicional para que determinar el menor valor
                    vSal = siguiente; //guarda el valor menor para ser usado en la condicional a la sgt vuelta del ciclo

                    menor = i; //guarda la ubicacion de la fila 

                }
            }
        }
        return menor;
    }

    //funcion que devuelve la ubicacion de la variable de entrada
    private int variableEntrada() {
        int numColumnas = columnSRcompletas + 2;
        int numFilas = numRestricciones + 4;
        int contador = 3;
        int posicion = 2;// primera posicion
        Fraccion fraccionVent = new Fraccion();
        Fraccion fraccionSig = new Fraccion();
        double vEnt = 0;
        double siguiente = 0;
        String stringVent = "";
        String stringSig = "";
        // String valorSinM = "";
        ClaseM claseM = new ClaseM();


        //el primer valor puede ser el mayor si el resto de valores son iguales o menores
        /*valorSinM*/ stringVent = claseM.obtenerValorSinM(String.valueOf(tablaU.getValueAt(numFilas - 1, 2)));
        //stringVent = valorSinM;
        if (fraccionVent.posicionBarra(stringVent) == -1) {
            vEnt = Double.parseDouble(stringVent);
        } else {
            fraccionVent = fraccionVent.deTablaFraccion(stringVent);
            vEnt = fraccionVent.toDecimal();
        }

        while (contador < numColumnas - 1) {//ciclo para hallar
            stringSig = claseM.obtenerValorSinM(String.valueOf(tablaU.getValueAt(numFilas - 1, contador)));
            //stringSig = valorSinM;//String.valueOf(tbl_Inicial.getValueAt(numFilas - 1, contador));
            if (fraccionSig.posicionBarra(stringSig) == -1) {
                siguiente = Double.parseDouble(stringSig);
            } else {
                fraccionSig = fraccionSig.deTablaFraccion(stringSig);
                siguiente = fraccionSig.toDecimal();
            }
            if (cmbMetodo.getSelectedIndex() == 0 || cmbObjectivo.getSelectedIndex() == 1) {
                if (siguiente > vEnt) {//si el siguiente valor es menor
                    vEnt = siguiente;//vEnt toma ese valor para ser comparado en la siguiente vuelta del ciclo
                    posicion = contador;//guarda la ubicacion de la columna 
                }
            } else {
                //  if (cmbObjectivo.getSelectedIndex() == 0) { //MINIMIZAR
                if (siguiente < vEnt) {//si el siguiente valor es menor
                    vEnt = siguiente;//vEnt toma ese valor para ser comparado en la siguiente vuelta del ciclo
                    posicion = contador;//guarda la ubicacion de la columna 
                }

                /*  } else {
                    if (siguiente > vEnt) {//si el siguiente valor es menor
                        vEnt = siguiente;//vEnt toma ese valor para ser comparado en la siguiente vuelta del ciclo
                        posicion = contador;//guarda la ubicacion de la columna 
                    }*/
                // }
            }

            contador++;
        }

        return posicion;
    }

    //funcion que realiza todas las iteraciones
    private void iteracciones() {

        int numColumnas = columnSRcompletas + 2;
        int numFilas = numRestricciones + 4;
        Object anteriorIteracion[][] = new Object[numFilas][numColumnas]; //matriz para tomar los datos actuales de la tabla
        Object nuevaIteracion[][] = new Object[numFilas][numColumnas]; //matriz para insertar nuevos datos a la tabla

        //llenar matriz anterior
        for (int i = 0; i < numFilas; i++) {
            for (int j = 0; j < numColumnas; j++) {
                anteriorIteracion[i][j] = tablaU.getValueAt(i, j);
            }
        }

        //llenar 2 primeras filas de la nueva matriz
        for (int k = 0; k < 2; k++) {
            for (int m = 0; m < numColumnas; m++) {
                nuevaIteracion[k][m] = anteriorIteracion[k][m];
            }
        }

        int fila = variableSalida(); //ubicacion de la fila del pivote
        int col = variableEntrada(); //ubicacion de la columna del pivote

        //obtener fila 1
        Fraccion f1Fila1 = new Fraccion();
        Fraccion f2Fila1 = new Fraccion();
        Fraccion fila1[] = new Fraccion[numColumnas - 2];
        String string1Fila1 = "";
        String string2Fila1 = "";
        for (int i = 0; i < fila1.length; i++) {
            string1Fila1 = String.valueOf(anteriorIteracion[fila][2 + i]);
            string2Fila1 = String.valueOf(anteriorIteracion[fila][col]);
            f1Fila1 = f1Fila1.deTablaFraccion(string1Fila1);
            fila1[i] = f1Fila1.dividir(f2Fila1.deTablaFraccion(string2Fila1));
        }

        //obtener las otras filas 0 y llenar la tabla de ecuaciones
        int numColEcuaciones = numColumnas - 1;
        int numFilaEcuaciones = (numRestricciones - 1) * 6;
        Object matrizEcuaciones[][] = new Object[numFilaEcuaciones][numColEcuaciones]; //matriz 
        String stringNega = "";
        String stringFila0 = "";
        Fraccion fracNega = new Fraccion();
        Fraccion fracFila0 = new Fraccion();
        Fraccion fracMulti0 = new Fraccion();
        int saltoFila = -6;
        String multiplicador = "";
        for (int iterador = 2; iterador < numFilas - 2; iterador++) {

            if (iterador != fila) {//si se cumpla se calcula el valor de las nuevas filas 0
                saltoFila += 6;
                for (int j = 2; j < numColumnas; j++) {
                    stringNega = String.valueOf(anteriorIteracion[iterador][col]);
                    fracNega = fracNega.deTablaFraccion(stringNega);
                    fracNega = fracNega.multiplicar(new Fraccion(-1, 1));
                    stringFila0 = String.valueOf(anteriorIteracion[iterador][j]);
                    fracFila0 = fracFila0.deTablaFraccion(stringFila0);
                    fracMulti0 = fila1[j - 2].multiplicar(fracNega);
                    matrizEcuaciones[saltoFila + 1][j - 1] = String.valueOf(fila1[j - 2]);
                    matrizEcuaciones[saltoFila + 2][j - 1] = String.valueOf(fracMulti0.toString());
                    fracMulti0 = fracMulti0.sumar(fracFila0);
                    matrizEcuaciones[saltoFila + 3][j - 1] = String.valueOf(fracFila0.toString());
                    matrizEcuaciones[saltoFila + 4][j - 1] = String.valueOf(fracMulti0.toString());
                    nuevaIteracion[iterador][j] = fracMulti0.toString();

                    multiplicador = String.valueOf(anteriorIteracion[iterador][col]);
                    if (multiplicador.charAt(0) == '-') {
                        multiplicador.subSequence(1, multiplicador.length());
                    } else {
                        multiplicador = "-" + multiplicador;
                    }

                    matrizEcuaciones[saltoFila][0] = anteriorIteracion[iterador][1] + "' = " + anteriorIteracion[1][col]
                            + "(" + multiplicador + ") + " + anteriorIteracion[iterador][1];
                    matrizEcuaciones[1 + saltoFila][0] = anteriorIteracion[1][col] + " = ";
                    matrizEcuaciones[2 + saltoFila][0] = multiplicador + "(" + anteriorIteracion[1][col] + ") = ";
                    matrizEcuaciones[3 + saltoFila][0] = anteriorIteracion[iterador][1] + " = ";
                    matrizEcuaciones[4 + saltoFila][0] = anteriorIteracion[iterador][1] + "' = ";
                }

            } else { //si no se cumple se guarda la fila 1 donde corresponde
                for (int j = 2; j < numColumnas; j++) {
                    nuevaIteracion[iterador][j] = fila1[j - 2].toString();
                }
            }
        }

        //mostrar matriz en la tabla de ecuaciones
        DefaultTableModel modeloEcucaciones = new DefaultTableModel();
        tblOperaciones.setModel(modeloEcucaciones);
        for (int i = 0; i < numColEcuaciones; i++) { //Agregar columnas
            modeloEcucaciones.addColumn(" ");
        }
        Object objEcuaciones[] = new Object[numColEcuaciones];
        for (int i = 0; i < numFilaEcuaciones; i++) { //llenar filas
            for (int k = 0; k < numColEcuaciones; k++) {
                objEcuaciones[k] = matrizEcuaciones[i][k];
            }
            modeloEcucaciones.addRow(objEcuaciones);
        }

        //llenar CB y XB
        for (int icb = 2; icb < numFilas - 2; icb++) {
            for (int ixb = 0; ixb < 2; ixb++) {
                if (icb == fila) {
                    nuevaIteracion[fila][ixb] = anteriorIteracion[ixb][col];
                } else {
                    nuevaIteracion[icb][ixb] = anteriorIteracion[icb][ixb];
                }
            }
        }
        //llenar Zj
        nuevaIteracion[numFilas - 2][0] = anteriorIteracion[numFilas - 2][0]; //" "
        nuevaIteracion[numFilas - 2][1] = anteriorIteracion[numFilas - 2][1]; //"Zj"
        String stringZj = "";
        String acumulaZj = "";

        for (int i = 2; i < numColumnas; i++) {
            acumulaZj = "";
            for (int j = 2; j < numFilas - 2; j++) {
                stringZj = guardarZj(String.valueOf(nuevaIteracion[j][0]), String.valueOf(nuevaIteracion[j][i]));
                acumulaZj += stringZj + ";";
            }
            nuevaIteracion[numFilas - 2][i] = devolverZj(acumulaZj);
        }

        //llenar Cj-Zj
        nuevaIteracion[numFilas - 1][0] = anteriorIteracion[numFilas - 1][0]; //" "
        nuevaIteracion[numFilas - 1][1] = anteriorIteracion[numFilas - 1][1]; //"Zj-Cj"
        nuevaIteracion[numFilas - 1][numColumnas - 1] = anteriorIteracion[numFilas - 1][numColumnas - 1]; //" "
        String stringA = "";
        String stringB = "";
        for (int i = 2; i < numColumnas - 1; i++) {
            stringA = String.valueOf(nuevaIteracion[0][i]);
            stringB = String.valueOf(nuevaIteracion[numFilas - 2][i]);
            nuevaIteracion[numFilas - 1][i] = devolverZjCj(stringA, stringB);
        }

        //mostrar matriz en la tabla
        DefaultTableModel modeloIteracion = new DefaultTableModel();
        Object obj[] = new Object[numColumnas];
        tablaU.setModel(modeloIteracion);
        for (int i = 0; i < numColumnas; i++) { //Agregar columnas
            modeloIteracion.addColumn(" ");
        }
        for (int i = 0; i < numFilas; i++) { //llenar filas
            for (int k = 0; k < numColumnas; k++) {
                obj[k] = nuevaIteracion[i][k];
            }
            modeloIteracion.addRow(obj);
        }

        //pintar pivote
        pintarPivote();

        numIteraciones++;
        lblNumItera.setText("" + numIteraciones + "");

    }

    //funciones no utilizadas
    private void resetearTablas(JTable tabla) {
        DefaultTableModel modelo = (DefaultTableModel) tabla.getModel();
        if (modelo.getColumnCount() > 0) {
            for (int i = tabla.getRowCount() - 1; i >= 0; i--) {
                modelo.removeRow(i);
            }
            for (int i = tabla.getColumnCount() - 1; i >= 0; i--) {
                tabla.getColumnModel().removeColumn(tabla.getColumnModel().getColumn(i));
            }
        }
    }

    //Verificar Iteracciones
    public boolean seguirIteracionesSimplex() {
        boolean seSigue = false;
        int numColumnas = columnSRcompletas + 2;
        int numFilas = numRestricciones + 4;
        String stringSig = "";
        double siguiente = 0;
        ClaseM claseM = new ClaseM();
        Fraccion fraccionSig = new Fraccion();
        for (int i = 2; i < numColumnas - 2; i++) {
            stringSig = claseM.obtenerValorSinM(String.valueOf(tablaU.getValueAt(numFilas - 1, i)));
            if (fraccionSig.posicionBarra(stringSig) == -1) {
                siguiente = Double.parseDouble(stringSig);
            } else {
                fraccionSig = fraccionSig.deTablaFraccion(stringSig);
                siguiente = fraccionSig.toDecimal();
            }
            if (siguiente > 0) {
                seSigue = true;
            }
        }
        return seSigue;
    }

    public boolean seguirIteracionesBigM() {
        boolean seSigue = false;
        int numColumnas = columnSRcompletas + 2;
        int numFilas = numRestricciones + 4;
        String stringSig = "";
        double siguiente = 0;
        ClaseM claseM = new ClaseM();
        Fraccion fraccionSig = new Fraccion();
        for (int i = 2; i < numColumnas - 2; i++) {
            stringSig = claseM.obtenerValorSinM(String.valueOf(tablaU.getValueAt(numFilas - 1, i)));
            if (fraccionSig.posicionBarra(stringSig) == -1) {
                siguiente = Double.parseDouble(stringSig);
            } else {
                fraccionSig = fraccionSig.deTablaFraccion(stringSig);
                siguiente = fraccionSig.toDecimal();
            }
            if (siguiente < 0) {
                seSigue = true;
            }
        }
        return seSigue;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelGeneral = new javax.swing.JPanel();
        lblVariables = new javax.swing.JLabel();
        lblRestricciones = new javax.swing.JLabel();
        txtVariables = new javax.swing.JTextField();
        txtRestricciones = new javax.swing.JTextField();
        btnGenerar = new javax.swing.JButton();
        panelTablaIteraccion = new javax.swing.JPanel();
        btnIteraccion = new javax.swing.JButton();
        lblIteraccion = new javax.swing.JLabel();
        lblNumItera = new javax.swing.JLabel();
        scrollOperaciones = new javax.swing.JScrollPane();
        tblOperaciones = new javax.swing.JTable();
        btnGenerarPDF = new javax.swing.JButton();
        scroll_tablaU = new javax.swing.JScrollPane();
        tablaU = new javax.swing.JTable();
        lblTablaU = new javax.swing.JLabel();
        lblTablaIOpera = new javax.swing.JLabel();
        panelRestrFunc = new javax.swing.JPanel();
        scrollSR = new javax.swing.JScrollPane();
        tblSR = new javax.swing.JTable();
        scrollTFuncion = new javax.swing.JScrollPane();
        tblFuncion = new javax.swing.JTable();
        btnCalcular = new javax.swing.JButton();
        cmbObjectivo = new javax.swing.JComboBox<>();
        cmbMetodo = new javax.swing.JComboBox<>();
        lblFuncionObjetivo = new javax.swing.JLabel();
        lblFunObj = new javax.swing.JLabel();
        lblFondo = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1360, 768));
        setSize(new java.awt.Dimension(1360, 768));

        panelGeneral.setAutoscrolls(true);
        panelGeneral.setMinimumSize(new java.awt.Dimension(900, 520));
        panelGeneral.setPreferredSize(new java.awt.Dimension(1360, 768));
        panelGeneral.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblVariables.setFont(new java.awt.Font("Roboto Black", 0, 15)); // NOI18N
        lblVariables.setForeground(new java.awt.Color(255, 255, 255));
        lblVariables.setText("Ingrese el número de variables:");
        panelGeneral.add(lblVariables, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 250, 30));

        lblRestricciones.setFont(new java.awt.Font("Roboto Black", 0, 15)); // NOI18N
        lblRestricciones.setForeground(new java.awt.Color(255, 255, 255));
        lblRestricciones.setText("Ingrese el número de restricciones:");
        panelGeneral.add(lblRestricciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, 260, 30));

        txtVariables.setFont(new java.awt.Font("Open Sans", 1, 15)); // NOI18N
        txtVariables.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtVariablesKeyTyped(evt);
            }
        });
        panelGeneral.add(txtVariables, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 60, 60, 30));

        txtRestricciones.setFont(new java.awt.Font("Open Sans", 1, 15)); // NOI18N
        txtRestricciones.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtRestriccionesKeyTyped(evt);
            }
        });
        panelGeneral.add(txtRestricciones, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 100, 60, 30));

        btnGenerar.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        btnGenerar.setForeground(new java.awt.Color(0, 153, 153));
        btnGenerar.setText("GENERAR");
        btnGenerar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarActionPerformed(evt);
            }
        });
        panelGeneral.add(btnGenerar, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 20, 130, 30));

        panelTablaIteraccion.setOpaque(false);

        btnIteraccion.setFont(new java.awt.Font("Roboto Black", 0, 15)); // NOI18N
        btnIteraccion.setForeground(new java.awt.Color(0, 153, 153));
        btnIteraccion.setText("Siguiente Iteración");
        btnIteraccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIteraccionActionPerformed(evt);
            }
        });

        lblIteraccion.setFont(new java.awt.Font("Roboto Black", 1, 15)); // NOI18N
        lblIteraccion.setForeground(new java.awt.Color(255, 255, 255));
        lblIteraccion.setText("Iteración:");

        lblNumItera.setFont(new java.awt.Font("Arial Black", 0, 15)); // NOI18N

        tblOperaciones.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        scrollOperaciones.setViewportView(tblOperaciones);

        btnGenerarPDF.setFont(new java.awt.Font("Roboto Black", 0, 15)); // NOI18N
        btnGenerarPDF.setForeground(new java.awt.Color(0, 153, 153));
        btnGenerarPDF.setText("Guardar PDF");
        btnGenerarPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarPDFActionPerformed(evt);
            }
        });

        tablaU.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        scroll_tablaU.setViewportView(tablaU);

        lblTablaU.setFont(new java.awt.Font("Roboto Black", 0, 15)); // NOI18N
        lblTablaU.setForeground(new java.awt.Color(255, 255, 255));
        lblTablaU.setText("Tabla U");

        lblTablaIOpera.setFont(new java.awt.Font("Roboto Black", 0, 15)); // NOI18N
        lblTablaIOpera.setForeground(new java.awt.Color(255, 255, 255));
        lblTablaIOpera.setText("Tabla Operaciones");

        javax.swing.GroupLayout panelTablaIteraccionLayout = new javax.swing.GroupLayout(panelTablaIteraccion);
        panelTablaIteraccion.setLayout(panelTablaIteraccionLayout);
        panelTablaIteraccionLayout.setHorizontalGroup(
            panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaIteraccionLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollOperaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 802, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(panelTablaIteraccionLayout.createSequentialGroup()
                            .addComponent(lblIteraccion, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(lblNumItera, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(47, 47, 47)
                            .addComponent(btnIteraccion)
                            .addGap(18, 18, 18)
                            .addComponent(btnGenerarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTablaU))
                        .addComponent(scroll_tablaU, javax.swing.GroupLayout.PREFERRED_SIZE, 802, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTablaIOpera, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        panelTablaIteraccionLayout.setVerticalGroup(
            panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelTablaIteraccionLayout.createSequentialGroup()
                .addGroup(panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelTablaIteraccionLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblIteraccion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                                .addComponent(lblNumItera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(panelTablaIteraccionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnIteraccion, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnGenerarPDF, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelTablaIteraccionLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblTablaU, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(scroll_tablaU, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTablaIOpera)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollOperaciones, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );

        panelGeneral.add(panelTablaIteraccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 80, 840, 620));

        panelRestrFunc.setOpaque(false);

        tblSR.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        tblSR.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblSRKeyTyped(evt);
            }
        });
        scrollSR.setViewportView(tblSR);

        tblFuncion.setFont(new java.awt.Font("Open Sans", 1, 12)); // NOI18N
        tblFuncion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tblFuncionKeyTyped(evt);
            }
        });
        scrollTFuncion.setViewportView(tblFuncion);

        btnCalcular.setFont(new java.awt.Font("Roboto Black", 0, 12)); // NOI18N
        btnCalcular.setForeground(new java.awt.Color(0, 153, 153));
        btnCalcular.setText("CALCULAR");
        btnCalcular.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalcularActionPerformed(evt);
            }
        });

        cmbObjectivo.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        cmbObjectivo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout panelRestrFuncLayout = new javax.swing.GroupLayout(panelRestrFunc);
        panelRestrFunc.setLayout(panelRestrFuncLayout);
        panelRestrFuncLayout.setHorizontalGroup(
            panelRestrFuncLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRestrFuncLayout.createSequentialGroup()
                .addGroup(panelRestrFuncLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRestrFuncLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panelRestrFuncLayout.createSequentialGroup()
                            .addGap(14, 14, 14)
                            .addComponent(scrollSR, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRestrFuncLayout.createSequentialGroup()
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(scrollTFuncion, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panelRestrFuncLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(cmbObjectivo, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panelRestrFuncLayout.createSequentialGroup()
                        .addGap(132, 132, 132)
                        .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        panelRestrFuncLayout.setVerticalGroup(
            panelRestrFuncLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRestrFuncLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cmbObjectivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrollTFuncion, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollSR, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(btnCalcular, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54))
        );

        panelGeneral.add(panelRestrFunc, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, 380, 300));

        cmbMetodo.setFont(new java.awt.Font("Roboto", 1, 12)); // NOI18N
        cmbMetodo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbMetodo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbMetodoItemStateChanged(evt);
            }
        });
        panelGeneral.add(cmbMetodo, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, 170, 30));

        lblFuncionObjetivo.setFont(new java.awt.Font("Roboto Black", 0, 15)); // NOI18N
        panelGeneral.add(lblFuncionObjetivo, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 40, 370, 30));

        lblFunObj.setFont(new java.awt.Font("Roboto Black", 1, 15)); // NOI18N
        lblFunObj.setForeground(new java.awt.Color(255, 255, 255));
        lblFunObj.setText("Función Objetivo: ");
        panelGeneral.add(lblFunObj, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 40, -1, 30));

        lblFondo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/atardecer2.jpg"))); // NOI18N
        lblFondo.setPreferredSize(new java.awt.Dimension(1360, 768));
        panelGeneral.add(lblFondo, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1360, 770));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelGeneral, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void mostrarLabels(boolean estado) {
        lblFunObj.setVisible(estado);
        lblFuncionObjetivo.setVisible(estado);
        resetearTablas(tblOperaciones);
        resetearTablas(tablaU);
        panelTablaIteraccion.setVisible(estado);

    }
    private void btnGenerarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarActionPerformed
        if (txtRestricciones.getText().isEmpty() || txtVariables.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se ingresaron correctamente los datos");
        } else {
            mostrarLabels(false);
            panelRestrFunc.setVisible(true);
            numVariables = Integer.parseInt(txtVariables.getText());
            numRestricciones = Integer.parseInt(txtRestricciones.getText());
            llenarTablaVar();
            llenarTablaRestr();
        }

    }//GEN-LAST:event_btnGenerarActionPerformed

    private void btnCalcularActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularActionPerformed
        crearCarpeta();
        if (tblFuncion.isEditing()) {//si se esta edtando la tabla
            tblFuncion.getCellEditor().stopCellEditing();//detenga la edicion
        }

        if (tblSR.isEditing()) {//si se esta edtando la tabla
            tblSR.getCellEditor().stopCellEditing();//detenga la edicion
        }

        btnIteraccion.requestFocus();
        this.setExtendedState(MAXIMIZED_BOTH);
        numFotos = 0;
        //  mostrarLabels(true);
        btnGenerarPDF.setVisible(false);
        matrizInicial();
        final SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                guardarCaptura(panelTablaIteraccion);
                return null;
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnCalcularActionPerformed
    public String[] guardarRestricciones() {
        String[] restriccionesString = new String[numRestricciones + 1];
        int columnas = numVariables + 2;
        String valor = "";
        //restricciones
        for (int i = 0; i < restriccionesString.length - 1; i++) {
            for (int j = 0; j < columnas; j++) {
                valor = String.valueOf(tblSR.getValueAt(i, j));
                if (j < numVariables) {
                    if (j == 0) {
                        restriccionesString[i] = valor + "X" + (j + 1);

                    } else {
                        if (valor.charAt(0) == '-') {
                            restriccionesString[i] += valor + "X" + (j + 1);
                        } else {
                            restriccionesString[i] += "+" + valor + "X" + (j + 1);
                        }
                    }
                } else {
                    restriccionesString[i] += valor;

                }
            }
        }
        //no negativadad
        restriccionesString[restriccionesString.length - 1] = "";
        String signo = "";
        for (int fila = 0; fila < numRestricciones; fila++) {
            signo = String.valueOf(tblSR.getValueAt(fila, columnas - 2));
            if (signo == "=") {
                signo = " sin signo de restriccion ";
            } else {
                signo += "0; ";
            }
            restriccionesString[restriccionesString.length - 1] += String.valueOf("X" + (fila + 1))
                    + signo;
        }
        return restriccionesString;
    }

    private void procesoBTNiteracion() {
        iteracciones();
        final SwingWorker worker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                numFotos++;
                guardarCaptura(panelTablaIteraccion);
                return null;
            }
        };
        worker.execute();

    }

    private void btnIteraccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIteraccionActionPerformed

        if (cmbMetodo.getSelectedIndex() == 0 || (cmbMetodo.getSelectedIndex() == 1 && cmbObjectivo.getSelectedIndex() == 1)) {
            if (seguirIteracionesSimplex()) {
                procesoBTNiteracion();
            } else {
                JOptionPane.showMessageDialog(null, "Se han terminado las iteraciones");
                btnGenerarPDF.setVisible(true);
            }

        } else {

            if (seguirIteracionesBigM()) {

                procesoBTNiteracion();
            } else {
                JOptionPane.showMessageDialog(null, "Se han terminado las iteraciones");
                btnGenerarPDF.setVisible(true);
            }

        }


    }//GEN-LAST:event_btnIteraccionActionPerformed

    private void cmbMetodoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbMetodoItemStateChanged
        DefaultComboBoxModel modelo = new DefaultComboBoxModel();
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            modelo.addElement("<=");
            if (cmbMetodo.getSelectedIndex() == 0) { //Metodo Simplex
                cmbObjectivo.setSelectedIndex(1);//Maximizar
                cmbObjectivo.setVisible(false);
            } else {
                cmbObjectivo.setSelectedIndex(0);
                cmbObjectivo.setVisible(true);
                modelo.addElement(">=");
                modelo.addElement("=");
            }
        }

        cmbSigno.setModel(modelo);


    }//GEN-LAST:event_cmbMetodoItemStateChanged

    private void txtVariablesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtVariablesKeyTyped
        char caracter = evt.getKeyChar();
        // Verificar si la tecla pulsada no es un digito
        if ((caracter < '0') || (caracter > '9')) {
            evt.consume();  // ignorar el evento de teclado
        }
    }//GEN-LAST:event_txtVariablesKeyTyped

    private void txtRestriccionesKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtRestriccionesKeyTyped
        char caracter = evt.getKeyChar();
        // Verificar si la tecla pulsada no es un digito
        if ((caracter < '0') || (caracter > '9')) {
            evt.consume();  // ignorar el evento de teclado
        }
    }//GEN-LAST:event_txtRestriccionesKeyTyped

    private void btnGenerarPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarPDFActionPerformed
        String metodo = "";
        String funcion = "";
        String ruta = escogerRuta();
        if (cmbMetodo.getSelectedIndex() == 0) {
            metodo = "Método Simplex";
        } else {
            metodo = "Método Big M";
        }
        funcion = lblFunObj.getText() + " " + lblFuncionObjetivo.getText();
        if (!ruta.isEmpty()) {
            GenerarPDF generarPDF = new GenerarPDF(directorio, ruta, metodo, funcion, guardarRestricciones(), numIteraciones);
            generarPDF.start();
        }

    }//GEN-LAST:event_btnGenerarPDFActionPerformed

    private void tblFuncionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblFuncionKeyTyped
        char caracter = evt.getKeyChar();
        // Verificar si la tecla pulsada no es un digito
        if (((caracter < '0') || (caracter > '9')) && (caracter != '.') && (caracter != '-')) {
            evt.consume();  // ignorar el evento de teclado
        }
    }//GEN-LAST:event_tblFuncionKeyTyped

    private void tblSRKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblSRKeyTyped
        char caracter = evt.getKeyChar();
        // Verificar si la tecla pulsada no es un digito
        if (((caracter < '0') || (caracter > '9')) && (caracter != '.') && (caracter != '-')) {
            evt.consume();  // ignorar el evento de teclado
        }
    }//GEN-LAST:event_tblSRKeyTyped

    private String escogerRuta() {
        JFileChooser jF1 = new JFileChooser();
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("*.pdf", "pdf");
        jF1.setFileFilter(filtro);
        String ruta = "";
        String newRuta = "";
        try {
            if (jF1.showSaveDialog(this) == jF1.APPROVE_OPTION) {
                ruta = jF1.getSelectedFile().getAbsolutePath();
                ruta = devolverExtension(ruta);
                if (new File(ruta).exists()) {
                    int decision = JOptionPane.showConfirmDialog(this,
                            "¿El fichero ya existe,deseas reemplazarlo?", "Guardar PDF", JOptionPane.YES_NO_OPTION);
                    if (JOptionPane.OK_OPTION == decision) {
                        newRuta = ruta;
                    }
                } else {
                    newRuta = ruta;
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newRuta;
    }

    private String devolverExtension(String pdf) {
        String extension = pdf.substring(pdf.length() - 4, pdf.length());
        if (".pdf".equals(extension)) {
            return pdf;
        } else {
            return pdf + ".pdf";
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        try {

            UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Framehd.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Framehd.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Framehd.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Framehd.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Framehd().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCalcular;
    private javax.swing.JButton btnGenerar;
    private javax.swing.JButton btnGenerarPDF;
    private javax.swing.JButton btnIteraccion;
    private javax.swing.JComboBox<String> cmbMetodo;
    private javax.swing.JComboBox<String> cmbObjectivo;
    private javax.swing.JLabel lblFondo;
    private javax.swing.JLabel lblFunObj;
    private javax.swing.JLabel lblFuncionObjetivo;
    private javax.swing.JLabel lblIteraccion;
    private javax.swing.JLabel lblNumItera;
    private javax.swing.JLabel lblRestricciones;
    private javax.swing.JLabel lblTablaIOpera;
    private javax.swing.JLabel lblTablaU;
    private javax.swing.JLabel lblVariables;
    private javax.swing.JPanel panelGeneral;
    private javax.swing.JPanel panelRestrFunc;
    private javax.swing.JPanel panelTablaIteraccion;
    private javax.swing.JScrollPane scrollOperaciones;
    private javax.swing.JScrollPane scrollSR;
    private javax.swing.JScrollPane scrollTFuncion;
    private javax.swing.JScrollPane scroll_tablaU;
    private javax.swing.JTable tablaU;
    private javax.swing.JTable tblFuncion;
    private javax.swing.JTable tblOperaciones;
    private javax.swing.JTable tblSR;
    private javax.swing.JTextField txtRestricciones;
    private javax.swing.JTextField txtVariables;
    // End of variables declaration//GEN-END:variables
}
