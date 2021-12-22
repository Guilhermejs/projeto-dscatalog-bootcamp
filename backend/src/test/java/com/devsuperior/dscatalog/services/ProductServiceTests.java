package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	private long existsId;
	private long nonExistsId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO dto;
	private Category category;
	
	@BeforeEach
	void setup() throws Exception {
		existsId = 1L;
		nonExistsId = 2L;
		dependentId = 3L;
		product = Factory.createProduct();
		dto = Factory.createProductDTO();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		Mockito.doNothing().when(repository).deleteById(existsId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistsId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		Mockito.when(repository.findById(existsId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(nonExistsId)).thenThrow(ResourceNotFoundException.class);
		Mockito.when(repository.getOne(existsId)).thenReturn(product);
		Mockito.when(repository.getOne(nonExistsId)).thenThrow(EntityNotFoundException.class);
		Mockito.when(catRepository.getOne(existsId)).thenReturn(category);
		Mockito.when(catRepository.getOne(nonExistsId)).thenThrow(EntityNotFoundException.class);
	}
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	 
	@Mock
	private CategoryRepository catRepository;
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
		
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existsId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existsId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistsId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistsId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		
		Assertions.assertThrows(DatabaseException.class, () -> { 
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
	
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenIdExists() { 
		
		ProductDTO result = service.findById(existsId);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getClass(), ProductDTO.class);
		
		Mockito.verify(repository).findById(existsId);
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistsId);
		});
		
		Mockito.verify(repository).findById(nonExistsId);
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		
		ProductDTO result = service.update(existsId, dto);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(result.getClass(), ProductDTO.class);
		
		Mockito.verify(repository).getOne(existsId);
		Mockito.verify(catRepository).getOne(existsId);
		Mockito.verify(repository).save(product);
	}
	
	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistsId, dto);
		});
	}
}
