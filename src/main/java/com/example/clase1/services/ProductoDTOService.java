package com.example.clase1.services;

import com.example.clase1.model.Producto;
import com.example.clase1.model.ProductoDTO;
import com.example.clase1.repository.ProductoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoDTOService {
    @Autowired
    private ProductoRepo productoRepo;
    public List<Object> getAllProductos(){
        List<Producto> productos=(List<Producto>) productoRepo.findAll();
        return productos.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private Object mapToDTO(Producto producto) {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setIdProducto(producto.getIdProducto());
        productoDTO.setNombre(producto.getNombre());
        productoDTO.setCodigoBarras(producto.getCodigoBarras());
        productoDTO.setIdCategoria(producto.getCategoria().getIdCategoria());
        productoDTO.setNombreCategoria(producto.getCategoria().getNomCategoria());
        return productoDTO;
    }
}