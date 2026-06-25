package com.aj.personal.projects.management.service.implementation;

import com.aj.personal.projects.management.dto.SavingsClusterDto;
import com.aj.personal.projects.management.dto.SavingsClusterItemDto;
import com.aj.personal.projects.management.dto.SavingsClusterItemRequestDto;
import com.aj.personal.projects.management.dto.SavingsClusterRequestDto;
import com.aj.personal.projects.management.entity.SavingsCluster;
import com.aj.personal.projects.management.entity.SavingsClusterItem;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.exception.BadRequestException;
import com.aj.personal.projects.management.exception.ResourceNotFoundException;
import com.aj.personal.projects.management.repository.IncomeRepository;
import com.aj.personal.projects.management.repository.SavingsClusterRepository;
import com.aj.personal.projects.management.service.AuthService;
import com.aj.personal.projects.management.service.SavingsClusterService;
import com.aj.personal.projects.management.service.support.SavingsClusterTemplateRegistry;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class SavingsClusterServiceImpl implements SavingsClusterService {

    private final SavingsClusterRepository savingsClusterRepository;
    private final IncomeRepository incomeRepository;
    private final AuthService authService;
    private final SavingsClusterTemplateRegistry templateRegistry;

    @Override
    public SavingsClusterDto previewCluster(SavingsClusterRequestDto request) {
        return buildClusterDto(
                null,
                request.getName().trim(),
                resolveItems(request.getName(), request.getItems())
        );
    }

    @Override
    public SavingsClusterDto createCluster(SavingsClusterRequestDto request) {
        User currentUser = authService.getCurrentUser();
        validateClusterNameUniqueness(currentUser.getId(), request.getName(), null);

        SavingsCluster cluster = new SavingsCluster(currentUser, request.getName().trim());
        applyItems(cluster, request.getName(), request.getItems());

        return mapCluster(savingsClusterRepository.save(cluster));
    }

    @Override
    public SavingsClusterDto updateCluster(Long id, SavingsClusterRequestDto request) {
        SavingsCluster cluster = getOwnedCluster(id);
        validateClusterNameUniqueness(cluster.getUser().getId(), request.getName(), cluster.getId());

        cluster.setName(request.getName().trim());

        if (request.getItems() != null) {
            applyItems(cluster, request.getName(), request.getItems());
        }

        return mapCluster(savingsClusterRepository.save(cluster));
    }

    @Override
    @Transactional(readOnly = true)
    public SavingsClusterDto getCluster(Long id) {
        return mapCluster(getOwnedCluster(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsClusterDto> getAllClusters() {
        User currentUser = authService.getCurrentUser();
        return savingsClusterRepository.findAllByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapCluster)
                .toList();
    }

    @Override
    public void deleteCluster(Long id) {
        SavingsCluster cluster = getOwnedCluster(id);

        if (incomeRepository.existsByClusterIdAndUserId(cluster.getId(), cluster.getUser().getId())) {
            throw new BadRequestException("Cluster cannot be deleted because it is already linked to income records");
        }

        savingsClusterRepository.delete(cluster);
    }

    private SavingsCluster getOwnedCluster(Long id) {
        User currentUser = authService.getCurrentUser();
        return savingsClusterRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Savings cluster not found with id " + id));
    }

    private void validateClusterNameUniqueness(Long userId, String name, Long currentClusterId) {
        savingsClusterRepository.findByUserIdAndNameIgnoreCase(userId, name.trim())
                .filter(existingCluster -> currentClusterId == null || !existingCluster.getId().equals(currentClusterId))
                .ifPresent(existingCluster -> {
                    throw new BadRequestException("Savings cluster " + name + " already exists");
                });
    }

    private void applyItems(
            SavingsCluster cluster,
            String clusterName,
            List<SavingsClusterItemRequestDto> requestItems
    ) {
        List<SavingsClusterItemDto> resolvedItems = resolveItems(clusterName, requestItems);
        cluster.getItems().clear();

        for (SavingsClusterItemDto resolvedItem : resolvedItems) {
            cluster.getItems().add(new SavingsClusterItem(
                    resolvedItem.getName(),
                    cluster,
                    resolvedItem.getPercentage()
            ));
        }
    }

    private List<SavingsClusterItemDto> resolveItems(
            String clusterName,
            List<SavingsClusterItemRequestDto> requestItems
    ) {
        if (requestItems == null || requestItems.isEmpty()) {
            return List.of();
        }

        Set<String> normalizedNames = new HashSet<>();
        int totalPercentage = 0;

        List<SavingsClusterItemDto> items = requestItems.stream()
                .map(requestItem -> {
                    String name = requestItem.getName().trim();
                    String normalizedName = normalize(name);

                    if (!normalizedNames.add(normalizedName)) {
                        throw new BadRequestException("Duplicate savings cluster item name: " + name);
                    }

                    Integer percentage = requestItem.getPercentage();
                    if (percentage == null) {
                        percentage = templateRegistry.resolveSuggestedPercentage(clusterName, name);
                    }
                    if (percentage == null) {
                        percentage = 0;
                    }

                    return SavingsClusterItemDto.builder()
                            .name(name)
                            .percentage(percentage)
                            .build();
                })
                .toList();

        for (SavingsClusterItemDto item : items) {
            if (item.getPercentage() < 0 || item.getPercentage() > 100) {
                throw new BadRequestException("Percentage for " + item.getName() + " must be between 0 and 100");
            }
            totalPercentage += item.getPercentage();
        }

        if (totalPercentage > 100) {
            throw new BadRequestException("The total savings cluster percentage cannot be greater than 100");
        }

        return items;
    }

    private SavingsClusterDto mapCluster(SavingsCluster cluster) {
        List<SavingsClusterItemDto> items = cluster.getItems()
                .stream()
                .map(item -> SavingsClusterItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .percentage(item.getPercentage())
                        .build())
                .toList();

        return buildClusterDto(cluster, cluster.getName(), items);
    }

    private SavingsClusterDto buildClusterDto(
            SavingsCluster cluster,
            String clusterName,
            List<SavingsClusterItemDto> items
    ) {
        int totalPercentage = items.stream()
                .map(SavingsClusterItemDto::getPercentage)
                .reduce(0, Integer::sum);

        return SavingsClusterDto.builder()
                .id(cluster == null ? null : cluster.getId())
                .name(clusterName)
                .totalPercentage(totalPercentage)
                .remainderPercentage(100 - totalPercentage)
                .items(items)
                .createdAt(cluster == null ? null : cluster.getCreatedAt())
                .updatedAt(cluster == null ? null : cluster.getUpdatedAt())
                .build();
    }

    private String normalize(String value) {
        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "");
    }
}
