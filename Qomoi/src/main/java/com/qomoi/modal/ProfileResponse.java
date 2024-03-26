package com.qomoi.modal;

import com.qomoi.dto.ProfileDto;
import com.qomoi.entity.BillingAddress;
import lombok.Data;

import java.util.List;

@Data
public class ProfileResponse {
    private ProfileDto profileDto;
    private List<BillingAddress> billingAddressList;
}
