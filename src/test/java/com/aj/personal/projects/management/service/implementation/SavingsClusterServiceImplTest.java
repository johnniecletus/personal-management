package com.aj.personal.projects.management.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;

import com.aj.personal.projects.management.dto.SavingsClusterDto;
import com.aj.personal.projects.management.dto.SavingsClusterItemRequestDto;
import com.aj.personal.projects.management.dto.SavingsClusterRequestDto;
import com.aj.personal.projects.management.entity.User;
import com.aj.personal.projects.management.repository.UserRepository;
import com.aj.personal.projects.management.service.SavingsClusterService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class SavingsClusterServiceImplTest {

    @Autowired
    private SavingsClusterService savingsClusterService;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(
                "cluster-test@example.com",
                "Cluster Test User",
                "cluster-test-user",
                "encoded-password"
        ));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createClusterAppliesTemplatePercentagesAndTracksRemainder() {
        SavingsClusterRequestDto request = new SavingsClusterRequestDto(
                "salary",
                List.of(
                        new SavingsClusterItemRequestDto("emergency savings", null),
                        new SavingsClusterItemRequestDto("tithe", null),
                        new SavingsClusterItemRequestDto("main savings", 20)
                )
        );

        SavingsClusterDto cluster = savingsClusterService.createCluster(request);

        assertThat(cluster.getItems()).hasSize(3);
        assertThat(cluster.getItems().get(0).getPercentage()).isEqualTo(30);
        assertThat(cluster.getItems().get(1).getPercentage()).isEqualTo(10);
        assertThat(cluster.getItems().get(2).getPercentage()).isEqualTo(20);
        assertThat(cluster.getTotalPercentage()).isEqualTo(60);
        assertThat(cluster.getRemainderPercentage()).isEqualTo(40);
    }
}
