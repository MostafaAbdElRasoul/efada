package com.efada.serviceImpl;

import java.io.IOException;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.efada.base.BaseServiceImpl;
import com.efada.dto.AppUserDTO;
import com.efada.entity.AppUser;
import com.efada.exception.EfadaCustomException;
import com.efada.repository.AppUserRepository;
import com.efada.utils.EfadaLogger;
import com.efada.utils.EfadaUtils;
import com.efada.utils.FileSystemUtils;
import com.efada.utils.ObjectMapperUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserServiceImpl extends BaseServiceImpl<AppUser, Long, AppUserDTO, AppUserRepository>{

	private final FileSystemUtils fileSystemUtils;	
	private final EfadaLogger efadaLogger;
	
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
			throw new EfadaCustomException("INVALID_FILE_TYPE");
		}
		catch (Exception ex) {
			efadaLogger.printStackTrace(ex, log);
			throw new EfadaCustomException("ERROR_DUE_TO_CHNAGNING_USER_PROFILE_IMAGE");
		}
		
		
	}

	public byte[] getUserProfileImgae(Long id) {
		AppUser user = baseRepository.findById(id).orElseThrow(
				()-> new NoSuchElementException("NO_VALUE_PRESENT"));
		try {			
			return fileSystemUtils.getFileBytes(user.getProfilePictureName());
		}catch (Exception ex) {
			efadaLogger.printStackTrace(ex, log);
			throw new EfadaCustomException("ERROR_DUE_TO_GETTING_USER_PROFILE_IMAGE");
		}
	}

	@Override
	public AppUserDTO save(AppUserDTO dto) {
		AppUser user = ObjectMapperUtils.map(dto, AppUser.class);

		if(baseRepository.existsByEmailOrUsername(user.getEmail(), user.getUsername()))
			throw new EfadaCustomException("EMAIL_OR_USERNAME_IS_ALREADY_EXISTS");		
		return super.save(dto);
	}

}
