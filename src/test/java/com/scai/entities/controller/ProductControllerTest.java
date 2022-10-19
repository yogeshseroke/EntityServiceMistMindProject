package com.scai.entities.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.scai.entities.response.ProductHierarchyResponse;
import com.scai.entities.service.ProductService;

public class ProductControllerTest {

	@Mock
	ProductService productService;

	@Mock
	ProductHierarchyResponse productHierarchyResponse;

	@InjectMocks
	ProductController productController;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testDownloadTemplate() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		String fileContent = "testDownloadTemplate";
		InputStreamResource getInputStreamResource = getInputStreamResourceOnInput(fileContent);
		when(productService.getCSVTemplateDownload(any(ProductHierarchyResponse.class), any(String.class)))
				.thenReturn(getInputStreamResource);
		ResponseEntity<InputStreamResource> receivedResponse = productController.downloadTemplate(any(String.class));
		String receivedFileContent = new String(receivedResponse.getBody().getInputStream().readAllBytes(),
				StandardCharsets.UTF_8);
		assertThat(receivedResponse.getStatusCode().value()).isEqualTo(200);
		assertThat(new String(receivedFileContent)).isEqualTo(fileContent);

	}

	@Test
	public void testDownloadCSV() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		String fileContent = "testDownloadCSV";
		InputStreamResource getInputStreamResource = getInputStreamResourceOnInput(fileContent);
		when(productService.getCSVDownload(any(ProductHierarchyResponse.class), any(String.class)))
				.thenReturn(getInputStreamResource);
		ResponseEntity<InputStreamResource> receivedResponse = productController.downloadTemplate(any(String.class));
		String receivedFileContent = new String(receivedResponse.getBody().getInputStream().readAllBytes(),
				StandardCharsets.UTF_8);
		assertThat(receivedResponse.getStatusCode().value()).isEqualTo(200);
		assertThat(new String(receivedFileContent)).isEqualTo(fileContent);
	}

	private InputStreamResource getInputStreamResourceOnInput(String testString) {
		InputStream is = new ByteArrayInputStream(testString.getBytes());
		return new InputStreamResource(is);
	} 
}
