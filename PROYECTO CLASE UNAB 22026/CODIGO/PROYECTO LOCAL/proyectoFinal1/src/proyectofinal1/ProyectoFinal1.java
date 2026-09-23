/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyectofinal1;

import Vista.Pantalla2;

/**
 * Clase principal del proyecto: es la clase configurada como main.class
 * (nbproject/project.properties) y por lo tanto la que Ant/NetBeans coloca
 * como Main-Class en el manifiesto del jar ejecutable (dist/proyectoFinal1.jar).
 *
 * @author salones
 */
public class ProyectoFinal1 {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(ProyectoFinal1.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        /* Se establece Nimbus como look and feel, si está disponible */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Se crea y muestra la ventana principal de la aplicación */
        java.awt.EventQueue.invokeLater(() -> new Pantalla2().setVisible(true));
    }

}
