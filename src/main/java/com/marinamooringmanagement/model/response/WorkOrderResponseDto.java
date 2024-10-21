package com.marinamooringmanagement.model.response;

import com.marinamooringmanagement.model.dto.metadata.WorkOrderPayStatusDto;
import com.marinamooringmanagement.model.dto.metadata.WorkOrderStatusDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOrderResponseDto implements Serializable {

    private static final long serialVersionUID = 5526863501379L;

    private Integer id;

    private String workOrderNumber;

    private String dueDate;

    private String scheduledDate;

    private String completedDate;

    private Time time;

    private String problem;

    private String reasonForDenial;

    private BigDecimal cost;

    private CustomerResponseDto customerResponseDto;

    private MooringResponseDto mooringResponseDto;

    private BoatyardResponseDto boatyardResponseDto;

    private UserResponseDto technicianUserResponseDto;

    private UserResponseDto financeUserResponseDto;

    private UserResponseDto customerOwnerUserResponseDto;

    private WorkOrderStatusDto workOrderStatusDto;

    private WorkOrderPayStatusDto workOrderPayStatusDto;

    private List<ImageResponseDto> imageResponseDtoList;

    private List<FormResponseDto> formResponseDtoList;

    private List<InventoryResponseDto> inventoryResponseDtoList;

    private List<VoiceMEMOResponseDto> voiceMEMOResponseDtoList;

}
