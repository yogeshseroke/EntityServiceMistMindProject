package com.scai.entities.service.impl;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import com.scai.entities.request.PersonnelRequest;
import com.scai.entities.response.DemandPlannerHierarchyResponse;
import com.scai.entities.response.PersonnelResponse;
import com.scai.entities.response.SalesmanHierarchyResponse;
import com.scai.entities.service.PersonnelService;
import com.scai.entities.utilities.CSVUtilities;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class PersonnelServiceImpl implements PersonnelService {

	private String FIND_ALL_PERSONNEL_QUERY = " FROM crosstab('select * from (select P.personnel_id, P.sku_code, P.location_code, P.sku_name, P.location_name, SH.level, SH.value  from personnel P JOIN public.salesman_hierarchy SH ON SH.personnel_id = P.personnel_id Where P.tenant_id like :tenantId  UNION ALL select P.personnel_id, P.sku_code, P.location_code, P.sku_name, P.location_name, DH.level, DH.value  from personnel P JOIN public.demandplanner_hierarchy DH ON DH.personnel_id = P.personnel_id Where P.tenant_id like :tenantId) FINAL_TABLE :singlePersonnelId ORDER BY personnel_id ASC', '(SELECT DISTINCT SH.level FROM salesman_hierarchy SH order by sh.level ASC) UNION ALL (SELECT DISTINCT DH.level FROM demandplanner_hierarchy DH order by DH.level ASC)') AS ";

	private String PERSONNEL_HEARDER_SORTING_VALUE = "(Personnel_Id text, Sku_Code text, Location_Code text, Sku_Name text, Location_Name text, :salesmanHierarchyString, :demandPlannerHierarchyString) :searchString :sortingString";

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private CommonService commonService;

	@Override
	public SalesmanHierarchyResponse getSalesmanHierarchyResponse(Object salesmanHierarchyRequest) {
		SalesmanHierarchyResponse salesmanHierarchyResponse = new SalesmanHierarchyResponse();
		try {
			if (salesmanHierarchyRequest instanceof Map) {
				Map<?, ?> salesmanHierarchyMap = (Map<?, ?>) salesmanHierarchyRequest;
				int noOfLevels = (int) (salesmanHierarchyMap
						.get(commonService.getHierarchyKey(salesmanHierarchyMap, "NoOfLevels")));
				if (noOfLevels > 0) {
					Map<String, String> salesmanHierarchyMapOut = new LinkedHashMap<>();
					for (int i = 1; i <= noOfLevels; i++) {
						String key = commonService.getHierarchyKey(salesmanHierarchyMap, "Level" + i);
						String value = (String) (salesmanHierarchyMap.get(key));
						salesmanHierarchyMapOut.put(key, value);
					}
					salesmanHierarchyResponse.setNoOfLevels(noOfLevels);
					salesmanHierarchyResponse.setSalesmanHierarchyLevelMap(salesmanHierarchyMapOut);
				}
			}
		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getSalesmanHierarchyResponse : " + e.getMessage());
		}
		return salesmanHierarchyResponse;
	}

	@Override
	public DemandPlannerHierarchyResponse getDemandPlannerHierarchyResponse(Object demandPlannerHierarchyRequest) {
		DemandPlannerHierarchyResponse demandPlannerHierarchyResponse = new DemandPlannerHierarchyResponse();
		try {
			if (demandPlannerHierarchyRequest instanceof Map) {
				Map<?, ?> demandHierarchyMap = (Map<?, ?>) demandPlannerHierarchyRequest;
				int noOfLevels = (int) (demandHierarchyMap
						.get(commonService.getHierarchyKey(demandHierarchyMap, "NoOfLevels")));
				if (noOfLevels > 0) {
					Map<String, String> demandHierarchyMapOut = new LinkedHashMap<>();
					for (int i = 1; i <= noOfLevels; i++) {
						String key = commonService.getHierarchyKey(demandHierarchyMap, "Level" + i);
						String value = (String) (demandHierarchyMap.get(key));
						demandHierarchyMapOut.put(key, value);
					}
					demandPlannerHierarchyResponse.setNoOfLevels(noOfLevels);
					demandPlannerHierarchyResponse.setDemandsHierarchyLevelMap(demandHierarchyMapOut);
				}
			}
		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getDemandPlannerHierarchyResponse : " + e.getMessage());
		}
		return demandPlannerHierarchyResponse;
	}

	@Override
	public InputStreamResource getCSVDownload(SalesmanHierarchyResponse salesmanHierarchyResponse,
			DemandPlannerHierarchyResponse demandPlannerHierarchyResponse, String tenantId) {

		log.info("Entities_Service : : : : >  getCSVDownload()");
		log.debug("Entities_Service : : : : > Salesman Hierarchy Response : " + salesmanHierarchyResponse.toString());
		log.debug("Entities_Service : : : : > Demand Planner Hierarchy Response : "
				+ demandPlannerHierarchyResponse.toString());
		ByteArrayInputStream in = null;
		try {
			PersonnelResponse personnelResponse = getAllPersonnels(createPersonnelRequest(0, 0, "", "", 0, tenantId),
					salesmanHierarchyResponse, demandPlannerHierarchyResponse, "forDownload");
			log.debug("Entities_Service : : : : > Personnel Response : " + personnelResponse.toString() + " --- ");
			List<String> downloadHeaders = commonService.changelistOfHeaderForCustomerView(
					personnelResponse.getListOfHearders(),
					commonService.getLevelValues(salesmanHierarchyResponse.getSalesmanHierarchyLevelMap()), "value",
					"SalesmanHierarchyLevel");
			downloadHeaders = commonService.changelistOfHeaderForCustomerView(downloadHeaders,
					commonService.getLevelValues(demandPlannerHierarchyResponse.getDemandsHierarchyLevelMap()), "value",
					"DemandPlannerHierarchyLevel");
			in = CSVUtilities.objectsToCSVCovertoer(downloadHeaders, personnelResponse.getListOfRowData());

		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getCSVDownload : " + e.getMessage());
			throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
		}
		return new InputStreamResource(in);
	}

	/*
	 * Method to fetch all the products exist in the Database.
	 */
	public PersonnelResponse getAllPersonnels(PersonnelRequest personnelRequest,
			SalesmanHierarchyResponse salesmanHierarchyResponse,
			DemandPlannerHierarchyResponse demandPlannerHierarchyResponse, String actionName) {
		log.info("Entities_Service : : : : > getAllPersonnels() ");
		log.debug("Entities_Service : : : : > Salesman Hierarchy Response Parameters : "
				+ salesmanHierarchyResponse.toString());
		log.debug("Entities_Service : : : : > Demand Planner Hierarchy Response Parameters : "
				+ demandPlannerHierarchyResponse.toString());
		String listOfHeaderString = commonService.getListOfHeaderString(personnelRequest, salesmanHierarchyResponse,
				demandPlannerHierarchyResponse);
		log.debug("Entities_Service : : : : > Header Name : " + listOfHeaderString);
		List<String> listOfHeader = commonService.getListofHeader(listOfHeaderString, personnelRequest);
		log.debug("Entities_Service : : : : > List of Headers : " + listOfHeader);
		String listOfHeaderStringWithSearch = commonService.updatingQueryWithSearch(personnelRequest.getSearchValue(),
				listOfHeaderString);
		String getAllNativeQuery = finalModifiedGetAllQueryWithPersonnelID("SELECT *", listOfHeaderStringWithSearch,
				personnelRequest);
		log.debug("Entities_Service : : : : >  SQL Native Query : " + getAllNativeQuery);

		List<Object[]> allPersonnels = getAllPersonnels(getAllNativeQuery, personnelRequest);

		listOfHeaderStringWithSearch = listOfHeaderStringWithSearch
				.replace("ORDER BY " + personnelRequest.getSortFieldStringWithASCOrDESC().trim(), "");
		int totalElements = getAllPersonnelsCount(finalModifiedGetAllQueryWithPersonnelID("SELECT COUNT(*)",
				listOfHeaderStringWithSearch, personnelRequest));
		log.debug("Entities_Service : : : : > Total Element Exist in the Database : " + totalElements);
		int pagesCount = totalElements / personnelRequest.getPageSize();
		int totalPages = totalElements % personnelRequest.getPageSize() == 0 ? pagesCount : pagesCount + 1;
		PersonnelResponse personnelResponse = updatingPersonnelResponse(
				commonService.getModifiedHeaderWithKeyOrVlaue(listOfHeader, salesmanHierarchyResponse,
						demandPlannerHierarchyResponse, actionName),
				allPersonnels, personnelRequest.getPageIndex() >= 0 ? personnelRequest.getPageIndex() + 1
						: personnelRequest.getPageIndex(),
				personnelRequest.getPageSize(), totalElements, totalPages);
		log.debug("Entities_Service : : : : >  Personnel Response Data : " + personnelResponse.toString());
		return personnelResponse;
	}

	private String getListOfHeaderString(PersonnelRequest personnelRequest,
			SalesmanHierarchyResponse salesmanHierarchyResponse,
			DemandPlannerHierarchyResponse demandPlannerHierarchyResponse) {
		log.info("Entities_Service : : : : > getListOfHeaderString()");
		log.debug("Entities_Service : : : : > Personnel Request : " + personnelRequest.toString() + "---");
		log.debug("Entities_Service : : : : > Salesman Hierarchy Response : " + salesmanHierarchyResponse.toString()
				+ "---");
		log.debug("Entities_Service : : : : >  Demand Planner Hierarchy Response : "
				+ demandPlannerHierarchyResponse.toString() + "---");

		personnelRequest = updatePageIndex(personnelRequest);

		String salesmanHierarchyString = getLevelListOnNoOfLevelsWithValue(salesmanHierarchyResponse.getNoOfLevels(),
				"s").stream().map(Object::toString).collect(Collectors.joining(" text,")) + " text";

		String demandPlannerHierarchyString = getLevelListOnNoOfLevelsWithValue(
				demandPlannerHierarchyResponse.getNoOfLevels(), "d").stream().map(Object::toString)
						.collect(Collectors.joining(" text,"))
				+ " text";

		log.debug("Entities_Service : : : : > Salesman Hierarchy String Value : " + salesmanHierarchyString);
		log.debug("Entities_Service : : : : > Demand Planner Hierarchy String Value : " + demandPlannerHierarchyString);

		String listOfHeaderString = new StringBuilder()
				.append(PERSONNEL_HEARDER_SORTING_VALUE
						.replace(":demandPlannerHierarchyString", demandPlannerHierarchyString)
						.replace(":salesmanHierarchyString", salesmanHierarchyString).replace(":sortingString", ""))
				.toString();
		log.debug("Entities_Service : : : : > Header Name  : " + listOfHeaderString);
		return listOfHeaderString;
	}

	public List<String> getLevelListOnNoOfLevelsWithValue(int noOfLevels, String appendString) {
		List<String> listLevels = new ArrayList<>();
		for (int i = 1; i <= noOfLevels; i++) {
			listLevels.add("\"" + appendString + String.valueOf(i) + "\"");
		}
		return listLevels;
	}

	/*
	 * Methods to get Headers for CSV file
	 */
	private List<String> getListofHeader(String listOfHeaderString, PersonnelRequest personnelRequest) {
		List<String> listOfData = Arrays.asList(
				listOfHeaderString.replace("ORDER BY " + personnelRequest.getSortFieldStringWithASCOrDESC().trim(), "")
						.replace(":searchString", "").replace("(", "").replace(")", "").replace("int", "")
						.replaceAll("text", "").split("\\s*,\\s*"));
		listOfData.set(listOfData.size() - 1, listOfData.get(listOfData.size() - 1).trim());
		return listOfData;
	}

	private PersonnelRequest updatePageIndex(PersonnelRequest personnelRequest) {
		int pageIndex = personnelRequest.getPageIndex() - 1;
		if (personnelRequest.getPersonnelId() > 0) {
			personnelRequest.setPageIndex(pageIndex);
		} else {
			personnelRequest.setPageIndex(0);
			personnelRequest.setPageSize(Integer.MAX_VALUE);
		}
		return personnelRequest;
	}

	/*
	 * Method to Create Product Request
	 */
	private PersonnelRequest createPersonnelRequest(int pageIndex, int pageSize, String sortFieldStringWithASCOrDESC,
			String searchValue) {
		log.info("Entities_Service : : : : > createPersonnelRequest()");
		PersonnelRequest personnelRequest = new PersonnelRequest();
		personnelRequest.setPageIndex(pageIndex);
		personnelRequest.setPageSize(pageSize);
		personnelRequest.setSortFieldStringWithASCOrDESC(sortFieldStringWithASCOrDESC);
		personnelRequest.setSearchValue(searchValue);
		log.debug("Entities_Service : : : : > Personnel Request Parameters : " + personnelRequest.toString() + "----");
		return personnelRequest;
	}

	/*
	 * Method to Download CSV Template
	 */
	public InputStreamResource getPersonnelTemplateDownload(
			DemandPlannerHierarchyResponse demandPlannerHierarchyResponse,
			SalesmanHierarchyResponse salesmanHierarchyResponse, String tenantId) {

		log.info("Entities_Service : : : : > getCSVTemplateDownload() ");
		log.debug("Entities_Service : : : : >  Demand Planner Hierarchy Response : "
				+ demandPlannerHierarchyResponse.toString());
		log.debug("Entities_Service : : : : >  Salesman Hierarchy Response : " + salesmanHierarchyResponse.toString());
		ByteArrayInputStream in = null;
		try {
			PersonnelRequest personnelRequest = createPersonnelRequest(0, 0, "", "");
			List<Object[]> rowData = new ArrayList<>();
			String listOfHeaderString = getListOfHeaderString(personnelRequest, salesmanHierarchyResponse,
					demandPlannerHierarchyResponse);
			log.debug("Entities_Service : : : : > Header_Name : " + listOfHeaderString + "---");
			List<String> listOfHeader = getListofHeader(listOfHeaderString, personnelRequest);

			listOfHeader = changelistOfHeaderForCustomerView(listOfHeader,
					getLevelValues(salesmanHierarchyResponse.getSalesmanHierarchyLevelMap()), "value",
					"SalesmanHierarchyLevel");

			listOfHeader = changelistOfHeaderForCustomerView(listOfHeader,
					getLevelValues(demandPlannerHierarchyResponse.getDemandsHierarchyLevelMap()), "value",
					"DemandPlannerHierarchyLevel");

			log.debug("Entities_Service : : : : >  List Of Headers : " + listOfHeaderString + "---");
			in = CSVUtilities.objectsToCSVCovertoer(listOfHeader, rowData);

		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getCSVTemplateDownload() " + e.getMessage());
			throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
		}
		return new InputStreamResource(in);
	}

	private List<String> changelistOfHeaderForCustomerView(List<String> listOfHeader, List<String> listOfKeyOrValues,
			String keyOrValue, String replaceValue) {
		String headerCommaSeparated = "";

		if (keyOrValue.equals("key")) {
			headerCommaSeparated = listOfHeader.stream().collect(Collectors.joining(","));
		} else {
			headerCommaSeparated = listOfHeader.stream().map(header -> header.replace("_", " "))
					.collect(Collectors.joining(","));
		}
		List<String> levelKeyList = listOfKeyOrValues;
		for (int i = 1; i <= levelKeyList.size(); i++) {
			if (keyOrValue.equals("key")) {
				headerCommaSeparated = headerCommaSeparated.replace("\"" + replaceValue + i + "\"",
						levelKeyList.get(i - 1));
			} else {
				headerCommaSeparated = headerCommaSeparated.replace(replaceValue + i, levelKeyList.get(i - 1));
			}
		}
		return Arrays.asList(headerCommaSeparated.split("\\s*,\\s*"));

	}

	public String getHierarchyKey(Map<?, ?> hierarchyRequest, String filterValue) {
		List<String> Keys = new ArrayList(hierarchyRequest.keySet());
		for (int i = 0; i < Keys.size(); i++) {
			if (Keys.get(i).contains(filterValue)) {
				return Keys.get(i);
			}
		}
		return "";
	}

	/*
	 * Method to Create Product Request
	 */
	private PersonnelRequest createPersonnelRequest(int pageIndex, int pageSize, String sortFieldStringWithASCOrDESC,
			String searchValue, long personnelId, String tenantId) {
		log.info("Entities_Service : : : : > createProductRequest - Creating Product Request ");
		PersonnelRequest personnelRequest = new PersonnelRequest();
		personnelRequest.setPageIndex(pageIndex);
		personnelRequest.setPageSize(pageSize);
		personnelRequest.setSortFieldStringWithASCOrDESC(sortFieldStringWithASCOrDESC);
		personnelRequest.setSearchValue(searchValue);
		personnelRequest.setPersonnelId(personnelId);
		personnelRequest.setTenantId(tenantId);
		log.debug("Entities_Service : : : : > Personnel Request Parameters : " + personnelRequest.toString());

		return personnelRequest;
	}

	private String finalModifiedGetAllQueryWithPersonnelID(String actionValue, String listOfHeaderString,
			PersonnelRequest personnelRequest) {

		String modifiedString = new StringBuilder().append(actionValue)
				.append(FIND_ALL_PERSONNEL_QUERY.replaceAll(":tenantId", "''" + personnelRequest.getTenantId() + "''"))
				.append(listOfHeaderString).toString();
		if (personnelRequest.getPersonnelId() > 0) {
			modifiedString = modifiedString.replace(":singlePersonnelId",
					"AND personnel_id = " + personnelRequest.getPersonnelId());
		} else {
			modifiedString = modifiedString.replace(":singlePersonnelId", "");
		}
		return modifiedString;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getAllPersonnels(String getAllPersonnelQuery, PersonnelRequest personnelRequest) {

		return entityManager.createNativeQuery(getAllPersonnelQuery)
				.setFirstResult(personnelRequest.getPageIndex() * personnelRequest.getPageSize())
				.setMaxResults(personnelRequest.getPageSize()).getResultList();
	}

	/*
	 * Method to get Total Number of Personnel Exist
	 */
	private int getAllPersonnelsCount(String getAllPersonnelsquery) {
		log.info("Entities_Service : : : : > getAllPersonnelsCount - Getting all the Personnel Count");
		return Integer.parseInt(entityManager.createNativeQuery(getAllPersonnelsquery).getSingleResult().toString());
	}

	private PersonnelResponse updatingPersonnelResponse(List<String> headers, List<Object[]> rowData, int pageIndex,
			int pageSize, int totalElements, int totalPages) {
		PersonnelResponse personnelResponse = new PersonnelResponse();
		personnelResponse.setListOfHearders(headers);
		personnelResponse.setListOfRowData(rowData);
		personnelResponse.setPageIndex(pageIndex);
		personnelResponse.setPageSize(pageSize);
		personnelResponse.setTotalElements(totalElements);
		personnelResponse.setTotalPages(totalPages);
		return personnelResponse;

	}

	public List<String> getLevelValues(Map<String, String> hierarchyMap) {
		return new ArrayList<>(hierarchyMap.values());
	}

	public List<String> getLevelKeys(Map<String, String> hierarchyMap) {
		return new ArrayList<>(hierarchyMap.keySet());
	}

}
