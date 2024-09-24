package com.marinamooringmanagement.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.ipp.data.Deposit;
import com.intuit.ipp.data.Error;
import com.intuit.ipp.data.ReferenceType;
import com.intuit.ipp.exception.FMSException;
import com.intuit.ipp.exception.InvalidTokenException;
import com.intuit.ipp.services.DataService;
import com.intuit.ipp.services.QueryResult;
import com.intuit.oauth2.client.OAuth2PlatformClient;
import com.intuit.oauth2.data.BearerTokenResponse;
import com.intuit.oauth2.exception.OAuthException;
import com.marinamooringmanagement.client.OAuth2PlatformClientFactory;
import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.helper.QBOServiceHelper;
import com.marinamooringmanagement.mapper.PaymentMapper;
import com.marinamooringmanagement.model.entity.*;
import com.marinamooringmanagement.model.entity.QBO.QBOUser;
import com.marinamooringmanagement.model.entity.metadata.PaymentType;
import com.marinamooringmanagement.model.request.PaymentRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.PaymentRepository;
import com.marinamooringmanagement.repositories.QBO.QBOUserRepository;
import com.marinamooringmanagement.repositories.UserRepository;
import com.marinamooringmanagement.repositories.WorkOrderInvoiceRepository;
import com.marinamooringmanagement.repositories.metadata.PaymentTypeRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.security.util.LoggedInUserUtil;
import com.marinamooringmanagement.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentTypeRepository paymentTypeRepository;

    @Autowired
    private WorkOrderInvoiceRepository workOrderInvoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QBOServiceHelper helper;

    @Autowired
    private QBOUserRepository qboUserRepository;

    @Autowired
    OAuth2PlatformClientFactory factory;

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public BasicRestResponse savePayment(final PaymentRequestDto paymentRequestDto, final HttpServletRequest request, final Integer workOrderInvoiceId) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader(AppConstants.HeaderConstants.CUSTOMER_OWNER_ID);
            final User user;
            if (StringUtils.equals(LoggedInUserUtil.getLoggedInUserRole(), AppConstants.Role.FINANCE)) {
                final User financeUser = userRepository.findUserByIdWithoutImage(LoggedInUserUtil.getLoggedInUserID())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No finance user found with the given id: %1$s", LoggedInUserUtil.getLoggedInUserID())));

                user = userRepository.findUserByIdWithoutImage(financeUser.getCustomerOwnerId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No customer owner user found with the given id: %1$s", financeUser.getCustomerOwnerId())));
            } else {
                user = authorizationUtil.checkAuthority(customerOwnerId);
            }


            final WorkOrderInvoice workOrderInvoice = workOrderInvoiceRepository.findById(workOrderInvoiceId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order invoice found with the given id: %1$s", workOrderInvoiceId)));

            String quickBookCustomerIdStr = null;

            final WorkOrder workOrderInvoiceMappedWorkOrder = workOrderInvoice.getWorkOrder();
            if(null == workOrderInvoiceMappedWorkOrder) {
                final Mooring workOrderMappedMooring = workOrderInvoiceMappedWorkOrder.getMooring();
                if(null != workOrderMappedMooring) {
                    final Customer mooringMappedCustomer = workOrderMappedMooring.getCustomer();
                    if(null != mooringMappedCustomer) {
                        final QuickbookCustomer customerMappedQuickbookCustomer = mooringMappedCustomer.getQuickBookCustomer();
                        if(null != customerMappedQuickbookCustomer) {
                            quickBookCustomerIdStr = customerMappedQuickbookCustomer.getId().toString();
                        }
                    }
                }
            }

            if (null == quickBookCustomerIdStr)
                throw new RuntimeException(String.format("Work Order invoice with the id: %1$s is not connected with any Quickbook customer", workOrderInvoiceId));

            com.intuit.ipp.data.Payment payment = new com.intuit.ipp.data.Payment();

            // Set the customer reference
            ReferenceType customerRef = new ReferenceType();
            customerRef.setValue(quickBookCustomerIdStr);
            payment.setCustomerRef(customerRef);

            // Set the total amount
            payment.setTotalAmt(paymentRequestDto.getAmount());

            ReferenceType paymentMethodRef = new ReferenceType();
            paymentMethodRef.setValue(paymentRequestDto.getPaymentTypeId().toString());
            payment.setPaymentMethodRef(paymentMethodRef);

            final QBOUser qboUser = qboUserRepository.findQBOUserByEmail(user.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No QBO user found with the given email: %1$s", user.getEmail())));

            String realmId = qboUser.getRealmId();
            if (StringUtils.isEmpty(realmId)) {
                throw new RuntimeException("No realm ID.  QBO calls only work if the accounting scope was passed!");
            }
            String accessToken = qboUser.getAccessToken();

            // Save the payment using DataService
            try {
                DataService dataService = helper.getDataService(realmId, accessToken);

                com.intuit.ipp.data.Payment savedPayment = dataService.add(payment);

                response.setContent(savedPayment);
                response.setMessage("Payment saved successfully");
                response.setStatus(HttpStatus.OK.value());

            } /*
             * Handle 401 status code -
             * If a 401 response is received, refresh tokens should be used to get a new access token,
             * and the API call should be tried again.
             */ catch (InvalidTokenException e) {
                log.error("Error while calling executeQuery :: " + e.getMessage());

                //refresh tokens
                log.info("received 401 during companyinfo call, refreshing tokens now");
                OAuth2PlatformClient client = factory.getOAuth2PlatformClient();
                String refreshToken = qboUser.getRefreshToken();

                try {
                    BearerTokenResponse bearerTokenResponse = client.refreshToken(refreshToken);

                    qboUser.setLastModifiedDate(new Date(System.currentTimeMillis()));
                    qboUser.setAccessToken(bearerTokenResponse.getAccessToken());
                    qboUser.setRefreshToken(bearerTokenResponse.getRefreshToken());

                    qboUserRepository.save(qboUser);

                    //call company info again using new tokens
                    log.info("Saving payment using new token");
                    DataService dataService = helper.getDataService(realmId, accessToken);

                    com.intuit.ipp.data.Payment savedPayment = dataService.add(payment);

                    response.setContent(savedPayment);
                    response.setMessage("Payment saved successfully");
                    response.setStatus(HttpStatus.OK.value());
                } catch (OAuthException e1) {
                    log.error("Error while calling bearer token :: " + e.getMessage());
                    response.setMessage("Error while calling bearer token :: " + e.getMessage());
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                } catch (FMSException e1) {
                    log.error("Error while calling bearer token :: " + e.getMessage());
                    response.setMessage("Error while calling company currency :: " + e.getMessage());
                    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
            } catch (FMSException e) {
                List<Error> list = e.getErrorList();
                list.forEach(error -> log.error("Error while calling executeQuery :: " + error.getMessage()));
                response.setMessage("Error while calling executeQuery :: " + e.getMessage());
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage(e.getMessage());
        }
        return response;
    }

    private void performSave(final PaymentRequestDto paymentRequestDto, Payment payment, final Integer id, final HttpServletRequest request, final Integer workOrderInvoiceId) {
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            User user = authorizationUtil.checkAuthority(customerOwnerId);

            if (null == workOrderInvoiceId) throw new RuntimeException("No work order invoice Id provided");

            final WorkOrderInvoice workOrderInvoice = workOrderInvoiceRepository.findById(workOrderInvoiceId)
                    .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order invoice found with the given id: %1$s", workOrderInvoiceId)));

            if (null != workOrderInvoice.getCustomerOwnerUser() && !workOrderInvoice.getCustomerOwnerUser().getId().equals(user.getId()))
                throw new RuntimeException(String.format("Work order invoice with id %1$s is associated with some other customer owner"));
            if (null != payment.getCustomerOwnerUser() && !payment.getCustomerOwnerUser().getId().equals(user.getId()))
                throw new RuntimeException(String.format("Payment with id %1$s is associated with some other customer owner"));

            if (id == null) {
                payment.setCreationDate(new Date(System.currentTimeMillis()));
                payment.setCustomerOwnerUser(user);
            }

            payment.setLastModifiedDate(new Date(System.currentTimeMillis()));

            payment = paymentMapper.mapToEntity(payment, paymentRequestDto);

            if (null != paymentRequestDto.getPaymentTypeId()) {
                final PaymentType paymentType = paymentTypeRepository.findById(paymentRequestDto.getPaymentTypeId())
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No payment type found with the given id: %1$s", paymentRequestDto.getPaymentTypeId())));
                payment.setPaymentType(paymentType);
            }

            if (payment.getWorkOrderInvoice() == null || !payment.getWorkOrderInvoice().getId().equals(workOrderInvoiceId)) {
                if (payment.getWorkOrderInvoice() != null) {
                    Payment finalPayment = payment;
                    final WorkOrderInvoice prevWorkOrderInvoice = workOrderInvoiceRepository.findById(payment.getWorkOrderInvoice().getId())
                            .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order found with the id: %1$s", finalPayment.getWorkOrderInvoice().getId())));

                    if (id != null) {
                        prevWorkOrderInvoice.getPaymentList().removeIf(payment1 -> payment1.getId().equals(id));
                        workOrderInvoiceRepository.save(prevWorkOrderInvoice);
                    }
                }

                payment.setWorkOrderInvoice(workOrderInvoice);
                workOrderInvoice.getPaymentList().add(payment);
            }

            paymentRepository.save(payment);
            workOrderInvoiceRepository.save(workOrderInvoice);
        } catch (Exception e) {
            throw e;
        }
    }
}
