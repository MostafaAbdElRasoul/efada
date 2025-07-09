package com.efada.serviceImpl;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.efada.base.BaseServiceImpl;
import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.exception.EfadaCustomException;
import com.efada.exception.EfadaValidationException;
import com.efada.repository.AppUserRepository;
import com.efada.utils.EfadaLogger;
import com.efada.utils.EfadaUtils;
import com.efada.utils.FileSystemUtils;
import com.efada.utils.ObjectMapperUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityManager;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AppUserServiceImpl extends BaseServiceImpl<AppUser, Long, AppUserDTO, AppUserRepository>{

	private FileSystemUtils fileSystemUtils;	
	private EfadaLogger efadaLogger;
	
	
	public AppUserServiceImpl(
            AppUserRepository baseRepository,
            EntityManager entityManager,
            EfadaLogger efadaLogger,
            FileSystemUtils fileSystemUtils
    ) {
        super(baseRepository, entityManager); 
        this.fileSystemUtils = fileSystemUtils;
        this.efadaLogger = efadaLogger;
    }
	
	@Override
	public AppUser getEntity() {
		// TODO Auto-generated method stub
		return new AppUser();
	}

	@Override
	public AppUserDTO getDTO() {
		// TODO Auto-generated method stub
		return new AppUserDTO();
	}

	public byte[] changeUserProfileImgae(MultipartFile file, Long id) {
		System.out.println("change "+baseRepository);
		AppUser user = baseRepository.findById(id).orElseThrow(
				()-> new NoSuchElementException("NO_VALUE_PRESENT"));
		try {
			if(user.getProfilePictureName() != null) {
				// remove the image
				fileSystemUtils.deleteFile(user.getProfilePictureName());	
			}
			
			String fileUniqueName="profile_"+user.getId()+"_"+"img"+fileSystemUtils.getFileExtension(file);
			user.setProfilePictureName(fileUniqueName);
			fileSystemUtils.saveToFileSystem(file, fileUniqueName);
			baseRepository.save(user);
			return fileSystemUtils.getFileBytes(fileUniqueName);
		}catch(IllegalArgumentException ex) {
			throw new EfadaValidationException("INVALID_FILE_TYPE");
		}
		catch (Exception ex) {
			efadaLogger.printStackTrace(ex, log);
			throw new EfadaValidationException("ERROR_DUE_TO_CHNAGNING_USER_PROFILE_IMAGE");
		}
		
		
	}

	public byte[] getUserProfileImgae(Long id) {
		AppUser user = baseRepository.findById(id).orElseThrow(
				()-> new NoSuchElementException("NO_VALUE_PRESENT"));
		try {
			System.out.println("profile "+user.getProfilePictureName());
			if(user.getProfilePictureName() == null)
				throw new EfadaValidationException("USER_HAS_NOT_PROFILE_IMAGE");
			return fileSystemUtils.getFileBytes(user.getProfilePictureName());
		}catch (EfadaValidationException ex) {
			efadaLogger.printStackTrace(ex, log);
			throw new EfadaValidationException("USER_HAS_NOT_PROFILE_IMAGE");
		}catch (Exception ex) {
			efadaLogger.printStackTrace(ex, log);
			throw new EfadaValidationException("ERROR_DUE_TO_GETTING_USER_PROFILE_IMAGE");
		}
	}

	@Override
	public AppUserDTO save(AppUserDTO dto) {
		AppUser user = ObjectMapperUtils.map(dto, AppUser.class);
		if(baseRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername()))
			throw new EfadaValidationException("EMAIL_OR_USERNAME_IS_ALREADY_EXISTS");		
		return super.save(dto);
	}

	@Override
	public AppUserDTO getById(Long id) {
		EfadaUtils.checkAuthorityForGetRequestAndDetails(id);
		return super.getById(id);
	}

	@Override
	public AppUserDTO getOne(Long id) {
		EfadaUtils.checkAuthorityForGetRequestAndDetails(id);
		return super.getOne(id);
	}

	@Override
	public void deleteById(Long id) {
		EfadaUtils.checkAuthorityForGetRequestAndDetails(id);
		super.deleteById(id);
	}

	@Override
	public AppUserDTO updateById(Long id, ObjectNode obj) {
		EfadaUtils.checkAuthorityForGetRequestAndDetails(id);
		return super.updateById(id, obj);
	}
	
}
