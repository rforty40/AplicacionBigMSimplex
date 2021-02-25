package controlador;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class GenerarPDF extends Thread {

    private String metodo;
    private String funcionObjetivo;
    private String[] restricciones;
    private int iteraciones;
    private String ruta;
    private File directorio;

    public GenerarPDF(File directorio, String ruta, String metodo, String funcionObjetivo, String[] restricciones, int iteraciones) {
        this.ruta = ruta;
        this.metodo = metodo;
        this.funcionObjetivo = funcionObjetivo;
        this.restricciones = restricciones;
        this.iteraciones = iteraciones;
        this.directorio = directorio;
    }

    @Override
    public void run() {
        // Se crea el documento
        Document documento = new Document();

        // Se crea el OutputStream para el fichero donde queremos dejar el pdf.
        FileOutputStream ficheroPdf = null;
        try {
            ficheroPdf = new FileOutputStream(ruta);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GenerarPDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            // Se asocia el documento al OutputStream y se indica que el espaciado entre
            // lineas sera de 20. Esta llamada debe hacerse antes de abrir el documento
            PdfWriter.getInstance(documento, ficheroPdf).setInitialLeading(20);
        } catch (DocumentException ex) {
            Logger.getLogger(GenerarPDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Se abre el documento.
        documento.open();
        //agregar texto
        try {
            documento.add(new Paragraph(metodo,
                    FontFactory.getFont("arial", // fuente
                            22, // tamaño
                            Font.ITALIC, // estilo
                            BaseColor.BLUE)));             // color
            documento.add(new Paragraph(funcionObjetivo,
                    FontFactory.getFont("arial", // fuente
                            16, // tamaño
                            Font.ITALIC, // estilo
                            BaseColor.BLACK)));             // color
            documento.add(new Paragraph("S.R",
                    FontFactory.getFont("arial", // fuente
                            16, // tamaño
                            Font.ITALIC, // estilo
                            BaseColor.BLACK)));             // color
            for (int i = 0; i < restricciones.length; i++) {
                documento.add(new Paragraph(restricciones[i],
                        FontFactory.getFont("arial", // fuente
                                12, // tamaño
                                Font.ITALIC, // estilo
                                BaseColor.BLACK)));             // color
            }

        } catch (DocumentException ex) {
            Logger.getLogger(GenerarPDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        //matriz inicial
        try {
            documento.add(new Paragraph("Matriz Inicial",
                    FontFactory.getFont("arial", // fuente
                            16, // tamaño
                            Font.ITALIC, // estilo
                            BaseColor.BLACK)));             // color
        } catch (DocumentException ex) {
            Logger.getLogger(GenerarPDF.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Image foto = Image.getInstance(directorio.getPath() + "/0.png");
            //foto.scaleToFit(100, 100);
            foto.scaleToFit(500, 500);
            foto.setAlignment(Chunk.ALIGN_MIDDLE);
            documento.add(foto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //fotos iteraciones
        for (int i = 1; i <= iteraciones; i++) {
           
            try {
                Image foto = Image.getInstance(directorio.getPath() + "/" + i + ".png");
                foto.scaleToFit(500, 500);
                foto.setAlignment(Chunk.ALIGN_MIDDLE);
           
                 
                documento.add(foto);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        documento.close();

        JOptionPane.showMessageDialog(null, "PDF guardado con exito");
        int borrar = JOptionPane.showConfirmDialog(null, "¿Desea borrar la carpeta de capturas: '"
                + directorio.getPath() + "' del escritorio?", "Alerta!", JOptionPane.YES_NO_OPTION);
        if (borrar == JOptionPane.OK_OPTION) {
            GenerarPDF.bDirectorio(directorio);

        }

    }

    public static void bDirectorio(File borrar) {
        if (borrar.isDirectory()) {
            try {
                for (File listFile : borrar.listFiles()) {
                    if (listFile.isFile()) {
                        listFile.delete();
                    } else {
                        if (listFile.isDirectory()) {
                            bDirectorio(listFile);
                            listFile.delete();
                        }
                    }
                }
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        borrar.delete();
    }
}
