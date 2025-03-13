package com.example.clase1.services;

import com.example.clase1.model.Producto;
import com.example.clase1.repository.ProductoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class ProductoServiceImp implements ProductoService {

    @Autowired
    private ProductoRepo animalillo;

    @Transactional(readOnly = true)
    @Override
    public Collection<Producto> listarProductos() {
        return (Collection<Producto>) animalillo.findAll();
    }

    @Override
    public Optional<Producto> productosPorId(Integer id) {
        return animalillo.findById(id);
    }

    @Override
    public Producto addProducto(Producto producto) {
        return animalillo.save(producto);
    }

    @Override
    public Producto actProducto(Integer id, Producto producto) {
        Optional<Producto> existingProducto = animalillo.findById(id);
        if (existingProducto.isPresent()) {
            Producto updateProducto = existingProducto.get();
            updateProducto.setNombre(producto.getNombre());
            //updateProducto.setIdCategoria(producto.getIdCategoria());
            updateProducto.setCodigoBarras(producto.getCodigoBarras());
            updateProducto.setPrecioVenta(producto.getPrecioVenta());
            updateProducto.setCantidadStock(producto.getCantidadStock());
            updateProducto.setEstado(producto.getEstado());
            return animalillo.save(updateProducto);
        } else {
            throw new RuntimeException("El Producto no Existe: " + id);
        }
    }

    @Override
    public boolean remProducto(Integer id) {
        Optional<Producto> producto = animalillo.findById(id);
        if (producto.isPresent()) {
            animalillo.delete(producto.get());
            return true;
        } else {
            return false;
        }
    }
}
