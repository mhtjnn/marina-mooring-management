package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.ImageMapper;
import com.marinamooringmanagement.mapper.UserMapper;
import com.marinamooringmanagement.model.dto.ImageDto;
import com.marinamooringmanagement.model.dto.UserDto;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.request.ImageRequestDto;
import com.marinamooringmanagement.model.request.MultipleImageRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.ImageResponseDto;
import com.marinamooringmanagement.model.response.UserResponseDto;
import com.marinamooringmanagement.repositories.*;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.ImageService;
import com.marinamooringmanagement.utils.ImageUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private MooringRepository mooringRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ImageMapper imageMapper;

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public BasicRestResponse uploadImage(Integer entityId, String entity, MultipleImageRequestDto multipleImageRequestDto, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));

        try {
            if(StringUtils.equals(entity, AppConstants.EntityConstants.CUSTOMER)) {
                final Customer customer = customerRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No customer found with the given id: %1$s", entityId)));
                List<Image> imageList = uploadImagesToEntity(multipleImageRequestDto, (null != customer.getImageList()) ? customer.getImageList() : new ArrayList<>());
                customer.setImageList(imageList);
                customerRepository.save(customer);
            } else if(StringUtils.equals(entity, AppConstants.EntityConstants.WORK_ORDER)) {
                final WorkOrder workOrder = workOrderRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No work order found with the given id: %1$s", entityId)));
                List<Image> imageList = uploadImagesToEntity(multipleImageRequestDto, (null != workOrder.getImageList()) ? workOrder.getImageList() : new ArrayList<>());
                workOrder.setImageList(imageList);
                workOrderRepository.save(workOrder);
            } else if(StringUtils.equals(entity, AppConstants.EntityConstants.MOORING)) {
                final Mooring mooring = mooringRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No mooring found with the given id: %1$s", entityId)));
                List<Image> imageList = uploadImagesToEntity(multipleImageRequestDto, (null != mooring.getImageList()) ? mooring.getImageList() : new ArrayList<>());
                mooring.setImageList(imageList);
                mooringRepository.save(mooring);
            } else if(StringUtils.equals(entity, AppConstants.EntityConstants.USER)) {
                final User user = userRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No user found with the given id: %1$s", entityId)));
                final Image image = uploadImageToEntity(multipleImageRequestDto, (null == user.getImage()) ? Image.builder().build() : user.getImage());
                image.setUser(user);
                imageRepository.save(image);
                final UserDto userDto = userMapper.mapToUserDto(UserDto.builder().build(), user);
                final ImageDto imageDto = imageMapper.toDto(ImageDto.builder().build(), user.getImage());
                userDto.setImageDto(imageDto);
                response.setContent(userDto);
                response.setMessage(String.format("Image uploaded successfully for the user with id: %1$s", entityId));
                response.setStatus(HttpStatus.OK.value());
            } else {
                throw new RuntimeException(String.format("Given entity: %1$s is not authorized to have images"));
            }
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private Image uploadImageToEntity(MultipleImageRequestDto multipleImageRequestDto, Image image) throws IOException {
        try {
            List<ImageRequestDto> imageRequestDtoList = multipleImageRequestDto.getImageRequestDtoList();
            if(imageRequestDtoList.isEmpty()) throw new RuntimeException("No image to upload");
            if(imageRequestDtoList.size() > 1) throw new RuntimeException("Multiple images to upload");
            final ImageRequestDto imageRequestDto = imageRequestDtoList.get(0);
            if(null == imageRequestDto.getImageData()) throw new RuntimeException("No image data provided");

            byte[] data = ImageUtils.validateEncodedString(imageRequestDto.getImageData());

            image.setImageData(data);
        } catch (Exception e) {
            throw e;
        }

        return image;
    }

    @Override
    public BasicRestResponse fetchImages(Integer entityId, String entity, HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            List<ImageResponseDto> imageResponseDtoList = new ArrayList<>();
            List<Image> imageList;
            if(StringUtils.equals(entity, AppConstants.EntityConstants.CUSTOMER)) {
                final Customer customer = customerRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No customer found with the given id: %1$s", entityId)));
                if(null != customer.getImageList()) {
                    imageList = customer.getImageList();
                    imageResponseDtoList = mapToImageResponseDtoList(imageList);
                }
            } else if(StringUtils.equals(entity, AppConstants.EntityConstants.WORK_ORDER)) {
                final WorkOrder workOrder = workOrderRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No work order found with the given id: %1$s", entityId)));
                if(null != workOrder.getImageList()) {
                    imageList = workOrder.getImageList();
                    imageResponseDtoList = mapToImageResponseDtoList(imageList);
                }
            } else if(StringUtils.equals(entity, AppConstants.EntityConstants.MOORING)) {
                final Mooring mooring = mooringRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No mooring found with the given id: %1$s", entityId)));
                if(null != mooring.getImageList()) {
                    imageList = mooring.getImageList();
                    imageResponseDtoList = mapToImageResponseDtoList(imageList);
                }
            } else {
                throw new RuntimeException(String.format("Given entity: %1$s is not authorized to have images"));
            }

            response.setContent(imageResponseDtoList);
            response.setMessage("Images fetched successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    @Override
    public BasicRestResponse editImage(final Integer id, final String entity, final Integer entityId, final ImageRequestDto imageRequestDto, final HttpServletRequest request) {
        final BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            boolean found = false;
            if(StringUtils.equals(entity, AppConstants.EntityConstants.CUSTOMER)) {
                final Customer customer = customerRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No customer found with the given id: %1$s", entity)));
                for(Image image: customer.getImageList()) {
                    if(!ObjectUtils.notEqual(image.getId(), id)) {
                        found = true;
                        imageMapper.toEntity(image, imageRequestDto);
                    }
                }

                if(!found) throw new RuntimeException(String.format("No image found with id: %1$s of customer with id: %2$s", id, entityId));

                customerRepository.save(customer);

            } else if(StringUtils.equals(entity, AppConstants.EntityConstants.WORK_ORDER)) {
                final WorkOrder workOrder = workOrderRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No work order found with the given id: %1$s", entityId)));
                for(Image image: workOrder.getImageList()) {
                    if(!ObjectUtils.notEqual(image.getId(), id)) {
                        found = true;
                        imageMapper.toEntity(image, imageRequestDto);
                    }
                }

                if(!found) throw new RuntimeException(String.format("No image found with id: %1$s of work order with id: %2$s", id, entityId));

                workOrderRepository.save(workOrder);
            } else if(StringUtils.equals(entity, AppConstants.EntityConstants.MOORING)) {
                final Mooring mooring = mooringRepository.findById(entityId).orElseThrow(() -> new ResourceNotFoundException(String.format("No mooring found with the given id: %1$s", entityId)));

                for(Image image: mooring.getImageList()) {
                    if(!ObjectUtils.notEqual(image.getId(), id)) {
                        found = true;
                        imageMapper.toEntity(image, imageRequestDto);
                    }
                }

                if(!found) throw new RuntimeException(String.format("No image found with id: %1$s of mooring with id: %2$s", id, entityId));

                mooringRepository.save(mooring);
            } else {
                throw new RuntimeException(String.format("Given entity: %1$s is not authorized to have images"));
            }

            response.setMessage("Images edited successfully");
            response.setStatus(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return response;
    }

    private List<Image> uploadImagesToEntity(final MultipleImageRequestDto multipleImageRequestDto, List<Image> imageList) throws IOException {
        if(null != multipleImageRequestDto.getImageRequestDtoList() && !multipleImageRequestDto.getImageRequestDtoList().isEmpty()) {
            Integer imageNumber = 1;
            for(ImageRequestDto imageRequestDto: multipleImageRequestDto.getImageRequestDtoList()) {
                Image image = imageMapper.toEntity(Image.builder().build(), imageRequestDto);

                if(null == imageRequestDto.getImageName()) throw new RuntimeException(String.format("No name provided for image at number: %1$s", imageNumber));
                if(null == imageRequestDto.getImageData()) throw new RuntimeException(String.format("No image provided for: %1$s", imageRequestDto.getImageName()));

                image.setImageData(ImageUtils.validateEncodedString(imageRequestDto.getImageData()));
                image.setCreationDate(new Date(System.currentTimeMillis()));
                image.setLastModifiedDate(new Date(System.currentTimeMillis()));
                imageList.add(image);

                imageNumber++;
            }
        }
        return imageList;
    }

    private List<ImageResponseDto> mapToImageResponseDtoList(final List<Image> imageList) {
        List<ImageResponseDto> imageResponseDtoList = new ArrayList<>();
        for(Image image: imageList) {
            ImageResponseDto imageResponseDto = imageMapper.toResponseDto(ImageResponseDto.builder().build(), image);
            imageResponseDtoList.add(imageResponseDto);
        }
        return imageResponseDtoList;
    }
}
