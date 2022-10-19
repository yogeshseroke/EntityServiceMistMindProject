package com.scai.entities.service.impl;

import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;

import com.scai.entities.response.ProductHierarchyResponse;
import com.scai.entities.response.ProductResponse;
import com.scai.entities.utilities.CSVUtilities;

@SpringBootTest
public class ProductServiceImplTest {

	@InjectMocks
	private ProductServiceImpl productService;

	Object singleObject;

	private ProductHierarchyResponse productHierarchyResponse;

	private List<Object[]> objList;

	private ProductResponse productResponse;

	private InputStreamResource inputStreamResource;

	@InjectMocks
	private CSVUtilities csvDownload;

	@Mock
	private EntityManager entityManager;

	@Mock
	Query query;

	@BeforeEach
	public void setup() {

		query = Mockito.mock(Query.class);
		productHierarchyResponse = getProductHierarchyResponse();
		singleObject = (Object) 10;
		objList = getObjectList(3);
		productResponse = getproductResponse();
	}

	@Test
	void testGetCSVDownload() {
		ByteArrayInputStream in = CSVUtilities.objectsToCSVCovertoer(productResponse.getListOfHearders(),
				productResponse.getListOfRowData());
		inputStreamResource = new InputStreamResource(in);
		when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
		when(query.setFirstResult(Mockito.anyInt())).thenReturn(query);
		when(query.setMaxResults(Mockito.anyInt())).thenReturn(query);
		when(query.getResultList()).thenReturn(objList);
		when(query.getSingleResult()).thenReturn(singleObject);
		InputStreamResource input = productService.getCSVDownload(productHierarchyResponse, "tenantId");
		Assert.assertEquals(inputStreamResource.toString(), input.toString());
	} 

	@Test
	void testGetCSVTemplateDownload() {
		ByteArrayInputStream in = CSVUtilities.objectsToCSVCovertoer(productResponse.getListOfHearders(),
				productResponse.getListOfRowData());
		inputStreamResource = new InputStreamResource(in);
		when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
		when(query.setFirstResult(Mockito.anyInt())).thenReturn(query);
		when(query.setMaxResults(Mockito.anyInt())).thenReturn(query);
		when(query.getResultList()).thenReturn(objList);
		when(query.getSingleResult()).thenReturn(singleObject);
		InputStreamResource input = productService.getCSVTemplateDownload(productHierarchyResponse, "testTenantID");
		Assert.assertEquals(inputStreamResource.toString(), input.toString());
	}

	private ProductResponse getproductResponse() {
		List<Object[]> listOfRowData = getListOfRowData();
		ProductResponse productResponse = new ProductResponse();
		productResponse.setPageIndex(1);
		productResponse.setPageSize(10);
		productResponse.setTotalElements(10);
		productResponse.setTotalPages(5);
		productResponse.setListOfHearders(Arrays.asList("Header1", "Header2", "Header3"));
		productResponse.setListOfRowData(listOfRowData);
		return productResponse;
	}

	private List<Object[]> getListOfRowData() {
		List<Object[]> listOfRowData = new ArrayList<Object[]>();
		Object[] objAr1 = { 1, "Test_d1", "Test_n1", "Test_l1", "101", "lays", "chips", "test" };
		Object[] objAr2 = { 2, "Test_d2", "Test_n2", "Test_l2", "102", "coke", "cold drink", "test" };
		listOfRowData.add(objAr1);
		listOfRowData.add(objAr2);
		return listOfRowData;
	}

	private List<Object[]> getObjectList(int n) {
		List<Object[]> objList = new ArrayList<Object[]>();

		for (int i = 1; i <= n; i++) {
			objList.add(getObject(i));
		}
		return objList;
	}

	private ProductHierarchyResponse getProductHierarchyResponse() {
		ProductHierarchyResponse productHierarchyResponse = new ProductHierarchyResponse();
		productHierarchyResponse.setNoOfLevels(3);
		productHierarchyResponse.setProdductHierarchyLevelMap(getListMap());
		return productHierarchyResponse;
	}

	private Map<String, String> getListMap() {
		Map<String, String> levelMap = new LinkedHashMap<>();
		levelMap.put("1", "1");
		levelMap.put("2", "2");
		levelMap.put("3", "3");
		return levelMap;
	}

	private Object[] getObject(int i) {
		Object[] ar1 = { i, "Test_d" + i, "Test_n" + i, "Test_l" + i, "10" + i, "lays" + i, "chips" + i, "test" + i };
		return ar1;
	}
}
