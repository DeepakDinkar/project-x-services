package com.qomoi.validator;

import com.qomoi.utility.Constants;
import com.qomoi.dto.LoginRequestDTO;
import com.qomoi.dto.SignUpRequestDTO;
import com.qomoi.exception.MissingFieldException;
import org.springframework.util.StringUtils;

public class ValidateUserFields {

    public void validateSignUpFields(SignUpRequestDTO signUpRequestDTO) throws MissingFieldException {

        if (!StringUtils.hasText(signUpRequestDTO.getFirstName()))
            throw new MissingFieldException(Constants.FIRSTNAME_MANDATORY);

        if (!StringUtils.hasText(signUpRequestDTO.getLastName()))
            throw new MissingFieldException(Constants.LASTNAME_MANDATORY);

        if (!StringUtils.hasText(signUpRequestDTO.getMobile()))
            throw new MissingFieldException(Constants.PHONE_NUMBER_MANDATORY);

        if (!StringUtils.hasText(signUpRequestDTO.getEmailId()))
            throw new MissingFieldException(Constants.EMAIL_ID_MANDATORY);

        if (!StringUtils.hasText(signUpRequestDTO.getPassword()))
            throw new MissingFieldException(Constants.PASSWORD_MANDATORY);

        if (!StringUtils.hasText(signUpRequestDTO.getUserType()))
            throw new MissingFieldException(Constants.USER_TYPE_MANDATORY);

//        if (!StringUtils.hasText(signUpRequestDTO.getStreet()))
//            throw new MissingFieldException(Constants.STREET_MANDATORY);
//
//        if (!StringUtils.hasText(signUpRequestDTO.getCity()))
//            throw new MissingFieldException(Constants.CITY_MANDATORY);
//
//        if (!StringUtils.hasText(signUpRequestDTO.getState()))
//            throw new MissingFieldException(Constants.STATE_MANDATORY);
//
//        if (!StringUtils.hasText(signUpRequestDTO.getPincode()))
//            throw new MissingFieldException(Constants.PINCODE_MANDATORY);
    }

    public void validateProfileUpdateFields(SignUpRequestDTO signUpRequestDTO) throws MissingFieldException {

        if (!StringUtils.hasText(signUpRequestDTO.getFirstName()))
            throw new MissingFieldException("FirstName is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getLastName()))
            throw new MissingFieldException("LastName is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getMobile()))
            throw new MissingFieldException("PhoneNumber is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getEmailId()))
            throw new MissingFieldException("EmailId is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getPassword()))
            throw new MissingFieldException("Password is Missing ");

//        if (!StringUtils.hasText(signUpRequestDTO.getStreet()))
//            throw new MissingFieldException("Street name is Missing ");
//
//        if (!StringUtils.hasText(signUpRequestDTO.getCity()))
//            throw new MissingFieldException("City is Missing ");
//
//        if (!StringUtils.hasText(signUpRequestDTO.getState()))
//            throw new MissingFieldException("State is Missing ");
//
//        if (!StringUtils.hasText(signUpRequestDTO.getPincode()))
//            throw new MissingFieldException("Pin code is Missing ");
    }

    public void validateLoginFields(LoginRequestDTO loginRequestDTO) throws MissingFieldException {

        if (!StringUtils.hasText(loginRequestDTO.getEmailId()))
            throw new MissingFieldException(Constants.EMAIL_ID_MANDATORY);

        if (!StringUtils.hasText(loginRequestDTO.getPassword()))
            throw new MissingFieldException(Constants.PASSWORD_MANDATORY);
    }

}

