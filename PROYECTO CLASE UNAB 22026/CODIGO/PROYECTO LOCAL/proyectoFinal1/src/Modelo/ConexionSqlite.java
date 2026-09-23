/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import Controlador.Producto;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de conexión y prueba con SQLite usando JDBC
 * Requisito: sqlite-jdbc-3.36.x.jar agregado a las librerías del proyecto Ant
 */
public class ConexionSqlite {

    // Ruta del archivo de base de datos (se crea automáticamente si no existe)
    private static final String URL = "jdbc:sqlite:db-eje1.db";

     //─── Obtener conexión ─────────────────────────────────────────
    public static Connection conectar() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
            System.out.println(" Conexión exitosa a SQLite");   
        } catch (SQLException e) {
            System.out.println(" Error al conectar: " + e.getMessage());
        }
        return conn;
    }

    // ─── Crear tabla de ejemplo ───────────────────────────────────
    public static void crearTabla() {
        String sql = "CREATE TABLE IF NOT EXISTS estudiantes ("
                   + "id      INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "nombre  TEXT    NOT NULL, "
                   + "nota    REAL    NOT NULL"
                   + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Tabla 'estudiantes1' lista");

        } catch (SQLException e) {
            System.out.println("❌ Error al crear tabla: " + e.getMessage());
        }
    }
    
        // ─── Crear tabla de ejemplo ───────────────────────────────────
    public static void crearTabla2() {
        String sql = "CREATE TABLE IF NOT EXISTS productos ("
                   + "id_producto      INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "nombre_producto  TEXT    NOT NULL, "
                   + "precio    REAL    NOT NULL"
                   + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Tabla 'estudiantes2' lista");

        } catch (SQLException e) {
            System.out.println("❌ Error al crear tabla: " + e.getMessage());
        }
    }

    
     // ─── Crear tabla de ejemplo ───────────────────────────────────
    public static void crearTablaProducto() {
        String sql = "CREATE TABLE IF NOT EXISTS productos ("
                   + "id_producto      INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "nombre_producto  TEXT    NOT NULL, "
                   + "unidades INTEGER NOT NULL,"
                   + "precio_unitario    REAL    NOT NULL,"
                   + "categoria TEXT NOT NULL,"
                   + "descripcion TEXT NOT NULL"
                   + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ Tabla 'productos3' lista");

        } catch (SQLException e) {
            System.out.println("❌ Error al crear tabla: " + e.getMessage());
        }
    }

   
    
    // ─── Insertar un registro ─────────────────────────────────────
    public static void insertar(String nombre, double nota) {
        String sql = "INSERT INTO estudiantes (nombre, nota) VALUES ('"
                   + nombre + "', " + nota + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("✅ Insertado: " + nombre + " - " + nota);

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
        }
    }
    
        // ─── Insertar un registro ─────────────────────────────────────
    
    /**
     * String sql = "CREATE TABLE IF NOT EXISTS productos ("
                   + "id_producto      INTEGER PRIMARY KEY AUTOINCREMENT, "
                   + "nombre_producto  TEXT    NOT NULL, "
                   + "precio    REAL    NOT NULL"
                   + ");";
     **/
    
    public static void insertar2(String nombre_producto, double precio) {
        String sql = "INSERT INTO productos (nombre_producto, precio) "
                + "VALUES ("
                + "'" + nombre_producto + "', "
                + "" + precio + ""
                + ");";
        
        System.out.println("query:" + sql);

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("✅ Insertado: " + nombre_producto + " - " + precio);

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
        }
    }

    
        public static void insertarProducto(Producto p) {
            
        String sql = "INSERT INTO productos (nombre_producto, unidades, precio_unitario, categoria, descripcion) "
                + "VALUES ("
                + "'" + p.nombre_producto + "', "
                + "" + p.unidades + ","
                + "" + p.precio_unitario + ","
                + "'" + p.categoria + "', "
                + "'" + p.descripcion + "' "
                + ");";
        
        System.out.println("query:" + sql);

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("✅ Insertado: " + p.nombre_producto + " - " + p.precio_unitario);

        } catch (SQLException e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
        }
    }
    // ─── Actualizar un producto existente ─────────────────────────
    public static void actualizarProducto(Producto p) {
        String sql = "UPDATE productos SET "
                   + "nombre_producto = ?, "
                   + "unidades = ?, "
                   + "precio_unitario = ?, "
                   + "categoria = ?, "
                   + "descripcion = ? "
                   + "WHERE id_producto = ?;";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.nombre_producto);
            ps.setInt(2, p.unidades);
            ps.setDouble(3, p.precio_unitario);
            ps.setString(4, p.categoria);
            ps.setString(5, p.descripcion);
            ps.setInt(6, p.id_producto);

            int filas = ps.executeUpdate();
            System.out.println("✅ Producto actualizado (" + filas + " fila(s)): " + p.nombre_producto);

        } catch (SQLException e) {
            System.out.println("❌ Error al actualizar producto: " + e.getMessage());
        }
    }

    // ─── Consultar todos los productos (para llenar la JTable) ────
    public static List<Producto> consultarProductos() {
        List<Producto> lista = new ArrayList<>();
        String sql = "SELECT id_producto, nombre_producto, unidades, precio_unitario, categoria, descripcion FROM productos;";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto p = new Producto();
                p.id_producto     = rs.getInt("id_producto");
                p.nombre_producto = rs.getString("nombre_producto");
                p.unidades        = rs.getInt("unidades");
                p.precio_unitario = rs.getDouble("precio_unitario");
                p.categoria       = rs.getString("categoria");
                p.descripcion     = rs.getString("descripcion");
                lista.add(p);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar productos: " + e.getMessage());
        }

        return lista;
    }

    // ─── Consultar todos los registros ────────────────────────────
    public static void consultarTodos() {
        String sql = "SELECT * FROM estudiantes;";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            System.out.println("\n─── Estudiantes en la BD ───");
            while (rs.next()) {
                System.out.println("ID: "     + rs.getInt("id")
                                 + " | Nombre: " + rs.getString("nombre")
                                 + " | Nota: "   + rs.getDouble("nota"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar: " + e.getMessage());
        }
    }
    
        // ─── Consultar todos los registros ────────────────────────────
    // NOTA: esta version quedo desactualizada porque la tabla "productos" ya
    // no tiene la columna "precio" (ahora tiene unidades, precio_unitario,
    // categoria y descripcion). Se deja corregida para que coincida con el
    // esquema actual; para llenar la JTable se usa consultarProductos().
    public static void consultarTodos2() {
        String sql = "SELECT * FROM productos;";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {

            System.out.println("\n─── Productos en la BD ───");
            while (rs.next()) {
                System.out.println("ID_Productos: "     + rs.getInt("id_producto")
                                 + " | nombre_producto: " + rs.getString("nombre_producto")
                                 + " | unidades: "        + rs.getInt("unidades")
                                 + " | precio_unitario: " + rs.getDouble("precio_unitario")
                                 + " | categoria: "       + rs.getString("categoria")
                                 + " | descripcion: "     + rs.getString("descripcion"));
            }

        } catch (SQLException e) {
            System.out.println("❌ Error al consultar: " + e.getMessage());
        }
    }

    // ─── Main de prueba ───────────────────────────────────────────
    public static void main(String[] args) {
/*
        conectar();
        
        crearTabla();
        crearTabla2();

        insertar("Ana Gómez",   4.5);
        insertar("Luis Pérez",  3.8);
        insertar("María López", 4.9);
        
        insertar2("silvia", 2);

        consultarTodos();
        consultarTodos2();**/


    }
}

