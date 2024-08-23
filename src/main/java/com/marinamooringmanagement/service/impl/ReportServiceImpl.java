package com.marinamooringmanagement.service.impl;

import com.marinamooringmanagement.constants.AppConstants;
import com.marinamooringmanagement.model.entity.User;
import com.marinamooringmanagement.model.entity.WorkOrder;
import com.marinamooringmanagement.model.response.BasicRestResponse;
import com.marinamooringmanagement.model.response.report.JobLocationReportResponse;
import com.marinamooringmanagement.model.response.report.JobTypeReportResponse;
import com.marinamooringmanagement.repositories.WorkOrderRepository;
import com.marinamooringmanagement.security.util.AuthorizationUtil;
import com.marinamooringmanagement.service.ReportService;
import com.marinamooringmanagement.utils.MathUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private AuthorizationUtil authorizationUtil;

    @Override
    public BasicRestResponse getReportByJobType(HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<WorkOrder> completedWorkOrderList = workOrderRepository.findAllWorkOrderWithOnlyJobType(user.getId(), AppConstants.BooleanStringConst.YES);
            List<WorkOrder> openWorkOrderList = workOrderRepository.findAllWorkOrderWithOnlyJobType(user.getId(), AppConstants.BooleanStringConst.NO);

            Map<Pair<Integer, String>, Integer> jobTypeCnt = new HashMap<>();

            getJobTypeMap(completedWorkOrderList, jobTypeCnt);
            getJobTypeMap(openWorkOrderList, jobTypeCnt);

            int jobTypeLength = completedWorkOrderList.size()+openWorkOrderList.size();
            List<JobTypeReportResponse> jobTypeReportResponseList = new ArrayList<>();
            jobTypeCnt.forEach((jobType, frequency) -> {
                JobTypeReportResponse jobTypeReportResponse = JobTypeReportResponse.builder().build();
                jobTypeReportResponse.setId(jobType.getLeft());
                jobTypeReportResponse.setType(jobType.getRight());

                double freqPercentage = MathUtils.PercentageCalculator(frequency, jobTypeLength);
                jobTypeReportResponse.setPercentage(freqPercentage);

                jobTypeReportResponseList.add(jobTypeReportResponse);
            });

            response.setStatus(HttpStatus.OK.value());
            response.setContent(jobTypeReportResponseList);
            response.setTotalSize(jobTypeReportResponseList.size());
            response.setCurrentSize(jobTypeReportResponseList.size());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    @Override
    public BasicRestResponse getReportByJobLocation(final HttpServletRequest request) {
        BasicRestResponse response = BasicRestResponse.builder().build();
        response.setTime(new Timestamp(System.currentTimeMillis()));
        try {
            final Integer customerOwnerId = request.getIntHeader("CUSTOMER_OWNER_ID");
            final User user = authorizationUtil.checkAuthority(customerOwnerId);

            List<WorkOrder> completedWorkOrderList = workOrderRepository.findAllWorkOrderWithOnlyServiceArea(user.getId(), AppConstants.BooleanStringConst.YES);
            List<WorkOrder> openWorkOrderList = workOrderRepository.findAllWorkOrderWithOnlyServiceArea(user.getId(), AppConstants.BooleanStringConst.NO);

            Map<Pair<Integer, String>, Integer> jobTypeCnt = new HashMap<>();

            getJobTypeMap(completedWorkOrderList, jobTypeCnt);
            getJobTypeMap(openWorkOrderList, jobTypeCnt);

            int jobTypeLength = completedWorkOrderList.size()+openWorkOrderList.size();
            List<JobLocationReportResponse> jobLocationReportResponseList = new ArrayList<>();
            jobTypeCnt.forEach((jobLocation, frequency) -> {
                JobLocationReportResponse jobLocationReportResponse = JobLocationReportResponse.builder().build();
                jobLocationReportResponse.setId(jobLocation.getLeft());
                jobLocationReportResponse.setJobLocation(jobLocation.getRight());

                double freqPercentage = MathUtils.PercentageCalculator(frequency, jobTypeLength);
                jobLocationReportResponse.setPercentage(freqPercentage);

                jobLocationReportResponseList.add(jobLocationReportResponse);
            });

            response.setStatus(HttpStatus.OK.value());
            response.setContent(jobLocationReportResponseList);
            response.setTotalSize(jobLocationReportResponseList.size());
            response.setCurrentSize(jobLocationReportResponseList.size());

        } catch (Exception e) {
            response.setMessage(e.getLocalizedMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    private void getJobTypeMap(final List<WorkOrder> workOrderList, Map<Pair<Integer, String>, Integer> jobTypeCnt) {
        for(WorkOrder workOrder: workOrderList) {
            final Integer jpbTypeId = workOrder.getJobType().getId();
            final String jobType = workOrder.getJobType().getType();
            if(jobTypeCnt.containsKey(Pair.of(jpbTypeId, jobType))) {
                jobTypeCnt.put(Pair.of(jpbTypeId, jobType), jobTypeCnt.get(Pair.of(jpbTypeId, jobType))+1);
            } else {
                jobTypeCnt.put(Pair.of(jpbTypeId, jobType), 1);
            }
        }
    }
}
