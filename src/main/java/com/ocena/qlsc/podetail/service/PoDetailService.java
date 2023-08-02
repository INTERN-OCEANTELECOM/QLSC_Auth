package com.ocena.qlsc.podetail.service;

import com.ocena.qlsc.common.util.ReflectionUtil;
import com.ocena.qlsc.podetail.utils.FileExcelUtil;
import com.ocena.qlsc.user.model.RoleUser;
import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.message.StatusCode;
import com.ocena.qlsc.common.message.StatusMessage;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.common.util.SystemUtil;
import com.ocena.qlsc.po.dto.PoDTO;
import com.ocena.qlsc.po.model.Po;
import com.ocena.qlsc.po.repository.PoRepository;
import com.ocena.qlsc.podetail.dto.PoDetailResponse;
import com.ocena.qlsc.podetail.model.PoDetail;
import com.ocena.qlsc.podetail.model.PoDetailMapper;
import com.ocena.qlsc.podetail.repository.PoDetailRepository;
import com.ocena.qlsc.podetail.constants.ImportErrorType;
import com.ocena.qlsc.common.response.ErrorResponseImport;
import com.ocena.qlsc.podetail.constants.RegexConstants;
import com.ocena.qlsc.podetail.constants.UpdateFieldConstants;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.repository.ProductRepository;
import com.ocena.qlsc.user.model.Role;
import com.ocena.qlsc.user.repository.RoleRepository;
import com.ocena.qlsc.user_history.enums.Action;
import com.ocena.qlsc.user_history.enums.ObjectName;
import com.ocena.qlsc.user_history.model.HistoryDescription;
import com.ocena.qlsc.user_history.service.HistoryService;
import com.ocena.qlsc.user_history.utils.FileUtil;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PoDetailService extends BaseServiceImpl<PoDetail, PoDetailResponse> implements IPoDetailService {
    @Autowired
    PoDetailMapper poDetailMapper;

    @Autowired
    PoDetailRepository poDetailRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PoRepository poRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FileExcelUtil processExcelFile;

    @Autowired
    HistoryService historyService;

    @Autowired
    FileExcelUtil fileExcelUtil;

    @Override
    public List<String> validationRequest(Object object) {
        return super.validationRequest(object);
    }

    @Override
    protected BaseRepository<PoDetail> getBaseRepository() {
        return poDetailRepository;
    }

    @Override
    protected BaseMapper<PoDetail, PoDetailResponse> getBaseMapper() {
        return poDetailMapper;
    }

    @Override
    protected Function<String, Optional<PoDetail>> getFindByFunction() {
        return poDetailRepository::findByPoDetailId;
    }

    @Override
    protected Class<PoDetail> getEntityClass() {
        return PoDetail.class;
    }

    @Override
    protected Page<PoDetail> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        List<String> listSerialNumbers = searchKeywordDto.getKeyword().get(1) != null ?
                Arrays.asList(searchKeywordDto.getKeyword().get(1).split("\\s+")) : new ArrayList<>();

        Pageable page = pageable;

        if (!listSerialNumbers.isEmpty()) {
            pageable = PageRequest.of(0, Integer.MAX_VALUE);
        }

        Page<PoDetail> pageSearchPoDetails = poDetailRepository.searchPoDetail(
                searchKeywordDto.getKeyword().get(0),
                searchKeywordDto.getKeyword().get(2),
                searchKeywordDto.getKeyword().get(3),
                searchKeywordDto.getKeyword().get(4),
                searchKeywordDto.getKeyword().get(5),   
                searchKeywordDto.getKeyword().get(6),
                searchKeywordDto.getKeyword().get(7),
                searchKeywordDto.getKeyword().get(8),
                searchKeywordDto.getKeyword().get(9), pageable);

        if (listSerialNumbers.isEmpty()) {
            return pageSearchPoDetails;
        }

        List<PoDetail> mergeList = pageSearchPoDetails.getContent()
                .stream()
                .filter(poDetail -> listSerialNumbers.contains(poDetail.getSerialNumber()))
                .collect(Collectors.toList());
                
        //Create Page with Start End
        List<PoDetail> pagePoDetails = mergeList
                .subList(page.getPageNumber() * page.getPageSize(),
                        Math.min(page.getPageNumber() * page.getPageSize() + page.getPageSize(), mergeList.size()));
        return new PageImpl<>(pagePoDetails, page, mergeList.size());

    }

    @Override
    protected List<PoDetail> getListSearchResults(String keyword) {
        return null;
    }

    public ListResponse<PoDetailResponse> getByPO(String poNumber) {
        if (poNumber.equals("getAll")) {
            return ResponseMapper.toListResponseSuccess(
                    poDetailRepository.findAll()
                            .stream().map(poDetail -> getBaseMapper().entityToDto(poDetail)).collect(Collectors.toList()));
        }
        return ResponseMapper.toListResponseSuccess(
                poDetailRepository.findByPoNumber(poNumber)
                        .stream().map(poDetail -> getBaseMapper().entityToDto(poDetail)).collect(Collectors.toList()));
    }

    public Object setDataFromDTO(PoDetailResponse poDetailResponse, int rowIndex, List<String> fields) {
        Optional<PoDetail> optionalPoDetail = poDetailRepository.findByPoDetailId(poDetailResponse.getPoDetailId());
        // If the PO Detail does not exist, add an error to the list of errors and continue to the next row
        if (optionalPoDetail.isEmpty()) {
            return new ErrorResponseImport(ImportErrorType.DATA_NOT_FOUND,
                    rowIndex, poDetailResponse.getPoDetailId() + " không tồn tại");
        }
        PoDetail poDetail = optionalPoDetail.get();
        try {
            for (String field: fields) {
                if(RegexConstants.requiredFeilds.stream().anyMatch(value -> value.equals(field)))
                     continue;
                Method method = ReflectionUtil.setterMethod(PoDetail.class, field, ReflectionUtil.getFieldType(field, new PoDetail()));
                method.invoke(poDetail, ReflectionUtil.getFieldValueByReflection(field, poDetailResponse));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return poDetail;

    }

    /**
     * Processes an Excel file containing information about PO Details and updates their information in the database.
     *
     * @param file      the Excel file to process
     * @return a ListResponse containing a list of errors if any errors occurred during processing, or a list of updated PO Details if no errors occurred
     * @throws NoSuchMethodException     if a specified method does not exist
     * @throws NoSuchFieldException      if a specified field does not exist
     * @throws IllegalAccessException    if access to a specified field is denied
     * @throws InvocationTargetException if a specified method cannot be invoked
     */
    @SuppressWarnings("unchecked")
    public ListResponse<ErrorResponseImport> updatePoDetailFromExcel(MultipartFile file) throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        LinkedList<ErrorResponseImport> listErrorResponse = new LinkedList<>();
        List<PoDetail> listUpdatePoDetail = new ArrayList<>();

        // Process the Excel file
        Object dataFile = processExcelFile.getSheetIteratorFromExcelFile(file);
        //  If the Excel file could not be processed, return an error response
        if (dataFile instanceof ListResponse) {
            return (ListResponse) dataFile;
        }
        Iterator<Row> rowIterator = (Iterator<Row>) dataFile;

        // Validate the header row
        Object dataInHeader = fileExcelUtil.getFieldsNameInHeader(rowIterator);
        if(dataInHeader instanceof ErrorResponseImport) {
            listErrorResponse.add((ErrorResponseImport) dataInHeader);
            return ResponseMapper.toListResponse(listErrorResponse, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }
        List<String> fields = (List<String>) dataInHeader;

        // Read each row in the sheet and update the corresponding PO Detail in the database
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if(processExcelFile.isLastedRow(row)) {
                break;
            }
            int rowIndex = row.getRowNum() + 1;

            // Read the data from the row
            Object data = readDataFromRow(row, rowIndex, fields);


            // If there is an error, add it to the list of errors; otherwise, update the corresponding PO Detail
            if (data instanceof ErrorResponseImport) {
                ErrorResponseImport errorResponseImport = (ErrorResponseImport) data;
                listErrorResponse.add(errorResponseImport);
            } else {
                PoDetailResponse poDetailResponse = (PoDetailResponse) data;

                if(listUpdatePoDetail.stream()
                        .anyMatch(poDetail -> poDetail.getPoDetailId().equals(poDetailResponse.getPoDetailId()))) {
                    listErrorResponse.add(new ErrorResponseImport(ImportErrorType.RECORD_EXISTED,
                            rowIndex, poDetailResponse.getPoDetailId() + " bị trùng"));
                    continue;
                }
                Object value = setDataFromDTO(poDetailResponse, rowIndex, fields);
                if(value instanceof ErrorResponseImport) {
                    listErrorResponse.add((ErrorResponseImport) value);
                } else {
                    System.out.println("value" + value);
                    listUpdatePoDetail.add((PoDetail) value);
                }
            }
        }

        if (listErrorResponse.isEmpty() && listUpdatePoDetail.size() > 0) {
            poDetailRepository.saveAll(listUpdatePoDetail);
            saveHistoryImportDataExcel(Action.UPDATE.getValue(), listUpdatePoDetail, file);
            return ResponseMapper.toListResponseSuccess(List.of(
                    new ErrorResponseImport(ImportErrorType.DATA_SUCCESS, listUpdatePoDetail.size() + " dòng update thành công")));
        }
        return ResponseMapper.toListResponse(listErrorResponse, listErrorResponse.size(), 1, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
    }

    public ListResponse<PoDetailResponse> searchBySerialNumbers(MultipartFile file) {
        LinkedList<ErrorResponseImport> listError = new LinkedList<>();
        Object dataFile = processExcelFile.getSheetIteratorFromExcelFile(file);
        if(dataFile instanceof ListResponse<?>) {
            return (ListResponse) dataFile;
        }
        // Get an iterator over the rows in the Excel file
        Iterator<Row> rowIterator = (Iterator<Row>) dataFile;
        // Validate header value
        ErrorResponseImport errorResponseImport = processExcelFile.validateHeaderValue(rowIterator, RegexConstants.searchSerialNumbers);
        if(errorResponseImport != null) {
            listError.add(errorResponseImport);
            return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }


        List<String> listSearchSerialNumber = new ArrayList<>();
        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();
            int rowIndex = row.getRowNum() + 1;

            if (row.getCell(0).getCellType() == CellType.BLANK) {
                // Stop reading data when an empty line is encountered
                listError.add(new ErrorResponseImport(ImportErrorType.DATA_NOT_MAP, rowIndex, "Dữ liệu không được để trống"));
                continue;
            }

            String serialNumber = processExcelFile.getCellValueToString(row, 0);
            listSearchSerialNumber.add(serialNumber);
        }
        if(!listError.isEmpty()) {
            return ResponseMapper.toListResponse(listError, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }
        listSearchSerialNumber.forEach(System.out::println);
        List<PoDetail> listResults = poDetailRepository.findBySerialNumberIn(listSearchSerialNumber);
        listResults.forEach(System.out::println);
        return ResponseMapper.toListResponseSuccess(listResults.stream()
                .map(value -> getBaseMapper().entityToDto(value))
                .collect(Collectors.toList()));
    }

    private ErrorResponseImport validateDataImport(PoDetailResponse poDetailResponse, List<PoDetail> listInsert, Integer rowIndex) {
        // Check if the product in the row exists in the database
        boolean isProductExist = productRepository.findAll().stream()
                .anyMatch(p -> p.getProductId().equals(poDetailResponse.getProduct().getProductId()));

        if (isProductExist) {
            // If the product exists, continue processing the row
            Optional<Po> optionalPo = poRepository.findByPoNumber(poDetailResponse.getPo().getPoNumber());
            // // If the PO detail already exists in the database or has already been added to the list of PO details to be inserted, add an error
            Optional<PoDetail> optionalPoDetail = poDetailRepository.findByPoDetailId(poDetailResponse.getPoDetailId());
            if (optionalPoDetail.isPresent() || listInsert.stream()
                    .anyMatch(value -> value.getPoDetailId().equals(poDetailResponse.getPoDetailId()))) {
                return new ErrorResponseImport(ImportErrorType.RECORD_EXISTED,
                        rowIndex, poDetailResponse.getPoDetailId() + " đã tồn tại nên không thể import");
            }
            if (optionalPo.isEmpty()) {
                // If the PO for the PO detail does not exist in the database, add an error
                return new ErrorResponseImport(ImportErrorType.DATA_NOT_FOUND,
                        rowIndex, poDetailResponse.getPoDetailId() + " có PO không tồn tại");
            }

            // If the number of PO details for the PO has reached the PO quantity, add an error
            // and stop processing the file
            Integer quantity = optionalPo.get().getQuantity();
            Long countPoDetailByPoNumber = poDetailRepository.countByPoNumber(poDetailResponse.getPo().getPoNumber());
            if (countPoDetailByPoNumber + listInsert.size() > quantity) {
                return new ErrorResponseImport(poDetailResponse.getPo().getPoNumber(),
                        "Import nhiều hơn số lượng cho phép");
            }
        } else {
            return new ErrorResponseImport(ImportErrorType.DATA_NOT_FOUND,
                    rowIndex, poDetailResponse.getPoDetailId() + " có Mã Hàng Hóa không tồn tại");
        }
        return null;
    }

    public void saveHistoryImportDataExcel(String action, List<PoDetail> listPoDetail, MultipartFile file) {
        // Get List PoNumber distinct
        List<String> distinctPoNumber = listPoDetail.stream()
                .map(poDetail -> poDetail.getPo().getPoNumber())
                .distinct()
                .collect(Collectors.toList());

        HistoryDescription description = new HistoryDescription();
        description.setKey(String.join(", ", distinctPoNumber));
        description.setImportAmount(((Integer) listPoDetail.size()).toString());
        String descriptionHistory = "";
        for (PoDetail poDetail : listPoDetail) {
            descriptionHistory += "<" + poDetail.getSerialNumber().toString() + "> ";
        }
        description.setDetails(description.setDescription(descriptionHistory));

        // Save File to History
        String filePath = FileUtil.saveUploadedFile(file, action);
        //
        historyService.save(action, ObjectName.PoDetail, description.getDescription(), "", filePath);
    }

    /**
     * Imports PO detail data from an Excel file and saves it to the database.
     *
     * @param file the Excel file containing the PO detail data
     * @return a list of ErrorResponseImport objects representing any errors that occurred during the import process
     * @throws IOException
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public ListResponse<ErrorResponseImport> importPODetailFromExcel(MultipartFile file) {
        LinkedList<ErrorResponseImport> listErrorResponse = new LinkedList<>();
        List<PoDetail> listInsertPoDetail = new ArrayList<>();

        // Process the Excel file
        Object dataFile = processExcelFile.getSheetIteratorFromExcelFile(file);
        // If the Excel file could not be processed, return an error response
        if (dataFile instanceof ListResponse) {
            return (ListResponse) dataFile;
        }

        // Get an iterator over the rows in the Excel file
        Iterator<Row> rowIterator = (Iterator<Row>) dataFile;

        // Validate header value
        ErrorResponseImport errorResponse;
        Object dataInHeader = fileExcelUtil.getFieldsNameInHeader(rowIterator);
        if(dataInHeader instanceof ErrorResponseImport) {
            listErrorResponse.add((ErrorResponseImport) dataInHeader);
            return ResponseMapper.toListResponse(listErrorResponse, 0, 0, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
        }

        List<String> fieldsImport = (List<String>) dataInHeader;

        // Read each row in the Excel file and save the data to the database
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            int rowIndex = row.getRowNum() + 1;

            if(processExcelFile.isLastedRow(row)) {
                break;
            }

            // Read the data from the row
            Object data = readDataFromRow(row, rowIndex, fieldsImport);

            // If there was an error reading the row, add it to the list of errors
            if (data instanceof ErrorResponseImport) {
                errorResponse = (ErrorResponseImport) data;
                listErrorResponse.add(errorResponse);
            } else {
                // If the row was read successfully, process the data
                PoDetailResponse poDetailResponse = (PoDetailResponse) data;

                // Validate poDetail
                errorResponse = validateDataImport(poDetailResponse, listInsertPoDetail, rowIndex);
                if(errorResponse != null) {
                    listErrorResponse.add(errorResponse);
                    if (errorResponse.getErrorDescription().equals("Import nhiều hơn số lượng cho phép"))
                        break;
                    continue;
                }
                // If the PO detail is valid, convert it to an entity and add it to the list of PO details to be inserted
                PoDetail poDetail = getBaseMapper().dtoToEntity(poDetailResponse);
                listInsertPoDetail.add(poDetail);
            }
        }

        if (listErrorResponse.isEmpty() && listInsertPoDetail.size() > 0) {
            poDetailRepository.saveAll(listInsertPoDetail);
            saveHistoryImportDataExcel(Action.IMPORT.getValue(), listInsertPoDetail, file);
            return ResponseMapper.toListResponseSuccess(List.of(
                    new ErrorResponseImport(ImportErrorType.DATA_SUCCESS, listInsertPoDetail.size() + " dòng import thành công")));
        }
        return ResponseMapper.toListResponse(listErrorResponse, listErrorResponse.size(), 1, StatusCode.DATA_NOT_MAP, StatusMessage.DATA_NOT_MAP);
    }

    /**
     * Reads data from a row in an Excel file and returns an object representing the data.
     *
     * @param row      the row in the Excel file to read data from
     * @param rowIndex the index of the row in the Excel file
     * @return an object representing the data from the row, or an ErrorResponseImport object if there is an error
     */
    public Object readDataFromRow(Row row, int rowIndex, List<String> fieldsImport) {

        PoDetailResponse poDetailResponse = new PoDetailResponse();
        int colIndex = 0;
        for(String field: fieldsImport) {
            Object value = RegexConstants.functionGetDateFromCellExcel.get(field).apply(row, colIndex);
            colIndex++;

            if(field.equals("productId")) {
                poDetailResponse.setProduct(new ProductDTO((String) value));
                continue;
            }

            if(field.equals("poNumber")) {
                poDetailResponse.setPo(new PoDTO((String) value));
                continue;
            }

            if(value == null)
                return new ErrorResponseImport(ImportErrorType.DATA_NOT_MAP, rowIndex, "Hàng " + rowIndex + " cột " + (colIndex + 1) + " không phải number");

            try {
                if(value instanceof Long) {
                    Method method = ReflectionUtil.setterMethod(PoDetailResponse.class, field, Long.class);
                    method.invoke(poDetailResponse, value);
                } else if(value instanceof String) {
                    Method method = ReflectionUtil.setterMethod(PoDetailResponse.class, field, String.class);
                    method.invoke(poDetailResponse, value);
                } else if(value instanceof Short) {
                    Method method = ReflectionUtil.setterMethod(PoDetailResponse.class, field, Short.class);
                    method.invoke(poDetailResponse, value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }


        String poDetailId = poDetailResponse.getPo().getPoNumber() + "-" + poDetailResponse.getProduct().getProductId() + "-"
                + poDetailResponse.getSerialNumber();
        poDetailResponse.setPoDetailId(poDetailId);

        // Validate the PO detail object and return it if it is valid
        List<String> resultError = validationRequest(poDetailResponse);
        if (resultError == null) {
            return poDetailResponse;
        } else {
            // If the PO detail object is invalid, return an ErrorResponseImport object with an error message
            return new ErrorResponseImport(ImportErrorType.DATA_NOT_MAP, rowIndex, resultError.get(0));
        }
    }

    /**
     * Updates a PO detail record in the database with new data.
     *
     * @param poDetailResponse the new data to be saved
     * @param key              the ID of the PO detail record to be updated
     * @return a DataResponse object indicating whether the update was successful or not
     */
    @Transactional
    public DataResponse<PoDetailResponse> updatePoDetail(PoDetailResponse poDetailResponse, String key) {
        Optional<PoDetail> poDetail = poDetailRepository.findByPoDetailId(key);
        // Update the PO detail record with the new data
        if (poDetail.isPresent()) {
            HistoryDescription description = new HistoryDescription();
            description.setKey(poDetailResponse.getPoDetailId());
            String compare = poDetail.get().compare(getBaseMapper().dtoToEntity(poDetailResponse), Action.EDIT, description);
            description.setDetails(compare);


            poDetail.get().setRepairCategory(poDetailResponse.getRepairCategory());
            poDetail.get().setRepairStatus((poDetailResponse.getRepairStatus()));
            poDetail.get().setKcsVT(poDetailResponse.getKcsVT());
            poDetail.get().setBbbgNumberExport(poDetailResponse.getBbbgNumberExport());
            poDetail.get().setNote(poDetailResponse.getNote());
            poDetail.get().setWarrantyPeriod(poDetailResponse.getWarrantyPeriod());
            poDetail.get().setImportDate(poDetailResponse.getImportDate());
            poDetail.get().setExportPartner(poDetailResponse.getExportPartner());
            poDetail.get().setPriority(poDetailResponse.getPriority());

            poDetailRepository.save(poDetail.get());
            historyService.save(Action.EDIT.getValue(), ObjectName.PoDetail, description.getDescription(), "", null);

            return ResponseMapper.toDataResponse("", StatusCode.REQUEST_SUCCESS, StatusMessage.REQUEST_SUCCESS);
        }

        // Return an error response if the PO detail record was not found
        return ResponseMapper.toDataResponse(null, StatusCode.DATA_NOT_FOUND, StatusMessage.DATA_NOT_FOUND);
    }

    public DataResponse<String> deletePoDetail(String id) {
        Optional<PoDetail> poDetail = poDetailRepository.findByPoDetailId(id);
        if (poDetail.isPresent()) {
            /* Save History */
            HistoryDescription description = new HistoryDescription();
            description.setKey(poDetail.get().getPoDetailId());

            poDetailRepository.delete(poDetail.get());
            historyService.save(Action.DELETE.getValue(), ObjectName.PoDetail, description.getDescription(), "", null);
            return ResponseMapper.toDataResponseSuccess("Success");
        }
        return ResponseMapper.toDataResponseSuccess(null);
    }

    public Boolean validateRoleUpdatePO(List<String> attribute) {
        String email = SystemUtil.getCurrentEmail();
        List<Role> allRoles = roleRepository.getRoleByEmail(email);
        List<String> fieldsKSCUpdate = new ArrayList<>(Arrays.asList("productId", "serialNumber", "poNumber","kcsVT"));
        List<String> fieldsRepairStatusUpdate = new ArrayList<>(Arrays.asList("productId", "serialNumber", "poNumber","repairStatus"));

        for (Role role : allRoles) {
            if ((role.getRoleName().equals(RoleUser.ROLE_ADMIN.name()) || role.getRoleName().equals(RoleUser.ROLE_MANAGER.name()))
                    || (role.getRoleName().equals(RoleUser.ROLE_KCSANALYST.name()) && attribute.equals(fieldsKSCUpdate)
                    || (role.getRoleName().equals(RoleUser.ROLE_REPAIRMAN.name()) && attribute.equals(fieldsRepairStatusUpdate)))){
                return true;
            }
        }
        return false;
    }

    public List<String> validateRoleUpdatePO1(List<String> attribute) {
        String email = SystemUtil.getCurrentEmail();
        List<Role> allRoles = roleRepository.getRoleByEmail(email);
        List<String> fields = new ArrayList<>(Arrays.asList("productId", "serialNumber", "poNumber"));

        for (Role role : allRoles) {
            if ((role.getRoleName().equals(RoleUser.ROLE_ADMIN.name()) || role.getRoleName().equals(RoleUser.ROLE_MANAGER.name()))){
                fields = attribute;
            }

            if(role.getRoleName().equals(RoleUser.ROLE_KCSANALYST.name()) && attribute.stream().anyMatch(field -> field.equals(UpdateFieldConstants.KCS_VT))){
                fields.add("kcsVT");
            }

            if(role.getRoleName().equals(RoleUser.ROLE_REPAIRMAN.name()) && attribute.stream().anyMatch(field -> field.equals(UpdateFieldConstants.REPAIR_STATUS))){
                fields.add("repairStatus");
            }
        }
        return fields;
    }

    public ListResponse<PoDetailResponse> getBySerialNumber(String serialNumber){
        List<PoDetail> poDetails = poDetailRepository.getPoDetailsBySerialNumber(serialNumber);

        List<PoDetailResponse> poDetailResponses = poDetails
                .stream()
                .map(poDetail -> poDetailMapper.entityToDto(poDetail))
                .collect(Collectors.toList());

        return ResponseMapper.toListResponseSuccess(poDetailResponses);
    }

    /**
     * Get All PoDetail By List Keyword
     *
     * @param searchKeywordDto
     * @return list PoDetail
     */
    public ListResponse<PoDetail> getAllByListKeyword(SearchKeywordDto searchKeywordDto){
        Page<PoDetail> poDetailPage = getPageResults(searchKeywordDto, PageRequest.of(0, Integer.MAX_VALUE));

        return ResponseMapper.toListResponseSuccess(poDetailPage.getContent());
    }
}
