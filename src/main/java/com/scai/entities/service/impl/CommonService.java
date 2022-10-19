package com.scai.entities.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.scai.entities.request.PersonnelRequest;
import com.scai.entities.request.ProductRequest;
import com.scai.entities.response.DemandPlannerHierarchyResponse;
import com.scai.entities.response.ProductHierarchyResponse;
import com.scai.entities.response.SalesmanHierarchyResponse;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class CommonService {
	//for personnel
	private String FIND_ALL_PERSONNEL_QUERY = " FROM crosstab('select * from (select P.personnel_id, P.sku_code, P.location_code, P.sku_name, P.location_name, SH.level, SH.value  from personnel P JOIN public.salesman_hierarchy SH ON SH.personnel_id = P.personnel_id Where P.tenant_id like :tenantId  UNION ALL select P.personnel_id, P.sku_code, P.location_code, P.sku_name, P.location_name, DH.level, DH.value  from personnel P JOIN public.demandplanner_hierarchy DH ON DH.personnel_id = P.personnel_id Where P.tenant_id like :tenantId) FINAL_TABLE :singlePersonnelId ORDER BY personnel_id ASC', '(SELECT DISTINCT SH.level FROM salesman_hierarchy SH order by sh.level ASC) UNION ALL (SELECT DISTINCT DH.level FROM demandplanner_hierarchy DH order by DH.level ASC)') AS ";

	private String PERSONNEL_HEARDER_SORTING_VALUE = "(Personnel_Id text, Sku_Code text, Location_Code text, Sku_Name text, Location_Name text, :salesmanHierarchyString, :demandPlannerHierarchyString) :searchString :sortingString";

	private String LIST_OF_MAIN_HEADERS = "SKU Name, Location Name, SKU Code, Location Code";
	
	//for products
	private String FIND_ALL_PRODUCTS_QUERY = " FROM crosstab( 'SELECT S.sku_id, S.sku_code, S.location_code, S.sku_name, S.location_name, S.sale_price_unit, sh.level, sh.value from public.SKU S join public.SKU_Hierarchy SH on S.SKU_ID = SH.SKU_ID where S.tenant_id like :tenantId :singleProductId order by S.sku_id ASC', 'SELECT DISTINCT SH.level FROM SKU_Hierarchy SH order by sh.level ASC') AS ";

	private String PRODUCTS_HEARDER_SORTING_VALUE = "(Sku_Id text, Sku_Code text, Location_Code text, Sku_Name text, Location_Name text, Sale_Price_Unit text, :productHierarchyString) :searchString :sortingString";

	//private String LIST_OF_MAIN_HEADERS = "SKU Name, Location Name, SKU Code, Location Code, Sale Price Unit";

	private List<Integer> removeIndexAndAddLast = Arrays.asList(3, 4);


	
	
	public String getListOfHeaderString(PersonnelRequest personnelRequest,
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
				.collect(Collectors.joining(" text,")) + " text";

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
	
	public String getListOfHeaderString(ProductRequest productRequest,
			ProductHierarchyResponse productHierarchyResponse) {
		log.debug("Entities_Service : : : : > Product Request : " + productRequest.toString());
		log.debug("Entities_Service : : : : >  Product Hierarchy Response : " + productHierarchyResponse.toString());
		productRequest = updatePageIndex(productRequest);
		String productHierarchyString = getLevelListOnNoOfLevels(productHierarchyResponse.getNoOfLevels()).stream()
				.map(Object::toString).collect(Collectors.joining(" text,")) + " text";
		log.debug("Entities_Service : : : : > Product Hierarchy String Value : " + productHierarchyString);
		String listOfHeaderString = new StringBuilder().append(PRODUCTS_HEARDER_SORTING_VALUE
				.replace(":productHierarchyString", productHierarchyString).replace(":sortingString", "")).toString();
		log.debug("Entities_Service : : : : >  Header Name  : " + listOfHeaderString);
		return listOfHeaderString;
	}
	
	public List<String> getListofHeader(String listOfHeaderString, PersonnelRequest personnelRequest) {
		List<String> listOfData = Arrays.asList(
				listOfHeaderString.replace("ORDER BY " + personnelRequest.getSortFieldStringWithASCOrDESC().trim(), "")
						.replace(":searchString", "").replace("(", "").replace(")", "").replace("int", "")
						.replaceAll("text", "").split("\\s*,\\s*"));
		listOfData.set(listOfData.size() - 1, listOfData.get(listOfData.size() - 1).trim());
		return listOfData;
	}
	
	public List<String> getListofHeader(String listOfHeaderString, ProductRequest productRequest) {
		List<String> listOfData = Arrays.asList(
				listOfHeaderString.replace("ORDER BY " + productRequest.getSortFieldStringWithASCOrDESC().trim(), "")
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
	
	private ProductRequest updatePageIndex(ProductRequest productRequest) {
		int pageIndex = productRequest.getPageIndex() - 1;
		if (productRequest.getProductId() > 0) {
			productRequest.setPageIndex(pageIndex);
		} else {
			productRequest.setPageIndex(0);
			productRequest.setPageSize(Integer.MAX_VALUE);
		}
		return productRequest; 
	}
	 
	public List<String> changelistOfHeaderForCustomerView(List<String> listOfHeader, List<String> listOfKeyOrValues,
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
	
	public List<String> changelistOfHeaderForCustomerView(List<String> listOfHeader, List<String> listOfKeyOrValues,
			String keyOrValue) {
		String headerCommaSeparated = "";

		if (keyOrValue.equals("key")) {
			headerCommaSeparated = listOfHeader.stream().collect(Collectors.joining(","));
		} else {
			headerCommaSeparated = listOfHeader.stream().map(header -> header.replace("_", " "))
					.collect(Collectors.joining(","));
		}
		List<String> levelKeyList = listOfKeyOrValues;
		for (int i = 1; i <= levelKeyList.size(); i++) {
			headerCommaSeparated = headerCommaSeparated.replace("\"" + i + "\"", levelKeyList.get(i - 1));
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

//	public String getHierarchyKey(Map<?, ?> hierarchyRequest, String filterValue) {
//		List<String> productKey = new ArrayList(hierarchyRequest.keySet());
//		for (int i = 0; i < productKey.size(); i++) {
//			if (productKey.get(i).contains(filterValue)) {
//				return productKey.get(i);
//			}
//		}
//		return "";
//	}
	
	public List<String> getModifiedHeaderWithKeyOrVlaue(List<String> listOfHeader,
			SalesmanHierarchyResponse salesmanHierarchyResponse,
			DemandPlannerHierarchyResponse demandPlannerHierarchyResponse, String actionName) {
		List<String> modifiedHeaderLIst = new ArrayList<>();
		if (actionName.equals("forGet")) {
			modifiedHeaderLIst = changelistOfHeaderForCustomerView(listOfHeader,
					getLevelKeys(salesmanHierarchyResponse.getSalesmanHierarchyLevelMap()), "key", "");
			modifiedHeaderLIst = changelistOfHeaderForCustomerView(modifiedHeaderLIst,
					getLevelKeys(demandPlannerHierarchyResponse.getDemandsHierarchyLevelMap()), "key", "");
		} else {
			modifiedHeaderLIst = changelistOfHeaderForCustomerView(listOfHeader,
					getLevelKeys(salesmanHierarchyResponse.getSalesmanHierarchyLevelMap()), "value", "s");
			modifiedHeaderLIst = changelistOfHeaderForCustomerView(modifiedHeaderLIst,
					getLevelKeys(demandPlannerHierarchyResponse.getDemandsHierarchyLevelMap()), "value", "d");
		}
		return modifiedHeaderLIst;
	}
	
	public List<String> getModifiedHeaderWithKeyOrVlaue(List<String> listOfHeader,
			ProductHierarchyResponse productHierarchyResponse, String actionName) {
		List<String> modifiedHeaderLIst = new ArrayList<>();
		if (actionName.equals("forGet")) {
			modifiedHeaderLIst = changelistOfHeaderForCustomerView(listOfHeader,
					getLevelKeys(productHierarchyResponse.getProdductHierarchyLevelMap()), "key");
		} else {
			modifiedHeaderLIst = changelistOfHeaderForCustomerView(listOfHeader,
					getLevelValues(productHierarchyResponse.getProdductHierarchyLevelMap()), "value");
		}
		return modifiedHeaderLIst;
	}
	
	public String updatingQueryWithSearch(String searchValue, String listOfHeaderString) {
		if (!searchValue.isEmpty()) {
			String searchString = updatedSearchString(searchValue, listOfHeaderString);
			listOfHeaderString = listOfHeaderString.replace(":searchString", searchString);
		} else {
			listOfHeaderString = listOfHeaderString.replace(":searchString", "");
		}
		return listOfHeaderString;
	} 
	
	private String updatedSearchString(String searchValue, String listOfHeaderString) {
		String likeValue = " ILIKE '%" + searchValue;
		String whereValue = "WHERE ";
		return new StringBuilder().append(whereValue).append(listOfHeaderString.replace(":searchString", "")
				.replace("(", "").replace(")", "").replaceAll(",", " OR ").replaceAll("text", likeValue + "%'"))
				.toString();
	}
	
	 
	
	public List<String> getLevelValues(Map<String, String> hierarchyMap) {
		return new ArrayList<>(hierarchyMap.values());
	}
	
	public List<String> getLevelKeys(Map<String, String> hierarchyMap) {
		return new ArrayList<>(hierarchyMap.keySet());
	}
	
	public List<String> getLevelListOnNoOfLevelsWithValue(int noOfLevels, String appendString) {
		List<String> listLevels = new ArrayList<>();
		for (int i = 1; i <= noOfLevels; i++) {
			listLevels.add("\"" + appendString + String.valueOf(i) + "\"");
		}
		return listLevels;
	}
	
	public List<String> getLevelListOnNoOfLevels(int noOfLevels) {
		List<String> listLevels = new ArrayList<>();
		for (int i = 1; i <= noOfLevels; i++) {
			listLevels.add("\"" + String.valueOf(i) + "\"");
		}
		return listLevels;
	}
	
	
}
