// SpecificReportEntryService.java
package com.PFE.DTT.service;

import com.PFE.DTT.dto.SpecificChecklistItemDTO;
import com.PFE.DTT.dto.SpecificReportEntryDTO;
import com.PFE.DTT.model.Department;
import com.PFE.DTT.model.SpecificReportEntry;
import com.PFE.DTT.model.User;
import com.PFE.DTT.repository.SpecificReportEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecificReportEntryService {

    private final SpecificReportEntryRepository repository;

    public List<SpecificChecklistItemDTO> getChecklistForUser(Long reportId, User user) {
        return repository.findByReportId(reportId).stream()
                .map(entry -> {
                    SpecificChecklistItemDTO dto = new SpecificChecklistItemDTO();
                    dto.setEntryId(entry.getId());
                    dto.setCriteriaId(entry.getSpecificControlCriteria().getId());
                    dto.setCriteriaDescription(entry.getSpecificControlCriteria().getDescription());

                    Set<Department> checkSet = entry.getSpecificControlCriteria().getCheckResponsibles();
                    Set<Department> implSet = entry.getSpecificControlCriteria().getImplementationResponsibles();
                    dto.setCheckResponsibles(checkSet.stream().collect(Collectors.toList()));
                    dto.setImplementationResponsibles(implSet.stream().collect(Collectors.toList()));

                    dto.setHomologation(entry.getHomologation());
                    dto.setAction(entry.getAction());
                    dto.setResponsableAction(entry.getResponsableAction());
                    dto.setDeadline(entry.getDeadline());
                    dto.setSuccessControl(entry.getSuccessControl());
                    dto.setUpdated(entry.isUpdated());

                    boolean isAssigned = entry.getReport().getAssignedUsers().contains(user);
                    boolean isCheckResponsible = entry.getSpecificControlCriteria().getCheckResponsibles().stream()
                            .anyMatch(dep -> dep.getId() == (user.getDepartment().getId()));
                    boolean isCreator = entry.getReport().getCreatedBy().getId().equals(user.getId());
                    boolean isEditable = !entry.isUpdated() && (isCreator || isAssigned) && isCheckResponsible;
                    dto.setEditable(isEditable);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public String updateEntry(int entryId, SpecificReportEntryDTO req, User user) {
        SpecificReportEntry entry = repository.findById(entryId).orElse(null);
        if (entry == null) return "Entry not found";
        if (entry.isUpdated()) return "This entry is already updated";

        boolean isAssigned = entry.getReport().getAssignedUsers().contains(user);
        boolean isCheckResponsible = entry.getSpecificControlCriteria().getCheckResponsibles().stream()
                .anyMatch(dep -> dep.getId() == (user.getDepartment().getId()));
        boolean isCreator = entry.getReport().getCreatedBy().getId().equals(user.getId());

        if (!(isAssigned || isCreator) || !isCheckResponsible) return "Unauthorized";

        entry.setHomologation(req.isHomologation());
        if (req.isHomologation()) {
            entry.setAction(null);
            entry.setResponsableAction(null);
            entry.setDeadline(null);
            entry.setSuccessControl(null);
        } else {
            entry.setAction(req.getAction());
            entry.setResponsableAction(req.getResponsableAction());
            entry.setDeadline(req.getDeadline());
            entry.setSuccessControl(req.getSuccessControl());
        }

        entry.setUpdated(true);
        repository.save(entry);
        return "OK";
    }
} 