package com.example.clase1.controller;

import com.example.clase1.model.Producto;
import com.example.clase1.services.ProductoServiceImp;
import com.example.clase1.services.ProductoDTOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ProductoController {

    @Autowired
    private ProductoServiceImp gatitos;

    @Autowired
    private ProductoDTOService productoDTOService;

    @GetMapping("/allDTO")
    public List<Object> getAllProductosDTO() {
        return productoDTOService.getAllProductos();
    }

    @PostMapping("/addProducto")
    public ResponseEntity<Producto> createProducto(@RequestBody Producto producto) {
        Producto createProducto = gatitos.addProducto(producto);
        return new ResponseEntity<>(createProducto, HttpStatus.CREATED);
    }

    @GetMapping("/listarProductos")
    public List<Producto> getAllProductos() {
        return (List<Producto>) gatitos.listarProductos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        Optional<Producto> producto = gatitos.productosPorId(id);
        return producto.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(@PathVariable Integer id, @RequestBody Producto producto) {
        Producto updateProducto = gatitos.actProducto(id, producto);
        return new ResponseEntity<>(updateProducto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProducto(@PathVariable Integer id) {
        boolean isDeleted = gatitos.remProducto(id);

        if (isDeleted) {
            return ResponseEntity.ok().body("Producto eliminado con éxito");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto con id " + id + " no encontrado");
        }
    }
}
