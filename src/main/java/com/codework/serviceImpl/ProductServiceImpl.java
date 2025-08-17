package com.codework.serviceImpl;

import com.codework.exception.ProductNotFoundException;
import com.codework.model.Category;
import com.codework.model.Product;
import com.codework.repository.CategoryRepository;
import com.codework.repository.ProductRepository;
import com.codework.request.AddProductRequest;
import com.codework.request.ProductUpdateRequest;
import com.codework.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Product addProduct(AddProductRequest request) {
        // check if the category is found in the database
        // if yes, set it as the new product category
        // if not then save it as a new category
        // then set it as the new product category
        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
                .orElseGet(()-> {
            Category newCategory = new Category(request.getCategory().getName());
            return categoryRepository.save(newCategory);
        });
        request.setCategory(category);
        return productRepository.save(createProduct(request,category));

    }

    private Product createProduct(AddProductRequest request, Category category){
        return new Product(request.getName(),request.getBrand(),request.getPrice(),request.getInventory(),request.getDescription(),category);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(()->new ProductNotFoundException("Product not found!"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresentOrElse(productRepository::delete, ()->{throw  new ProductNotFoundException("Product not found.");});

    }

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {

        return productRepository.findById(productId)
                .map(existingProduct ->updateExistingProduct(existingProduct,request))
                .map(productRepository::save)
                .orElseThrow(()-> new ProductNotFoundException("Product not found."));



    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request){
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setCategory(request.getCategory());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());

        Category category = categoryRepository.findByName(request.getCategory().getName());
        return existingProduct;

    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category,brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand,name);
    }
}
