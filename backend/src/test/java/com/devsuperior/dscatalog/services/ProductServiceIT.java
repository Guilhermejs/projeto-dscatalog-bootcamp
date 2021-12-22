package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@SpringBootTest
@Transactional
public class ProductServiceIT {
	
	@Autowired
	private ProductService service;
	
	@Autowired
	private ProductRepository repository;
	
	private Long idExist;
	private Long nonExist;
	private Long countProducts;
	
	@BeforeEach
	void setup() throws Exception {
		idExist = 1L;
		nonExist = 1000L;
		countProducts = 25L;
	}
	
	@Test
	public void findAllPagedShouldReturnSortedPageWhenSorteByName() {
		
		PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));
		
		Page<ProductDTO> page = service.findAllPaged(pageRequest);
		
		Assertions.assertEquals("Macbook Pro", page.getContent().get(0).getName());
		Assertions.assertEquals("PC Gamer", page.getContent().get(1).getName());
		Assertions.assertEquals("PC Gamer Alfa", page.getContent().get(2).getName());
		
	}
	
	@Test
	public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
		
		PageRequest pageRequest = PageRequest.of(50, 10);
		
		Page<ProductDTO> page = service.findAllPaged(pageRequest);
		
		Assertions.assertTrue(page.isEmpty());
		
	}
	
	@Test
	public void findAllPagedShouldReturnPageWhenPage0Size10() {
		
		PageRequest pageRequest = PageRequest.of(0, 10);
		
		Page<ProductDTO> page = service.findAllPaged(pageRequest);
		
		Assertions.assertNotNull(page);
		Assertions.assertFalse(page.isEmpty());
		Assertions.assertEquals(page.getSize(), pageRequest.getPageSize());
		Assertions.assertEquals(0, page.getNumber());
		Assertions.assertEquals(countProducts, page.getTotalElements());	
	}
	
	@Test
	public void deleteShouldDeleteResourceWhenIdExists() {
		
		service.delete(idExist);
		
		Assertions.assertEquals(countProducts - 1, repository.count());
		
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionceWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExist);
		});
	}
}
