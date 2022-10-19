package com.scai.entities.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.scai.entities.model.Sku;
import com.scai.entities.model.SkuHierarchy;
import com.scai.entities.repository.SkuRepository;
import com.scai.entities.request.ProductRequest;
import com.scai.entities.response.CSVUploadResponse;
import com.scai.entities.response.ProductHierarchyResponse;
import com.scai.entities.response.ProductResponse;
import com.scai.entities.service.ProductService;
import com.scai.entities.utilities.CSVUtilities;
import com.scai.entities.utilities.CommonUtilites;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	SkuRepository skuRepository;

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private CommonService commonService;

	private String FIND_ALL_PRODUCTS_QUERY = " FROM crosstab( 'SELECT S.sku_id, S.sku_code, S.location_code, S.sku_name, S.location_name, S.sale_price_unit, sh.level, sh.value from public.SKU S join public.SKU_Hierarchy SH on S.SKU_ID = SH.SKU_ID where S.tenant_id like :tenantId :singleProductId order by S.sku_id ASC', 'SELECT DISTINCT SH.level FROM SKU_Hierarchy SH order by sh.level ASC') AS ";

	private String PRODUCTS_HEARDER_SORTING_VALUE = "(Sku_Id text, Sku_Code text, Location_Code text, Sku_Name text, Location_Name text, Sale_Price_Unit text, :productHierarchyString) :searchString :sortingString";

	private String LIST_OF_MAIN_HEADERS = "SKU Name, Location Name, SKU Code, Location Code, Sale Price Unit";

	private List<Integer> removeIndexAndAddLast = Arrays.asList(3, 4);

	/*
	 * Method to fetch all the products exist in the Database.
	 */
	public ProductResponse getAllProducts(ProductRequest productRequest,
			ProductHierarchyResponse productHierarchyResponse, String actionName) {
		ProductResponse productResponse = new ProductResponse();
		try {

			log.info("Entities_Service : : : : > getAllProducts ");
			log.debug("Entities_Service : : : : > Product Hierarchy Response Parameters : "
					+ productHierarchyResponse.toString());
			String listOfHeaderString = commonService.getListOfHeaderString(productRequest, productHierarchyResponse);
			log.debug("Entities_Service : : : : > Header Name : " + listOfHeaderString);
			List<String> listOfHeader = commonService.getListofHeader(listOfHeaderString, productRequest);
			log.debug("Entities_Service : : : : > List of Headers : " + listOfHeader);
			String listOfHeaderStringWithSearch = commonService.updatingQueryWithSearch(productRequest.getSearchValue(),
					listOfHeaderString);
			String getAllNativeQuery = finalModifiedGetAllQueryWithProductID("SELECT *", listOfHeaderStringWithSearch,
					productRequest);
			int totalElements = getAllProductsCount(finalModifiedGetAllQueryWithProductID("SELECT COUNT(*)",
					listOfHeaderStringWithSearch, productRequest));
			log.debug("Entities_Service : : : : >  SQL Native Query : " + getAllNativeQuery);
			List<Object[]> allProducts = getAllProducts(getAllNativeQuery, productRequest);
			listOfHeaderStringWithSearch = listOfHeaderStringWithSearch
					.replace("ORDER BY " + productRequest.getSortFieldStringWithASCOrDESC().trim(), "");
			log.debug("Entities_Service : : : : > Total Element Exist in the Database : " + totalElements);
			int pagesCount = totalElements / productRequest.getPageSize();
			int totalPages = totalElements % productRequest.getPageSize() == 0 ? pagesCount : pagesCount + 1;
			productResponse = updatingProductResponse(

					commonService.getModifiedHeaderWithKeyOrVlaue(listOfHeader, productHierarchyResponse, actionName),
					allProducts, productRequest.getPageIndex() >= 0 ? productRequest.getPageIndex() + 1

							: productRequest.getPageIndex(),
					productRequest.getPageSize(), totalElements, totalPages);
			log.debug("Entities_Service : : : : >  Product Response Data : " + productResponse.toString());
		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getAllProducts : " + e.getMessage());
		}
		return productResponse;
	}

	private List<String> getModifiedHeaderWithKeyOrVlaue(List<String> listOfHeader,
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

//	private List<?> updateHeaderForCustormer(List<?> listOfHeader) {
//		 listOfHeader.stream().map(header -> header.replace("_", " "))
//				.collect(Collectors.toList());
//		 List<?> removedElements
//		
//		
//	}
//
//	private void swapElementsForCustomerView(List<?> listOfObjects) {
//		List<?> removedValues = new ArrayList<>();
//		for (int i = 0; i < removeIndexAndAddLast.size(); i++) {
//			removedValues.add(removedValues.get(i));
//		}
//		
//	}

	private String getListOfHeaderString(ProductRequest productRequest,
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

	/*
	 * Methods to get Headers for CSV file
	 */
	private List<String> getListofHeader(String listOfHeaderString, ProductRequest productRequest) {
		List<String> listOfData = Arrays.asList(
				listOfHeaderString.replace("ORDER BY " + productRequest.getSortFieldStringWithASCOrDESC().trim(), "")
						.replace(":searchString", "").replace("(", "").replace(")", "").replace("int", "")
						.replaceAll("text", "").split("\\s*,\\s*"));
		listOfData.set(listOfData.size() - 1, listOfData.get(listOfData.size() - 1).trim());
		return listOfData;
	}

	private ProductResponse updatingProductResponse(List<String> headers, List<Object[]> rowData, int pageIndex,
			int pageSize, int totalElements, int totalPages) {
		ProductResponse productResponse = new ProductResponse();
		productResponse.setListOfHearders(headers);
		productResponse.setListOfRowData(rowData);
		productResponse.setPageIndex(pageIndex);
		productResponse.setPageSize(pageSize);
		productResponse.setTotalElements(totalElements);
		productResponse.setTotalPages(totalPages);
		return productResponse;

	}

	private String updatedSearchString(String searchValue, String listOfHeaderString) {
		String likeValue = " ILIKE '%" + searchValue;
		String whereValue = "WHERE ";
		return new StringBuilder().append(whereValue)
				.append(listOfHeaderString.replace(":searchString", "").replace("(", "").replace(")", "")
						.replace("SKU_ID int,", "").replaceAll(",", " OR ").replaceAll("text", likeValue + "%'"))
				.toString();
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

	private String finalModifiedGetAllQueryWithProductID(String actionValue, String listOfHeaderString,
			ProductRequest productRequest) {
		String modifiedString = new StringBuilder().append(actionValue)
				.append(FIND_ALL_PRODUCTS_QUERY.replaceAll(":tenantId", "''" + productRequest.getTenantid() + "''"))
				.append(listOfHeaderString).toString();
		if (productRequest.getProductId() > 0) {
			modifiedString = modifiedString.replace(":singleProductId",
					"AND S.sku_id = " + productRequest.getProductId());
		} else {
			modifiedString = modifiedString.replace(":singleProductId", "");
		}
		return modifiedString;
	}

	@SuppressWarnings("unchecked")
	private List<Object[]> getAllProducts(String getAllProductsquery, ProductRequest productRequest) {
		return entityManager.createNativeQuery(getAllProductsquery)
				.setFirstResult(productRequest.getPageIndex() * productRequest.getPageSize())
				.setMaxResults(productRequest.getPageSize()).getResultList();
	}

	/*
	 * Method to get Total Number of Product Exist
	 */
	private int getAllProductsCount(String getAllProductsquery) {
		log.info("Entities_Service : : : : > getAllProductsCount - Getting all the Products Count");
		return Integer.parseInt(entityManager.createNativeQuery(getAllProductsquery).getSingleResult().toString());
	}

	/*
	 * Method to Create Product Request
	 */
	private ProductRequest createProductRequest(int pageIndex, int pageSize, String sortFieldStringWithASCOrDESC,
			String searchValue, String tenantId) {
		log.info("Entities_Service : : : : > createProductRequest - Creating Product Request ");
		ProductRequest productRequest = new ProductRequest();
		productRequest.setPageIndex(pageIndex);
		productRequest.setPageSize(pageSize);
		productRequest.setSortFieldStringWithASCOrDESC(sortFieldStringWithASCOrDESC);
		productRequest.setSearchValue(searchValue);
		productRequest.setTenantid(tenantId);
		log.debug("Entities_Service : : : : > Product Request Parameters : " + productRequest.toString());
		return productRequest;
	}

	private ProductRequest createProductRequest(int pageIndex, int pageSize, String sortFieldStringWithASCOrDESC,
			String searchValue) {
		log.info("Entities_Service : : : : > createProductRequest - Creating Product Request ");
		ProductRequest productRequest = new ProductRequest();
		productRequest.setPageIndex(pageIndex);
		productRequest.setPageSize(pageSize);
		productRequest.setSortFieldStringWithASCOrDESC(sortFieldStringWithASCOrDESC);
		productRequest.setSearchValue(searchValue);
		log.debug("Entities_Service : : : : > Product Request Parameters : " + productRequest.toString());
		return productRequest;
	}

	/*
	 * Method to Download CSV
	 */
	@Override
	public InputStreamResource getCSVDownload(ProductHierarchyResponse productHierarchyResponse, String tenantId) {
		log.info("Entities_Service : : : : >  getCSVDownload()");
		log.debug("Entities_Service : : : : > Product Hierarchy Response : " + productHierarchyResponse.toString());
		ByteArrayInputStream in = null;
		try {
			ProductResponse productResponse = getAllProducts(createProductRequest(0, 0, "", "", tenantId),
					productHierarchyResponse, "forDownload");
			log.debug("Entities_Service : : : : > Product Response : " + productResponse.toString() + " --- ");

			in = CSVUtilities
					.objectsToCSVCovertoer(
							commonService.changelistOfHeaderForCustomerView(productResponse.getListOfHearders(),
									commonService.getLevelValues(
											productHierarchyResponse.getProdductHierarchyLevelMap()),
									"value"),
							productResponse.getListOfRowData());

		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getCSVDownload : " + e.getMessage());
			throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
		}
		return new InputStreamResource(in);
	}

	/*
	 * Method to Download CSV Template
	 */
	public InputStreamResource getCSVTemplateDownload(ProductHierarchyResponse productHierarchyResponse,
			String tenantId) {
		log.info("Entities_Service : : : : > getCSVTemplateDownload()");
		log.debug("Entities_Service : : : : > Product Hierarchy Response : " + productHierarchyResponse.toString());
		ByteArrayInputStream in = null;
		try {
			ProductRequest productRequest = createProductRequest(0, 0, "", "");
			List<Object[]> rowData = new ArrayList<>();
			String listOfHeaderString = commonService.getListOfHeaderString(productRequest, productHierarchyResponse);
			log.debug("Entities_Service : : : : > Header_Name : " + listOfHeaderString);

			List<String> listOfHeader = commonService.getListofHeader(listOfHeaderString, productRequest);
			listOfHeader = commonService.changelistOfHeaderForCustomerView(listOfHeader,
					commonService.getLevelValues(productHierarchyResponse.getProdductHierarchyLevelMap()), "value");

			log.debug("Entities_Service : : : : > List Of Headers : " + listOfHeaderString + "---");
			in = CSVUtilities.objectsToCSVCovertoer(listOfHeader, rowData);

		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getCSVTemplateDownload : " + e.getMessage());
			throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
		}
		return new InputStreamResource(in);
	}

	private List<String> changelistOfHeaderForCustomerView(List<String> listOfHeader, List<String> listOfKeyOrValues,
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

	public CSVUploadResponse uploadProductCSVData(MultipartFile csvFile,
			ProductHierarchyResponse productHierarchyResponse, String tenantId) {
		String processMessage = "";
		int processErrorCode = 0;
		List<Long> failedValidationRowNumbers = new ArrayList<>();
		try {
			// check is CSV file or not
			if (CSVUtilities.isCSVFile(csvFile)) {
				CSVParser csvParserObj = CSVUtilities.getCSVParserfromFileObj(csvFile.getInputStream());
				List<String> listOfMainHeaders = new ArrayList<String>(
						Arrays.asList(LIST_OF_MAIN_HEADERS.split("\\s*,\\s*")));

				List<String> listOfDynamicHeaders = commonService
						.getLevelValues(productHierarchyResponse.getProdductHierarchyLevelMap());

				listOfMainHeaders.addAll(listOfDynamicHeaders);
				// Validation on the headers
				if (isHeaderValid(csvParserObj.getHeaderNames(), listOfMainHeaders)) {
					// if (true) {
					List<Sku> skuList = readDataFromCSVParser(csvParserObj, listOfMainHeaders, listOfDynamicHeaders,
							failedValidationRowNumbers, tenantId);
					// Check if any data is exist in DB or not
					List<Sku> skuTransiantList = getNewRecordsAndUpdateOld(skuList);
					skuRepository.saveAll(skuTransiantList);
					processMessage = "Uploaded the file successfully: " + csvFile.getOriginalFilename();
					processErrorCode = 200;
				} else {
					processMessage = "Please check that header names of CSV" + csvFile.getOriginalFilename();
					processErrorCode = 400;
				}
			} else {
				processMessage = "Please check that as given file is not CSV" + csvFile.getOriginalFilename();
				processErrorCode = 403;
			}
		} catch (IOException e) {
			processMessage = "Failed to parse the CSV file" + csvFile.getOriginalFilename();
			processErrorCode = 406;
			log.error("Entities_Service : : : : > Exception in uploadProductCSVData : " + e.getMessage());
		} catch (Exception e) {
			processMessage = "Failed to upload the CSV file" + csvFile.getOriginalFilename();
			processErrorCode = 500;
			log.error("Entities_Service : : : : > Exception in uploadProductCSVData : " + e.getMessage());
		}
		CSVUploadResponse csvUploadResponse = updateCSVUploadResponse(processErrorCode, processMessage,
				failedValidationRowNumbers);
		return csvUploadResponse;
	}

	private List<Sku> getNewRecordsAndUpdateOld(List<Sku> skuList) {
		List<Sku> skuTransiantList = new ArrayList<>();
		skuList.forEach(sku -> {
			Sku SkuReference = (Sku) SerializationUtils.clone(sku);
			Sku skuT = skuRepository.findByLocationCodeAndSkuCodeAndTenantId(sku.getLocationCode(), sku.getSkuCode(),
					sku.getTenantId());
			if (skuT != null) {
				skuT = updateSkuExistingData(skuT, SkuReference);
				skuRepository.save(skuT);
			} else {
				skuTransiantList.add(SkuReference);
			}

		});
		return skuTransiantList;
	}

	private Sku updateSkuExistingData(Sku skuT, Sku SkuReference) {
		skuT.setSalePriceUnit(SkuReference.getSalePriceUnit());

		Object[] dbObject = skuT.getSkuHierarchy().toArray();
		Object[] needToUpdateObject = SkuReference.getSkuHierarchy().toArray();
		Set<SkuHierarchy> updatedSet = new HashSet<>();

		for (int i = 0; i < dbObject.length; i++) {
			SkuHierarchy dbObjectT = (SkuHierarchy) dbObject[i];
			SkuHierarchy needToUpdateObjectT = (SkuHierarchy) needToUpdateObject[i];
			dbObjectT.setLevel(needToUpdateObjectT.getLevel());
			dbObjectT.setValue(needToUpdateObjectT.getValue());
			updatedSet.add(dbObjectT);
		}
		// Update the SKU hierarchy
		skuT.setSkuHierarchy(updatedSet);
		return skuT;
	}

	private CSVUploadResponse updateCSVUploadResponse(int processErrorCode, String processMessage,
			List<Long> failedValidationRowNumbers) {
		CSVUploadResponse csvUploadResponse = new CSVUploadResponse();
		csvUploadResponse.setProcessErrorCode(processErrorCode);
		csvUploadResponse.setProcessMessage(processMessage);
		csvUploadResponse.setFailedValidationRowNumbers(failedValidationRowNumbers);
		return csvUploadResponse;
	}

	private boolean isHeaderValid(List<String> csvHeaders, List<String> requiredHeaders) {
		List<String> modifyHeaders = new ArrayList<>(csvHeaders);
		List<String> modifyrRquiredHeaders = new ArrayList<>(requiredHeaders);
		modifyHeaders = modifyHeaders.stream().map(String::toUpperCase).collect(Collectors.toList());
		modifyrRquiredHeaders = modifyrRquiredHeaders.stream().map(String::toUpperCase).collect(Collectors.toList());
		Collections.sort(modifyHeaders);
		Collections.sort(modifyrRquiredHeaders);
		return modifyHeaders.containsAll(modifyrRquiredHeaders);
	}

	private List<Sku> readDataFromCSVParser(CSVParser csvParserObj, List<String> listOfMainHeaders,
			List<String> listOfDynamicHeaders, List<Long> failedValidationRowNumbers, String tenantId)
			throws IOException {
		List<Sku> skuList = new ArrayList<>();
		Iterable<CSVRecord> csvRecords = csvParserObj.getRecords();
		;
		for (CSVRecord csvRecord : csvRecords) {
			if (isCSVDataValid(csvRecord.get(listOfMainHeaders.get(0).trim()),
					csvRecord.get(listOfMainHeaders.get(1).trim()), csvRecord.get(listOfMainHeaders.get(2).trim()),
					csvRecord.get(listOfMainHeaders.get(3).trim()), csvRecord.get(listOfMainHeaders.get(4).trim()))) {
				try {
					Sku sku = new Sku(csvRecord.get(listOfMainHeaders.get(0)), csvRecord.get(listOfMainHeaders.get(1)),
							csvRecord.get(listOfMainHeaders.get(2)), csvRecord.get(listOfMainHeaders.get(3)),
							Double.parseDouble(
									String.format("%.2f", Double.parseDouble(csvRecord.get(listOfMainHeaders.get(4))))),
							tenantId);
					addingSkuHierarchyToSku(csvRecord, listOfDynamicHeaders, sku);
					skuList.add(sku);

				} catch (NumberFormatException e) {
					log.error("Entities_Service : : : : > Exception in readDataFromCSVParser : " + e.getMessage());
					failedValidationRowNumbers.add(csvRecord.getRecordNumber() + 1);
				}
			} else {
				failedValidationRowNumbers.add(csvRecord.getRecordNumber() + 1);
			}
		}
		return skuList;
	}

	private void addingSkuHierarchyToSku(CSVRecord csvRecord, List<String> listOfDynamicHeaders, Sku sku) {
		Set<SkuHierarchy> skuhSet = new HashSet<>();
		for (int i = 0; i < listOfDynamicHeaders.size(); i++) {
			SkuHierarchy skuh = new SkuHierarchy(String.valueOf(i + 1), csvRecord.get(listOfDynamicHeaders.get(i)),
					sku);
			skuhSet.add(skuh);
		}
		sku.setSkuHierarchy(skuhSet);
	}

	private boolean isCSVDataValid(String skuName, String locationName, String skuCode, String locationCode,
			String salePrice) {
		return !Strings.isEmpty(skuName) && !Strings.isEmpty(locationName) && !Strings.isEmpty(salePrice)
				&& !Strings.isEmpty(skuCode) && !Strings.isEmpty(locationCode)
				&& CommonUtilites.isDoubleNumeric(salePrice);
	}

	public ProductHierarchyResponse getProductHierarchyResponse(Object productHierarchyRequest) {
		ProductHierarchyResponse productHierarchyResponse = new ProductHierarchyResponse();
		try {
			if (productHierarchyRequest instanceof Map) {
				Map<?, ?> productHierarchyMap = (Map<?, ?>) productHierarchyRequest;

				int noOfLevels = (int) (productHierarchyMap
						.get(commonService.getHierarchyKey(productHierarchyMap, "NoOfLevels")));
				if (noOfLevels > 0) {
					Map<String, String> productHierarchyMapOut = new LinkedHashMap<>();
					for (int i = 1; i <= noOfLevels; i++) {
						String key = commonService.getHierarchyKey(productHierarchyMap, "Level" + i);

						String value = (String) (productHierarchyMap.get(key));
						productHierarchyMapOut.put(key, value);
					}
					productHierarchyResponse.setNoOfLevels(noOfLevels);
					productHierarchyResponse.setProdductHierarchyLevelMap(productHierarchyMapOut);
				}
			}
		} catch (Exception e) {
			log.error("Entities_Service : : : : > Exception in getProductHierarchyResponse : " + e.getMessage());
		}
		return productHierarchyResponse;

	}

	public String getHierarchyKey(Map<?, ?> hierarchyRequest, String filterValue) {
		List<String> productKey = new ArrayList(hierarchyRequest.keySet());
		for (int i = 0; i < productKey.size(); i++) {
			if (productKey.get(i).contains(filterValue)) {
				return productKey.get(i);
			}
		}
		return "";
	}

	public List<String> getLevelListOnNoOfLevels(int noOfLevels) {
		List<String> listLevels = new ArrayList<>();
		for (int i = 1; i <= noOfLevels; i++) {
			listLevels.add("\"" + String.valueOf(i) + "\"");
		}
		return listLevels;
	}

	public List<String> getLevelValues(Map<String, String> hierarchyMap) {
		return new ArrayList<>(hierarchyMap.values());
	}

	public List<String> getLevelKeys(Map<String, String> hierarchyMap) {
		return new ArrayList<>(hierarchyMap.keySet());

	}

	@Override
	public ProductRequest getProductRequest(int pageIndex, int pageSize, String sortFieldStringWithASCOrDESC,
			String searchValue, long productId, String tenantId) {
		ProductRequest productRequest = new ProductRequest();
		productRequest.setPageIndex(pageIndex);
		productRequest.setPageSize(pageSize);
		productRequest.setSortFieldStringWithASCOrDESC(sortFieldStringWithASCOrDESC);
		productRequest.setSearchValue(searchValue);
		productRequest.setProductId(productId);
		productRequest.setTenantid(tenantId);

		return productRequest;
	}

}
