package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {
	
	@Autowired
	private ProductRepository repository;
	private Product product;
	private Optional<Product> obj;
	
	private long existingId;
	private long unexistingId;
	private int countTotalProducts;
	
	@BeforeEach
	void setUp() throws Exception {
		existingId = 1L;
		unexistingId = 26L;
		countTotalProducts = 25;
		product = new Product();
		obj = null;
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExists() {
		
		 repository.deleteById(existingId);
		
		 Optional<Product> result = repository.findById(existingId);
		 Assertions.assertTrue(result.isEmpty());	 
	}
	
	@Test
	public void deleteShouldThrowEmptyResultDataAccessExceptionWhenNoExistsId() {
		
		 Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
		 	repository.deleteById(unexistingId);
		 }); 
	}
	
	@Test
	public void insertShouldPersistWithAutoincrementWhenIdIsNull() {
		
		product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}
	
	@Test
	public void findByIdShouldReturnNoEmptyOptionalWhenIdExists() {
		
		obj = repository.findById(existingId);
		
		Assertions.assertTrue(obj.isPresent());
		//Assertions.assertNotNull(obj);
		Assertions.assertEquals(existingId, obj.get().getId());
	}
	
	@Test
	public void findByIdShouldEmptyOptionalWhenIdExists() {
		
		obj = repository.findById(unexistingId);
		
		Assertions.assertTrue(obj.isEmpty());
	}
}