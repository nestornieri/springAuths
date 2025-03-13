package com.example.clase1.repository;

import com.example.clase1.model.Producto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepo extends CrudRepository<Producto, Integer> {
}
