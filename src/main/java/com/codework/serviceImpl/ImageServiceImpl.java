package com.codework.serviceImpl;

import com.codework.dto.ImageDto;
import com.codework.exception.ResourceNotFoundException;
import com.codework.model.Image;
import com.codework.model.Product;
import com.codework.repository.ImageRepository;
import com.codework.service.ImageService;
import com.codework.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ProductService productService;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("No image found"));
    }

    @Override
    public void deleteImageById(Long id) {

        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, ()->{
            throw new ResourceNotFoundException("NO image found with this id: "+id);
        });
    }

    @Override
    public List<ImageDto> saveImage(List<MultipartFile> files, Long productId) {
        Product product = productService.getProductById(productId);
        List<ImageDto> savedImageDto = new ArrayList<>();
        for (MultipartFile file : files){
         try {
             Image image = new Image();
             image.setFileName(file.getOriginalFilename());
             image.setFileType(file.getContentType());
             image.setImage(new SerialBlob(file.getBytes()));
             image.setProduct(product);

             String buildDownloadUrl = "/api/v1/images/image/download/";

             String downloadUrl = buildDownloadUrl+image.getId();
             image.setDownloadUrl(downloadUrl);
            Image savedImage =  imageRepository.save(image);
            savedImage.setDownloadUrl(buildDownloadUrl +savedImage.getId());
            imageRepository.save(savedImage);

            ImageDto imageDto = new ImageDto();
            imageDto.setImageId(savedImage.getId());
            imageDto.setImageName(savedImage.getFileName());
            imageDto.setDownloadUrl(savedImage.getDownloadUrl());
            savedImageDto.add(imageDto);


         }catch (IOException | SQLException e){
             throw  new RuntimeException(e.getMessage());

         }
        }
        return savedImageDto;
    }

    @Override
    public void updateImage(MultipartFile file, Long imageId) {

        Image image = getImageById(imageId);
        try{
            image.setFileName(file.getOriginalFilename());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        }catch (IOException  | SQLException e){
            throw new RuntimeException(e.getMessage());
        }


    }
}
