package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class CategoryServiceImp implements CategoryService{

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize) {
        List<Category> categories= categoryRepository.findAll();
        if(categories.isEmpty()){
            throw new APIException("No Category Created till Now!!");
        }

        List<CategoryDTO> categoryDTOS= categories.stream()
                .map(category-> modelMapper.map(category, CategoryDTO.class))
                .toList();

//        // we can use this also which is long way but imp to learn
//        List<CategoryDTO> categoryDTOS= categories.stream()
//                .map(category-> {
//                    CategoryDTO categoryDTO= new CategoryDTO();
//                    categoryDTO.setCategoryId(category.getCategoryId());
//                    categoryDTO.setCategoryName(category.getCategoryName());
//                    return categoryDTO;
//                }).toList();

        CategoryResponse categoryResponse= new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        return categoryResponse;

//        return categories;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        // conversion of dto to entity
        Category category= modelMapper.map(categoryDTO, Category.class);

        Category categoryFromDB= categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDB!= null){
            throw new APIException("Category with the name "+ category.getCategoryName()+ " already exists!!");
        }

        categoryRepository.save(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category= categoryRepository.findById(categoryId)
                        .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));

        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        Category category= modelMapper.map(categoryDTO, Category.class);

        Category categoryFromDB= categoryRepository.findByCategoryName(category.getCategoryName());
        if(categoryFromDB!=null){
            throw new APIException("Category with the name "+ category.getCategoryName()+" already exists!!");
        }

        Category existingCategory= categoryRepository.findById(categoryId)
                        .orElseThrow(()-> new ResourceNotFoundException("Category", "categoryId", categoryId));

        existingCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(existingCategory);

        return modelMapper.map(existingCategory, CategoryDTO.class);
    }
}