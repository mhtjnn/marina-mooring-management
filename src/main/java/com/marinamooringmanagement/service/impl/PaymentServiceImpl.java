package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.exception.ResourceNotFoundException;
import com.marinamooringmanagement.mapper.PaymentMapper;
import com.marinamooringmanagement.model.entity.Payment;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.WorkOrderInvoice;
import com.marinamooringmanagement.model.entity.metadata.PaymentType;
import com.marinamooringmanagement.model.request.PaymentRequestDto;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.repositories.PaymentRepository;
import com.marinamooringmanagement.repositories.WorkOrderInvoiceRepository;
import com.marinamooringmanagement.repositories.metadata.PaymentTypeRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;

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

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public BasicRestResponse savePayment(final PaymentRequestDto paymentRequestDto, final HttpServletRequest request, final Integer workOrderInvoiceId) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Payment payment = Payment.builder().build();
            performSave(paymentRequestDto, payment, null, request, workOrderInvoiceId);
            log.info(String.format("Saving data in the database for payment ID %d", payment.getId()));
            response.setMessage("Payment Saved Successfully");
            response.setStatus(HttpStatus.CREATED.value());
        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void performSave(final PaymentRequestDto paymentRequestDto, Payment payment, final Integer id, final HttpServletRequest request, final Integer workOrderInvoiceId) {
        try {
            Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            User user = authorizationUtil.checkAuthority(customerOwnerId);

            if(null == workOrderInvoiceId) throw new RuntimeException("No work order invoice Id provided");

            final WorkOrderInvoice workOrderInvoice = workOrderInvoiceRepository.findById(workOrderInvoiceId)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format("No work order invoice found with the given id: %1$s", workOrderInvoiceId)));

            if(null != workOrderInvoice.getCustomerOwnerUser() && !workOrderInvoice.getCustomerOwnerUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Work order invoice with id %1$s is associated with some other customer owner"));
            if(null != payment.getCustomerOwnerUser() && !payment.getCustomerOwnerUser().getId().equals(user.getId())) throw new RuntimeException(String.format("Payment with id %1$s is associated with some other customer owner"));

            if(id == null) {
                payment.setCreationDate(new Date(System.currentTimeMillis()));
                payment.setCustomerOwnerUser(user);
            }

            payment.setLastModifiedDate(new Date(System.currentTimeMillis()));

            payment = paymentMapper.mapToEntity(payment, paymentRequestDto);

            if(null != paymentRequestDto.getPaymentTypeId()) {
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
