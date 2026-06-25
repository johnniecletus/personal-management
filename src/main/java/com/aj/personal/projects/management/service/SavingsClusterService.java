package com.aj.personal.projects.management.service;

import com.aj.personal.projects.management.dto.SavingsClusterDto;
import com.aj.personal.projects.management.dto.SavingsClusterRequestDto;
import java.util.List;

public interface SavingsClusterService {
    SavingsClusterDto previewCluster(SavingsClusterRequestDto request);

    SavingsClusterDto createCluster(SavingsClusterRequestDto request);

    SavingsClusterDto updateCluster(Long id, SavingsClusterRequestDto request);

    SavingsClusterDto getCluster(Long id);

    List<SavingsClusterDto> getAllClusters();

    void deleteCluster(Long id);
}
