// StandardReportEntryService.java
package com.PFE.DTT.service;

import com.PFE.DTT.dto.StandardChecklistItemDTO;
import com.PFE.DTT.dto.StandardReportEntryDTO;
import com.PFE.DTT.model.StandardReportEntry;
import com.PFE.DTT.model.User;
import com.PFE.DTT.repository.StandardReportEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StandardReportEntryService {

    private final StandardReportEntryRepository repository;

    public List<StandardChecklistItemDTO> getChecklistForUser(Long reportId, User user) {
        return repository.findByReportId(reportId).stream()
                .map(entry -> {
                    StandardChecklistItemDTO dto = new StandardChecklistItemDTO();
                    dto.setEntryId(entry.getId());
                    dto.setCriteriaId(entry.getStandardControlCriteria().getId());
                    dto.setCriteriaDescription(entry.getStandardControlCriteria().getDescription());
                    dto.setCheckResponsible(entry.getStandardControlCriteria().getCheckResponsible());
                    dto.setImplementationResponsible(entry.getStandardControlCriteria().getImplementationResponsible());
                    dto.setImplemented(entry.getImplemented());
                    dto.setAction(entry.getAction());
                    dto.setResponsableAction(entry.getResponsableAction());
                    dto.setDeadline(entry.getDeadline());
                    dto.setSuccessControl(entry.getSuccessControl());
                    dto.setUpdated(entry.isUpdated());

                    boolean isAssigned = entry.getReport().getAssignedUsers().stream()
                            .anyMatch(u -> u.getId().equals(user.getId()));
                    boolean isCheckResponsible = entry.getStandardControlCriteria().getCheckResponsible().getId() == user.getDepartment().getId();
                    boolean isCreator = entry.getReport().getCreatedBy().getId().equals(user.getId());

                    dto.setEditable(!entry.isUpdated() && ((isAssigned && isCheckResponsible) || isCreator));

                    return dto;
                })
                .collect(Collectors.toList());
    }


    public String updateEntry(Long entryId, StandardReportEntryDTO dto, User user) {
        Optional<StandardReportEntry> opt = repository.findById(entryId);
        if (opt.isEmpty()) return "Entry not found";

        StandardReportEntry entry = opt.get();

        if (entry.isUpdated()) return "Entry already updated";

        boolean isAssigned = entry.getReport().getAssignedUsers().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));
        boolean isCorrectDept = entry.getStandardControlCriteria().getCheckResponsible().getId() == user.getDepartment().getId();
        boolean isCreator = entry.getReport().getCreatedBy().getId().equals(user.getId());

        if (!(isAssigned && isCorrectDept) && !isCreator) return "Unauthorized";

        entry.setImplemented(dto.isImplemented());

        if (dto.isImplemented()) {
            entry.setAction(null);
            entry.setResponsableAction(null);
            entry.setDeadline(null);
            entry.setSuccessControl(null);
        } else {
            entry.setAction(dto.getAction());
            entry.setResponsableAction(dto.getResponsableAction());
            entry.setDeadline(dto.getDeadline());
            entry.setSuccessControl(dto.getSuccessControl());
        }

        entry.setUpdated(true);
        repository.save(entry);

        return "OK";
    }
} 