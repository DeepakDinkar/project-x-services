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

        if (!StringUtils.hasText(signUpRequestDTO.getEmail()))
            throw new MissingFieldException(Constants.EMAIL_ID_MANDATORY);

        if (!StringUtils.hasText(signUpRequestDTO.getPassword()))
            throw new MissingFieldException(Constants.PASSWORD_MANDATORY);
    }

    public void validateProfileUpdateFields(SignUpRequestDTO signUpRequestDTO) throws MissingFieldException {

        if (!StringUtils.hasText(signUpRequestDTO.getFirstName()))
            throw new MissingFieldException("FirstName is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getLastName()))
            throw new MissingFieldException("LastName is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getMobile()))
            throw new MissingFieldException("PhoneNumber is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getEmail()))
            throw new MissingFieldException("EmailId is Missing ");

        if (!StringUtils.hasText(signUpRequestDTO.getPassword()))
            throw new MissingFieldException("Password is Missing ");
    }

    public void validateLoginFields(LoginRequestDTO loginRequestDTO) throws MissingFieldException {

        if (!StringUtils.hasText(loginRequestDTO.getEmail()))
            throw new MissingFieldException(Constants.EMAIL_ID_MANDATORY);

        if (!StringUtils.hasText(loginRequestDTO.getPassword()))
            throw new MissingFieldException(Constants.PASSWORD_MANDATORY);
    }

}

