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
import com.scai.entities.response.DemandPlannerHierarchyResponse;
import com.scai.entities.response.PersonnelResponse;
import com.scai.entities.response.SalesmanHierarchyResponse;
import com.scai.entities.utilities.CSVUtilities;

@SpringBootTest
public class PersonnelServiceImplTest {

	@InjectMocks
	private PersonnelServiceImpl personnelServiceImpl;

	Object singleObject;

	private DemandPlannerHierarchyResponse demandPlannerHierarchyResponse;

	private SalesmanHierarchyResponse salesmanHierarchyResponse;

	private List<Object[]> objList;

	private PersonnelResponse personnelResponse;

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
		demandPlannerHierarchyResponse = getDemandPlannerHierarchyResponse();
		salesmanHierarchyResponse = getSalesmanHierarchyResponse();
		singleObject = new Integer(10);
		objList = getObjectList(3);
		personnelResponse = getPersonnelResponse();
	}

	@Test
	void testGetCSVDownload() {
		ByteArrayInputStream in = CSVUtilities.objectsToCSVCovertoer(personnelResponse.getListOfHearders(),
				personnelResponse.getListOfRowData());
		inputStreamResource = new InputStreamResource(in);
		when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
		when(query.setFirstResult(Mockito.anyInt())).thenReturn(query);
		when(query.setMaxResults(Mockito.anyInt())).thenReturn(query);
		when(query.getResultList()).thenReturn(objList);
		when(query.getSingleResult()).thenReturn(singleObject);
		InputStreamResource input = personnelServiceImpl.getCSVDownload(salesmanHierarchyResponse,
				demandPlannerHierarchyResponse, "tesTenantID");
		Assert.assertEquals(inputStreamResource.toString(), input.toString());
	}

	@Test
	void getCSVTemplateDownloadTest() {
		ByteArrayInputStream in = CSVUtilities.objectsToCSVCovertoer(personnelResponse.getListOfHearders(),
				personnelResponse.getListOfRowData());
		inputStreamResource = new InputStreamResource(in);
		when(entityManager.createNativeQuery(Mockito.anyString())).thenReturn(query);
		when(query.setFirstResult(Mockito.anyInt())).thenReturn(query);
		when(query.setMaxResults(Mockito.anyInt())).thenReturn(query);
		when(query.getResultList()).thenReturn(objList);
		when(query.getSingleResult()).thenReturn(singleObject);
		InputStreamResource input = personnelServiceImpl.getPersonnelTemplateDownload(demandPlannerHierarchyResponse,
				salesmanHierarchyResponse, "tesTenantID");
		Assert.assertEquals(inputStreamResource.toString(), input.toString());
	}

	private PersonnelResponse getPersonnelResponse() {
		List<Object[]> listOfRowData = getObjectList(3);
		PersonnelResponse personnelResponse = new PersonnelResponse();
		personnelResponse.setPageIndex(1);
		personnelResponse.setPageSize(10);
		personnelResponse.setTotalElements(10);
		personnelResponse.setTotalPages(5);
		personnelResponse.setListOfHearders(Arrays.asList("Header1", "Header2", "Header3"));
		personnelResponse.setListOfRowData(listOfRowData);
		return personnelResponse;
	}

	private List<Object[]> getObjectList(int n) {
		List<Object[]> objList = new ArrayList<Object[]>();

		for (int i = 1; i <= n; i++) {
			objList.add(getObject(i));
		}
		return objList;
	}

	private DemandPlannerHierarchyResponse getDemandPlannerHierarchyResponse() {
		DemandPlannerHierarchyResponse demandPlannerHierarchyResponse = new DemandPlannerHierarchyResponse();
		demandPlannerHierarchyResponse.setNoOfLevels(3);
		demandPlannerHierarchyResponse.setDemandsHierarchyLevelMap(getTestMap());
		return demandPlannerHierarchyResponse;
	}

	private SalesmanHierarchyResponse getSalesmanHierarchyResponse() {
		SalesmanHierarchyResponse salesmanHierarchyResponse = new SalesmanHierarchyResponse();
		salesmanHierarchyResponse.setNoOfLevels(3);
		salesmanHierarchyResponse.setSalesmanHierarchyLevelMap(getTestMap());
		return salesmanHierarchyResponse;
	}

	private Object[] getObject(int i) {
		Object[] ar1 = { i, "Test_d" + i, "Test_n" + i, "Test_l" + i, "10" + i, "lays" + i, "chips" + i, "test" + i };
		return ar1;
	}

	private Map<String, String> getTestMap() {
		Map<String, String> testMap = new LinkedHashMap<>();
		testMap.put("level1", "level1");
		testMap.put("level2", "level2");
		testMap.put("level3", "level3");
		return testMap;
	}

}
