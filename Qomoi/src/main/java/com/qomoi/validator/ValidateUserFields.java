package com.qomoi.validator;

import com.qomoi.utility.Constants;
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

}

