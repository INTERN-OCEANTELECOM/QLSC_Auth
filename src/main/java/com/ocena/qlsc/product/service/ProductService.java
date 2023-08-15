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
import com.ocena.qlsc.product.dto.image.ProductImageDto;
import com.ocena.qlsc.product.dto.product.ProductRequest;
import com.ocena.qlsc.product.dto.product.ProductResponse;
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

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ProductService extends BaseServiceImpl<Product, ProductRequest, ProductResponse> implements IProductService {
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
    protected BaseMapper<Product, ProductRequest, ProductResponse> getBaseMapper() {
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
     * @param searchKeywordDto receives the keywords and property used for searching
     * @param pageable         receives the page to be returned
     * @return a page of products according to the keywords
     */
    @Override
    protected Page<ProductResponse> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
//        List<String> listKeywords = StringUtil.splitStringToList(searchKeywordDto.getKeyword().get(0).trim());
        List<String> listKeywords = StringUtil.containsAlphabeticCharacters(searchKeywordDto.getKeyword().get(0).trim()) ?
                                    StringUtil.convertStringToList(searchKeywordDto.getKeyword().get(0).trim()) :
                                    StringUtil.splitStringToList(searchKeywordDto.getKeyword().get(0).trim());

        Page<Object[]> resultPage = productRepository.getProductPageable(pageable);

        Page<ProductResponse> productResponsePage = resultPage.map(objects -> ProductResponse.builder()
                .productId(objects[0].toString())
                .productName(objects[1].toString())
                .amount(Integer.valueOf(objects[2].toString()))
                .build());

        if(!listKeywords.isEmpty()) {
            productResponsePage = productResponsePage
                    .stream()
                    .filter(productResponse -> listKeywords.stream()
                            .anyMatch(key -> productResponse.getProductId().contains(key) ||
                            productResponse.getProductName().contains(key)))
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                            list -> new PageImpl<>(list, pageable, list.size())));
        }

        return productResponsePage;
    }

    @Override
    protected List<Product> getListSearchResults(String keyword) {
        return null;
    }

    public ListResponse<ProductResponse> getPagedProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> resultPage = productRepository.getProductPageable(pageable);
        Page<ProductResponse> productResponsePage = resultPage.map(objects -> ProductResponse.builder()
                .productId(objects[0].toString())
                .productName(objects[1].toString())
                .amount(Integer.valueOf(objects[2].toString()))
                .build());

        return ResponseMapper.toPagingResponseSuccess(productResponsePage);
    }

    public ListResponse<ProductResponse> getAllProduct() {
        List<ProductResponse> allProducts = getPagedProducts(0, Integer.MAX_VALUE).getData();
        return ResponseMapper.toListResponseSuccess(allProducts);
    }

    public DataResponse<ProductResponse> createProduct(ProductRequest productRequest) {
        List<ProductImage> productImages = new ArrayList<>();

        if(productRepository.existsProductByProductId(productRequest.getProductId())) {
            throw new DataAlreadyExistException(productRequest.getProductId().toString() + " already exist");
        }

        Product product = Product.builder()
                .productId(productRequest.getProductId())
                .productName(productRequest.getProductName())
                .build();

        for(String fileBase64: productRequest.getImagesBase64()) {
            byte[] fileBytes = fileUtil.convertBase64ToByteArray(fileBase64);
            productImages.add(new ProductImage(fileBytes, product));
        }

        product.setImages(productImages);
        Product savedProduct = productRepository.save(product);
        return ResponseMapper.toDataResponseSuccess(productMapper.entityToDto(savedProduct));
    }

    public DataResponse<ProductResponse> updateProduct(ProductRequest productRequest, String productId) {
        List<ProductImage> images = new ArrayList<>();
        Optional<Product> optionalProduct = productRepository.findByProductId(productId);

        if(optionalProduct.isEmpty()) {
            throw new ResourceNotFoundException(productId + "doesn't exist");
        }
        Product product = optionalProduct.get();
        product.getImages().clear();
        productMapper.dtoToEntity(productRequest, product);

        for(String fileBase64 : productRequest.getImagesBase64()) {
            byte[] fileBytes = fileUtil.convertBase64ToByteArray(fileBase64);
            images.add(new ProductImage(fileBytes, product));
        }
        product.getImages().addAll(images);
        Product savedProduct = productRepository.save(product);
        return ResponseMapper.toDataResponseSuccess(productMapper.entityToDto(savedProduct));
    }
}
