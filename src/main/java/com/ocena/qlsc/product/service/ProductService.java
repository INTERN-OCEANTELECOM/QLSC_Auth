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
        List<String> listKeywords = StringUtil.splitStringToList(searchKeywordDto.getKeyword().get(0).trim());

        Page<Object[]> resultPage = productRepository.getProductPageable(pageable);

        Page<ProductResponse> productResponsePage = resultPage.map(objects -> ProductResponse.builder()
                .productId(objects[0].toString())
                .productName(objects[1].toString())
                .amount(Integer.valueOf(objects[2].toString()))
                .build());

        if(!listKeywords.isEmpty()) {
            productResponsePage = productResponsePage
                    .stream()
                    .filter(productResponse -> listKeywords.contains(productResponse.getProductId()) ||
                            listKeywords.contains(productResponse.getProductName()))
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

    public DataResponse<ProductResponse> createProduct(List<MultipartFile> files, ProductRequest dto) {
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

    public DataResponse<ProductResponse> updateProduct(List<MultipartFile> files, ProductRequest productRequest, String productId) {
        Optional<Product> productOptional = productRepository.findByProductId(productId);

        if(productOptional.isEmpty())
            throw new ResourceNotFoundException(productId + " dosen't exist");

        Product product = productOptional.get();
        productMapper.dtoToEntity(productRequest, product);
        int indexFile = 0;

        for(MultipartFile file: files) {
            if(file == null)
                continue;

            ProductImage productImage = product.getImages().get(indexFile);
            byte[] fileBytesFromDb = fileUtil.getBytesFromFilePath(productImage.getFilePath());
            try {
                if(!fileUtil.compareEqualBytes(file.getBytes(), fileBytesFromDb)) {
                    productImage.setFilePath(fileUtil.saveProductImages(file, product.getProductName()));
                }
            } catch (IOException e) {
                throw new FileUploadException("Files isn't in the correct format");
            }

            indexFile++;
        }

        productRepository.save(product);
        return ResponseMapper.toDataResponseSuccess("Success");
    }

    public DataResponse<ProductResponse> getProductById(String productId) {
        Optional<Product> optionalProduct = productRepository.findByProductId(productId);

        if(optionalProduct.isEmpty())
            throw new ResourceNotFoundException(productId + " doesn't exist");

        Product product = optionalProduct.get();
        System.out.println(product);
        List<ProductImageDto> images = new ArrayList<>();

        for(ProductImage image: product.getImages()) {
            System.out.println("Vao day");
            images.add(new ProductImageDto(fileUtil.getFileBytes(image.getFilePath())));
        }

        ProductResponse response = productMapper.entityToDto(product);
        response.setImages(images);

        return ResponseMapper.toDataResponseSuccess(response);
    }
}
