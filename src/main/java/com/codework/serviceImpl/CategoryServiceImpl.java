package com.codework.serviceImpl;

import com.codework.exception.AlreadyExistException;
import com.codework.exception.ResourceNotFoundException;
import com.codework.model.Category;
import com.codework.repository.CategoryRepository;
import com.codework.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Category not found"));
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category).filter(e-> !categoryRepository.existsByName(e.getName()))
                .map(categoryRepository::save).orElseThrow(()->new AlreadyExistException(category.getName()+"already exist"));
    }

    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id)).map(oldCategory ->{
            oldCategory.setName(category.getName());
            return categoryRepository.save(oldCategory);
        }).orElseThrow(()-> new ResourceNotFoundException("Category not found"));
    }

    @Override
    public void deleteCategoryById(Long id) {

        categoryRepository.findById(id).ifPresentOrElse(categoryRepository::delete, ()->
        {
            throw new  ResourceNotFoundException("Category not Found.");
        });
    }
}
