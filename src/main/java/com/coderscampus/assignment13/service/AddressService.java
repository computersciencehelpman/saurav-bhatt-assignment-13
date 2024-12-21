package com.coderscampus.assignment13.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderscampus.assignment13.domain.Address;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.repository.AddressRepository;

@Service
public class AddressService {
	
	private final AddressRepository addressRepo;
	private final UserService userService;	

    public AddressService(AddressRepository addressRepo, UserService userService) {
		super();
		this.addressRepo = addressRepo;
		this.userService = userService;
	}

	@Transactional
    public void updateAddress(Address updatedAddress) {
        if (updatedAddress.getUser() == null || updatedAddress.getUser().getUserId() == null) {
            throw new IllegalArgumentException("Address must be linked to a valid user.");
        }

        Long userId = updatedAddress.getUser().getUserId();
        User user = userService.findById(userId);

        Address existingAddress = user.getAddress();
        if (existingAddress == null) {
            updatedAddress.setUser(user);
            user.setAddress(updatedAddress);
            addressRepo.save(updatedAddress);
        } else {
            existingAddress.setAddressLine1(updatedAddress.getAddressLine1());
            existingAddress.setAddressLine2(updatedAddress.getAddressLine2());
            existingAddress.setCity(updatedAddress.getCity());
            existingAddress.setRegion(updatedAddress.getRegion());
            existingAddress.setZipCode(updatedAddress.getZipCode());
            existingAddress.setCountry(updatedAddress.getCountry());
            addressRepo.save(existingAddress);
        }
    }
	
	
}
