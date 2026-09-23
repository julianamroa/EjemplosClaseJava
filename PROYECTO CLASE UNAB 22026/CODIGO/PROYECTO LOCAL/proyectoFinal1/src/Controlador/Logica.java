/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import Modelo.ConexionSqlite;
import java.util.List;

/**
 *
 * @author salones
 */
public class Logica {
    
    //Atributos de la clase
    ConexionSqlite bd ;
    
    
    //Metodos de la clase
    
    public void crearConexion(){
    
        bd = new ConexionSqlite();
        bd.conectar();

        
    }
    
    public void crearTablaProducto(){
        bd = new ConexionSqlite();
        bd.crearTablaProducto();
    }
    
    
    public void insertarNuevoProducto(Producto p){
    
        bd = new ConexionSqlite();
        bd.insertarProducto(p);
    }
    
    public void actualizarProducto(Producto p){
    
        bd = new ConexionSqlite();
        bd.actualizarProducto(p);
    }
    
    public void eliminarProducto(int id_producto){
    
        bd = new ConexionSqlite();
        bd.eliminarProducto(id_producto);
    }
    
    public List<Producto> consultarProductos(){
        
        bd = new ConexionSqlite();
        return bd.consultarProductos();
    }
    
    
    
    public static void main(String[] args) {
        
        
    }
}
