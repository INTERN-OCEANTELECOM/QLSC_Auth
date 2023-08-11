package com.ocena.qlsc.product.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.error.exception.DataAlreadyExistException;
import com.ocena.qlsc.common.error.exception.FileUploadException;
import com.ocena.qlsc.common.error.exception.ResourceNotFoundException;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.DataResponse;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.common.util.StringUtil;
import com.ocena.qlsc.podetail.utils.FileExcelUtil;
import com.ocena.qlsc.product.dto.ProductDto;
import com.ocena.qlsc.product.dto.ProductImageResponse;
import com.ocena.qlsc.product.mapper.ProductMapper;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.model.ProductImage;
import com.ocena.qlsc.product.repository.ProductRepository;
import com.ocena.qlsc.product.utils.FileUtil;
import com.ocena.qlsc.user_history.mapper.HistoryMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ProductService extends BaseServiceImpl<Product, ProductDto> implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    FileExcelUtil processExcelFile;

    @Autowired
    HistoryMapper mapper;

    @Autowired
    FileUtil fileUtil;

    @Override
    protected BaseRepository<Product> getBaseRepository() {
        return productRepository;
    }

    @Override
    protected BaseMapper<Product, ProductDto> getBaseMapper() {
        return productMapper;
    }

    @Override
    protected Function<String, Optional<Product>> getFindByFunction() {
        return productRepository::findByProductId;
    }

    @Override
    protected Class<Product> getEntityClass() {
        return Product.class;
    }

    @Override
    public Logger getLogger() {
        return super.getLogger();
    }

    /**
     * get Product By Page
     *
     * @param searchKeywordDto receives the keywords and property used for searching
     * @param pageable         receives the page to be returned
     * @return a page of products according to the keywords
     */
    @Override
    protected Page<ProductDto> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        List<String> listKeywords = StringUtil.splitStringToList(searchKeywordDto.getKeyword().get(0));

        Page<Object[]> resultPage = productRepository.getProductPageable(PageRequest.of(0, Integer.MAX_VALUE));
        List<ProductDto> productDTOs = resultPage.getContent().stream().map(objects -> ProductDto.builder()
                .productId(objects[0].toString())
                .productName(objects[1].toString())
                .amount(Integer.valueOf(objects[2].toString()))
                .build()).toList();

        try {
            if (!listKeywords.isEmpty()) {
                //Check if the first element of the list is of type Long
                Long.parseLong(listKeywords.get(0));
            }

            List<ProductDto> mergeList = productDTOs.stream()
                    .filter(product -> listKeywords.isEmpty()
                            || listKeywords.stream()
                            .anyMatch(keyword -> product.getProductId().contains(keyword)))
                    .collect(Collectors.toList());

            return mergeListToPageProductDTO(mergeList, pageable);
        } catch (NumberFormatException e) {
            List<ProductDto> mergeList =  productDTOs.stream().filter(productDTO -> productRepository.searchProduct(searchKeywordDto.getKeyword().get(0), pageable)
                    .map(product -> productMapper.entityToDto(product)).stream().anyMatch(productDTO1 -> productDTO1.getProductId().equals(productDTO.getProductId()))).collect(Collectors.toList());

            return mergeListToPageProductDTO(mergeList, pageable);
        }
    }

    @Override
    protected List<Product> getListSearchResults(String keyword) {
        return null;
    }

    public ListResponse<ProductDto> getProductByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> resultPage = productRepository.getProductPageable(pageable);
        Page<ProductDto> productResponsePage = resultPage.map(objects -> ProductDto.builder()
                .productId(objects[0].toString())
                .productName(objects[1].toString())
                .amount(Integer.valueOf(objects[2].toString()))
                .build());

        return ResponseMapper.toPagingResponseSuccess(productResponsePage);
    }

    public ListResponse<ProductDto> getAllProduct() {
        List<ProductDto> allProducts = getProductByPage(0, Integer.MAX_VALUE).getData() ;

        return ResponseMapper.toListResponseSuccess(allProducts);
    }

    public Page<ProductDto> mergeListToPageProductDTO(List<ProductDto> mergeList, Pageable pageable){
        List<ProductDto> pageProducts = mergeList
                .subList(pageable.getPageNumber() * pageable.getPageSize(),
                        Math.min(pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize(), mergeList.size()));

        return new PageImpl<>(pageProducts, pageable, mergeList.size());
    }

    public DataResponse<ProductDto> createProduct(List<MultipartFile> files, ProductDto dto) {
        List<ProductImage> productImages = new ArrayList<>();

        if(productRepository.existsProductByProductId(dto.getProductId())) {
            throw new DataAlreadyExistException(dto.getProductId().toString() + " already exist");
        }

        Product product = Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .build();

        for(MultipartFile file: files) {
            try {
                if(!fileUtil.isImage(file.getInputStream()))
                    throw new FileUploadException("Files isn't in the correct format");
                productImages.add(new ProductImage(fileUtil.saveProductImages(file, dto.getProductName()), product));
            } catch (IOException e) {
                throw new FileUploadException("Files isn't in the correct format");
            }
        }

        product.setImages(productImages);
        productRepository.save(product);
        return ResponseMapper.toDataResponseSuccess("Success");
    }

    public DataResponse<ProductDto> getProductById(String productId) {
        Optional<Product> optionalProduct = productRepository.findByProductId(productId);

        if(optionalProduct.isEmpty())
            throw new ResourceNotFoundException(productId + " doesn't exist");

        Product product = optionalProduct.get();
        System.out.println(product);
        List<ProductImageResponse> images = new ArrayList<>();

        for(ProductImage image: product.getImages()) {
            System.out.println("Vao day");
            images.add(new ProductImageResponse(fileUtil.getFileBytes(image.getFilePath())));
        }

        ProductDto response = productMapper.entityToDto(product);
        response.setImages(images);

        return ResponseMapper.toDataResponseSuccess(response);
    }
}
