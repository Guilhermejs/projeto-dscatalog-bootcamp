package com.devsuperior.dscatalog.resources;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.services.ProductService;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductResource.class)
public class ProductResourceTests {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ProductService service;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private ResultActions result;
	private ProductDTO dto;
	private PageImpl<ProductDTO> page;
	private Long existsId;
	private Long nonExistsId;
	private Long dependentId;
	
	@BeforeEach
	void setUp() throws Exception {
		
		dto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(dto));
		existsId = 1L;
		nonExistsId = 1000L;
		dependentId = 10L;
		
		when(service.findAllPaged((ArgumentMatchers.any()))).thenReturn(page);
		
		when(service.findById(existsId)).thenReturn(dto);
		when(service.findById(nonExistsId)).thenThrow(ResourceNotFoundException.class);
		
		when(service.update(ArgumentMatchers.eq(existsId), ArgumentMatchers.any())).thenReturn(dto);
		when(service.update(ArgumentMatchers.eq(nonExistsId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);
		
		doNothing().when(service).delete(existsId);
		doThrow(ResourceNotFoundException.class).when(service).delete(nonExistsId);
		doThrow(DatabaseException.class).when(service).delete(dependentId);
		
		when(service.insert(ArgumentMatchers.any(ProductDTO.class))).thenReturn(dto);
		
	}
	
	@Test
	public void insertShouldReturnProductDto() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.post("/products")
						.content(jsonBody).contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isCreated());
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenIdDependent() throws Exception {
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", dependentId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isBadRequest());
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistsId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() throws Exception {
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", existsId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNoContent());
		result.andExpect(jsonPath("$.id").doesNotExist());
		result.andExpect(jsonPath("$.name").doesNotExist());
	}
	
	@Test
	public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistsId)
						.content(jsonBody).contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void updateShouldReturnProductDtoWhenIdExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existsId)
						.content(jsonBody).contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
		
	}
	
	@Test
	public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistsId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}
	
	@Test
	public void findByIdShouldReturnProductDtoWhenExistsId() throws Exception {
		
		result = 
				mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existsId)
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").exists());
		result.andExpect(jsonPath("$.description").exists());
	}
	
	@Test
	public void findAllShouldReturnPage() throws Exception {
		result = 
				mockMvc.perform(get("/products")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
	}
}
