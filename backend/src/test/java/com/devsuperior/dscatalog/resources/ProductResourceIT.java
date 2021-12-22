package com.devsuperior.dscatalog.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	private Long existsId;
	private Long nonExistsId;
	private Long dependentId;
	private Long countTotalElements;
	private ProductDTO dto;
	
	@BeforeEach
	void setup() throws Exception {
		existsId = 1L;
		nonExistsId = 1000L;
		dependentId = 5L;
		countTotalElements = 25L;
		dto = Factory.createProductDTO();
	}
	
	@Test
	public void findAllShouldReturnSortedPageWhenSortByName() throws Exception{
	
		ResultActions result = 
				mockMvc.perform(get("/products?page=0&size=12&sort=name,asc")
						.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.totalElements").value(countTotalElements));
		result.andExpect(jsonPath("$.content").exists());
		result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
		result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
		result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
	}
	
	@Test
	public void updateShouldReturnProductDTOWhenIdExists() throws Exception {
		
		String expectedName = dto.getName();
		String expectedDesctription = dto.getDescription();
		
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", existsId)
						.content(jsonBody).contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("$.id").exists());
		result.andExpect(jsonPath("$.name").value(expectedName));
		result.andExpect(jsonPath("$.description").value(expectedDesctription));
	}
	
	@Test
	public void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
		
		String jsonBody = objectMapper.writeValueAsString(dto);
		
		ResultActions result = 
				mockMvc.perform(put("/products/{id}", nonExistsId)
						.content(jsonBody).contentType(MediaType.APPLICATION_JSON)
							.accept(MediaType.APPLICATION_JSON));
		
		result.andExpect(status().isNotFound());
	}	
}
