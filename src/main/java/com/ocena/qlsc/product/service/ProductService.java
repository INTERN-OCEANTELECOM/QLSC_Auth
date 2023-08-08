package com.ocena.qlsc.product.service;

import com.ocena.qlsc.common.dto.SearchKeywordDto;
import com.ocena.qlsc.common.model.BaseMapper;
import com.ocena.qlsc.common.repository.BaseRepository;
import com.ocena.qlsc.common.response.ListResponse;
import com.ocena.qlsc.common.response.ResponseMapper;
import com.ocena.qlsc.common.service.BaseServiceImpl;
import com.ocena.qlsc.common.util.StringUtil;
import com.ocena.qlsc.podetail.utils.FileExcelUtil;
import com.ocena.qlsc.product.dto.ProductDTO;
import com.ocena.qlsc.product.mapper.ProductMapper;
import com.ocena.qlsc.product.model.Product;
import com.ocena.qlsc.product.repository.ProductRepository;
import com.ocena.qlsc.user_history.mapper.HistoryMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class ProductService extends BaseServiceImpl<Product, ProductDTO> implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    FileExcelUtil processExcelFile;

    @Autowired
    HistoryMapper mapper;

    @Override
    protected BaseRepository<Product> getBaseRepository() {
        return productRepository;
    }

    @Override
    protected BaseMapper<Product, ProductDTO> getBaseMapper() {
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
    protected Page<ProductDTO> getPageResults(SearchKeywordDto searchKeywordDto, Pageable pageable) {
        List<String> listKeywords = StringUtil.splitStringToList(searchKeywordDto.getKeyword().get(0));

        getLogger().info("Test log search Product");

        Page<Object[]> resultPage = productRepository.getProductPageable(PageRequest.of(0, Integer.MAX_VALUE));
        List<ProductDTO> productDTOs = resultPage.getContent().stream().map(objects -> ProductDTO.builder()
                .productId(objects[0].toString())
                .productName(objects[1].toString())
                .amount(Integer.valueOf(objects[2].toString()))
                .build()).toList();

        try {
            if (!listKeywords.isEmpty()) {
                //Check if the first element of the list is of type Long
                Long.parseLong(listKeywords.get(0));
            }

            List<ProductDTO> mergeList = productDTOs.stream()
                    .filter(product -> listKeywords.isEmpty()
                            || listKeywords.stream()
                            .anyMatch(keyword -> product.getProductId().contains(keyword)))
                    .collect(Collectors.toList());

            return mergeListToPageProductDTO(mergeList, pageable);
        } catch (NumberFormatException e) {
            List<ProductDTO> mergeList =  productDTOs.stream().filter(productDTO -> productRepository.searchProduct(searchKeywordDto.getKeyword().get(0), pageable)
                    .map(product -> productMapper.entityToDto(product)).stream().anyMatch(productDTO1 -> productDTO1.getProductId().equals(productDTO.getProductId()))).collect(Collectors.toList());

            return mergeListToPageProductDTO(mergeList, pageable);
        }
    }

    @Override
    protected List<Product> getListSearchResults(String keyword) {
        return null;
    }

    public ListResponse<ProductDTO> getProductByPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Object[]> resultPage = productRepository.getProductPageable(pageable);
        Page<ProductDTO> productResponsePage = resultPage.map(objects -> ProductDTO.builder()
                .productId(objects[0].toString())
                .productName(objects[1].toString())
                .amount(Integer.valueOf(objects[2].toString()))
                .build());

        return ResponseMapper.toPagingResponseSuccess(productResponsePage);
    }

    public ListResponse<ProductDTO> getAllProduct() {
        List<ProductDTO> allProducts = getProductByPage(0, Integer.MAX_VALUE).getData() ;

        return ResponseMapper.toListResponseSuccess(allProducts);
    }

    public Page<ProductDTO> mergeListToPageProductDTO(List<ProductDTO> mergeList, Pageable pageable){
        List<ProductDTO> pageProducts = mergeList
                .subList(pageable.getPageNumber() * pageable.getPageSize(),
                        Math.min(pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize(), mergeList.size()));

        return new PageImpl<>(pageProducts, pageable, mergeList.size());
    }
}
