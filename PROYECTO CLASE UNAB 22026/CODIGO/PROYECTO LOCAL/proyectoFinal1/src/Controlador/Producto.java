/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author salones
 */
public class Producto {
    
    //Atributos
    
    public int id_producto;
    public String nombre_producto;
    public int unidades;
    public double precio_unitario;
    public String categoria;
    public String descripcion;
    
    
    //Constructor

    public Producto(String nombre_producto, int unidades, double precio_unitario, String categoria, String descripcion) {
        this.nombre_producto = nombre_producto;
        this.unidades = unidades;
        this.precio_unitario = precio_unitario;
        this.categoria = categoria;
        this.descripcion = descripcion;
    }

    public Producto() {
    }
    
    
    
    
    //Metodos de la clase

    public void info(Producto p1){
    
        System.out.println("Producto:");
        System.out.println("nombre: " + p1.nombre_producto);
        System.out.println("unidades: " +p1.unidades);
        System.out.println("precio unitario: " +p1.precio_unitario);
        System.out.println("categoria: " +p1.categoria);
        System.out.println("descripcion: " +p1.descripcion);
    }
    
    public static void main(String[] args) {
        System.out.println("Producto 1");
        Producto p1 = new Producto("Jabon durazno", 10, 15000, "Jabones", "Jabon delicioso de durazno");
        p1.info(p1);
        
        System.out.println("");
        System.out.println("Producto 2");
        Producto p2 = new Producto("Jabon mango", 5, 15000, "Jabones", "Jabon delicioso de mango");
        p2.info(p2);
    }
    
    
    
    
    
}
