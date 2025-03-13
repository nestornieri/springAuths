package com.example.clase1.services;

import com.example.clase1.model.Producto;

import java.util.Collection;
import java.util.Optional;

public interface ProductoService {
    Collection<Producto> listarProductos();
    Optional<Producto> productosPorId(Integer id);
    Producto addProducto(Producto producto);
    Producto actProducto(Integer id, Producto producto);
    boolean remProducto(Integer id);
}